package de.toomuchcoffee.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private List<Transaction> transactions = new ArrayList<>();

    public void add(Transaction transaction) {
        transactions.add(transaction);
    }

    public Statistics get() {
        Statistics statistics = new Statistics();
        statistics.setCount(transactions.size());
        return statistics;
    }
}
