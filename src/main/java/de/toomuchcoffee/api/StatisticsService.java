package de.toomuchcoffee.api;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private List<Transaction> transactions = new ArrayList<>();

    public boolean add(Transaction transaction) {
        if (transaction.getTimestamp() >= Instant.now().toEpochMilli() - 60_000L) {
            transactions.add(transaction);
            return true;
        }
        return false;
    }

    public Statistics get() {
        Statistics statistics = new Statistics();

        Double min = null;
        Double max = null;

        double sum = 0d;

        for (Transaction transaction : transactions) {
            if (min == null || transaction.getAmount() < min) {
                min = transaction.getAmount();
            }
            if (max == null || transaction.getAmount() > max) {
                max = transaction.getAmount();
            }
            sum += transaction.getAmount();
        }

        double avg = transactions.isEmpty() ? 0 : sum / transactions.size();

        statistics.setMin(min);
        statistics.setMax(max);
        statistics.setAvg(avg);
        statistics.setSum(sum);
        statistics.setCount(transactions.size());
        return statistics;
    }
}
