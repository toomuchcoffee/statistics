package de.toomuchcoffee.statistics.service;

import static java.util.Comparator.comparing;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.PriorityQueue;

import org.springframework.stereotype.Service;

import de.toomuchcoffee.statistics.domain.Statistics;
import de.toomuchcoffee.statistics.domain.Transaction;

@Service
public class StatisticsService {
    private Statistics statistics = new Statistics();

    private PriorityQueue<Transaction> sortedTransactions = new PriorityQueue<>(comparing(Transaction::getTimestamp));
    private ArrayDeque<Double> sortedAmounts = new ArrayDeque<>();

    public synchronized boolean add(Transaction transaction) {
        if (isInTimeWindow(transaction)) {
            sortedTransactions.add(transaction);
            addToStatistics(transaction.getAmount());
            return true;
        }
        return false;
    }

    private boolean isInTimeWindow(final Transaction transaction) {
        return transaction.getTimestamp() >= Instant.now().toEpochMilli() - 60_000L;
    }

    public Statistics get() {
        removeOutdatedTransactions();
        return statistics;
    }

    private void addToStatistics(double amount) {
        increaseCount();
        findMinAndMaxAfterAdd(amount);
        calculateSumAndAvgAfterAdd(amount);
    }

    private void removeFromStatistics(final Transaction transaction) {
        decreaseCount();
        findMinAndMaxAfterRemove(transaction.getAmount());
        calculateSumAndAvgAfterRemove(transaction.getAmount());
    }

    private void removeOutdatedTransactions() {
        if (sortedTransactions.isEmpty()) {
            return;
        }
        Transaction transaction = sortedTransactions.peek();

        while (transaction != null && !isInTimeWindow(transaction)) {
            sortedTransactions.poll();

            removeFromStatistics(transaction);

            transaction = sortedTransactions.isEmpty() ? null : sortedTransactions.peek();
        }
    }

    private void calculateSumAndAvgAfterAdd(double amount) {
        statistics.setSum(statistics.getSum() + amount);
        if (statistics.getAvg() == null) {
            statistics.setAvg(amount);
        } else {
            statistics.setAvg((statistics.getSum()) / statistics.getCount());
        }
    }

    private void calculateSumAndAvgAfterRemove(double amount) {
        statistics.setSum(statistics.getSum() - amount);
        if (statistics.getCount() == 0) {
            statistics.setAvg(null);
        } else {
            statistics.setAvg((statistics.getSum()) / statistics.getCount());
        }
    }

    private void findMinAndMaxAfterAdd(double amount) {
        if (statistics.getMin() == null && statistics.getMax() == null) {
            statistics.setMin(amount);
            statistics.setMax(amount);
            sortedAmounts.add(amount);
        } else if (amount < statistics.getMin()) {
            statistics.setMin(amount);
            sortedAmounts.addFirst(amount);
        } else if (amount > statistics.getMax()) {
            statistics.setMax(amount);
            sortedAmounts.addLast(amount);
        }
    }

    private void findMinAndMaxAfterRemove(double amount) {
        if (statistics.getCount() == 0) {
            statistics.setMin(null);
            statistics.setMax(null);
            sortedAmounts.remove();
        } else if (amount == statistics.getMin()) {
            sortedAmounts.removeFirst();
            statistics.setMax(sortedAmounts.getFirst());
        } else if (amount == statistics.getMax()) {
            sortedAmounts.removeLast();
            statistics.setMax(sortedAmounts.getLast());
        }
    }

    private void increaseCount() {
        statistics.setCount(statistics.getCount() + 1);
    }

    private void decreaseCount() {
        statistics.setCount(statistics.getCount() - 1);
    }

}
