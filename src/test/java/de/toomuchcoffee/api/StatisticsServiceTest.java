package de.toomuchcoffee.api;


import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Instant;

import org.awaitility.Duration;
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
        statisticsService.add(createTransaction(1000d, Instant.now().toEpochMilli() - 59_500));

        Statistics expected = new Statistics();
        expected.setCount(2);
        expected.setAvg(50d);
        expected.setMax(99d);
        expected.setMin(1d);
        expected.setSum(100d);

        await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Statistics statistics = statisticsService.get();
                    assertThat(statistics).isEqualToComparingFieldByField(expected);
                });

    }

    @Test
    public void getStatisticsCalculatesEmptyStatisticsWhenAllTransactionsAreOlderThan60sec() {
        statisticsService.add(createTransaction(1d, Instant.now().toEpochMilli() - 59_500));
        statisticsService.add(createTransaction(99d, Instant.now().toEpochMilli() - 59_500));
        statisticsService.add(createTransaction(1000d, Instant.now().toEpochMilli() - 59_500));

        Statistics expected = new Statistics();
        expected.setCount(0);
        expected.setAvg(null);
        expected.setMax(null);
        expected.setMin(null);
        expected.setSum(0);

        await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Statistics statistics = statisticsService.get();
                    assertThat(statistics).isEqualToComparingFieldByField(expected);
                });

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