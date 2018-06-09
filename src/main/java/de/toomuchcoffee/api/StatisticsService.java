package de.toomuchcoffee.api;

import static java.util.stream.Collectors.toList;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private List<Transaction> transactions = new ArrayList<>();

    public boolean add(Transaction transaction) {
        if (isInTimeWindow(transaction)) {
            transactions.add(transaction);
            return true;
        }
        return false;
    }

    private boolean isInTimeWindow(final Transaction transaction) {
        return transaction.getTimestamp() >= Instant.now().toEpochMilli() - 60_000L;
    }

    public Statistics get() {

        Double min = null;
        Double max = null;

        double sum = 0d;


        List<Transaction> transactionsInWindow = transactions.stream().filter(this::isInTimeWindow).collect(toList());
        for (Transaction transaction : transactionsInWindow) {
            if (min == null || transaction.getAmount() < min) {
                min = transaction.getAmount();
            }
            if (max == null || transaction.getAmount() > max) {
                max = transaction.getAmount();
            }
            sum += transaction.getAmount();
        }

        double avg = transactionsInWindow.isEmpty() ? 0 : sum / transactionsInWindow.size();

        Statistics statistics = new Statistics();
        statistics.setMin(min);
        statistics.setMax(max);
        statistics.setAvg(avg);
        statistics.setSum(sum);
        statistics.setCount(transactionsInWindow.size());
        return statistics;
    }
}
