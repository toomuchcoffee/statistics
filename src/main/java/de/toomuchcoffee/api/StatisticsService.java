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
        statistics.setCount(transactions.size());
        return statistics;
    }
}
