package de.toomuchcoffee.api;


import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    private StatisticsService statisticsService = new StatisticsService();

    @Test
    public void addTransactionAddsToCount() {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(Instant.now().toEpochMilli());

        assertThat(statisticsService.get().getCount()).isEqualTo(0);

        statisticsService.add(transaction);

        assertThat(statisticsService.get().getCount()).isEqualTo(1);
    }

    @Test
    public void addTransactionDoesNotAddToCountIfOlderThan60sec() {
        long now = Instant.now().toEpochMilli();
        long longAgo = now - 60_001L;

        Transaction transaction = new Transaction();
        transaction.setTimestamp(longAgo);

        assertThat(statisticsService.get().getCount()).isEqualTo(0);

        statisticsService.add(transaction);

        assertThat(statisticsService.get().getCount()).isEqualTo(0);
    }

    @Test
    public void getStatisticsCalculatesStatisticsFromOneExistingTransaction() {
        Transaction transaction = createTransaction(1_234.56);
        statisticsService.add(transaction);

        Statistics statistics = statisticsService.get();

        assertThat(statistics.getCount()).isEqualTo(1);
        assertThat(statistics.getAvg()).isEqualTo(1_234.56);
        assertThat(statistics.getMax()).isEqualTo(1_234.56);
        assertThat(statistics.getMin()).isEqualTo(1_234.56);
        assertThat(statistics.getSum()).isEqualTo(1_234.56);
    }

    @Test
    public void getStatisticsCalculatesStatisticsFromTwoExistingTransaction() {
        statisticsService.add(createTransaction(1d));
        statisticsService.add(createTransaction(99d));

        Statistics statistics = statisticsService.get();

        assertThat(statistics.getCount()).isEqualTo(2);
        assertThat(statistics.getAvg()).isEqualTo(50d);
        assertThat(statistics.getMax()).isEqualTo(99d);
        assertThat(statistics.getMin()).isEqualTo(1d);
        assertThat(statistics.getSum()).isEqualTo(100d);
    }

    @Test
    public void getStatisticsCalculatesStatisticsOnlyFromTransactionsOfLast60sec() {
        statisticsService.add(createTransaction(1d));
        statisticsService.add(createTransaction(99d));
        statisticsService.add(createTransaction(1000d, Instant.now().toEpochMilli() - 70_000));

        Statistics statistics = statisticsService.get();

        assertThat(statistics.getCount()).isEqualTo(2);
        assertThat(statistics.getAvg()).isEqualTo(50d);
        assertThat(statistics.getMax()).isEqualTo(99d);
        assertThat(statistics.getMin()).isEqualTo(1d);
        assertThat(statistics.getSum()).isEqualTo(100d);
    }

    private Transaction createTransaction(double amount) {
        return createTransaction(amount, Instant.now().toEpochMilli());
    }

    private Transaction createTransaction(double amount, long timestamp) {
        Transaction transaction = new Transaction();
        transaction.setTimestamp(timestamp);
        transaction.setAmount(amount);
        return transaction;
    }
}