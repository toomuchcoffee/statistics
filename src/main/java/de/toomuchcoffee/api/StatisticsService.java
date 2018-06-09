package de.toomuchcoffee.api;

import static java.util.Comparator.comparing;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private Statistics statistics = new Statistics();

    private List<Transaction> transactions = new ArrayList<>();
    private ArrayDeque<Double> sortedAmounts = new ArrayDeque<>();

    public boolean add(Transaction transaction) {
        if (isInTimeWindow(transaction)) {
            transactions.add(transaction);
            transactions.sort(comparing(Transaction::getTimestamp));
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
        increaseSum(amount);
        findMinAndMaxAfterAdd(amount);
        calculateAvgAfterAdd(amount);
    }

    private void removeFromStatistics(final Transaction transaction) {
        decreaseCount();
        decreaseSum(transaction.getAmount());
        findMinAndMaxAfterRemove(transaction.getAmount());
        calculateAvgAfterRemove(transaction.getAmount());
    }

    private void removeOutdatedTransactions() {
        if (transactions.isEmpty()) {
            return;
        }
        Transaction transaction = transactions.get(0);
        while (transaction != null && !isInTimeWindow(transaction)) {
            transactions.remove(0);

            removeFromStatistics(transaction);

            transaction = transactions.isEmpty() ? null : transactions.get(0);
        }
    }

    private void calculateAvgAfterAdd(double amount) {
        if (statistics.getAvg() == null) {
            statistics.setAvg(amount);
        } else {
            statistics.setAvg((statistics.getAvg() * (statistics.getCount() - 1) + amount) / statistics.getCount());
        }
    }

    private void calculateAvgAfterRemove(double amount) {
        if (statistics.getCount() == 0) {
            statistics.setAvg(null);
        } else {
            statistics.setAvg((statistics.getAvg() * (statistics.getCount() + 1) - amount) / statistics.getCount());
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

    private void increaseSum(double amount) {
        statistics.setSum(statistics.getSum() + amount);
    }

    private void decreaseSum(double amount) {
        statistics.setSum(statistics.getSum() - amount);
    }

    private void increaseCount() {
        statistics.setCount(statistics.getCount() + 1);
    }

    private void decreaseCount() {
        statistics.setCount(statistics.getCount() - 1);
    }

}
