package de.toomuchcoffee.api;


import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

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

        Statistics expected = new Statistics();
        expected.setCount(1);
        expected.setAvg(1_234.56);
        expected.setMax(1_234.56);
        expected.setMin(1_234.56);
        expected.setSum(1_234.56);

        assertThat(statistics).isEqualToComparingFieldByField(expected);

    }

    @Test
    public void getStatisticsCalculatesStatisticsFromTwoExistingTransaction() {
        statisticsService.add(createTransaction(50d));
        statisticsService.add(createTransaction(1d));
        statisticsService.add(createTransaction(99d));

        Statistics statistics = statisticsService.get();

        Statistics expected = new Statistics();
        expected.setCount(3);
        expected.setAvg(50d);
        expected.setMax(99d);
        expected.setMin(1d);
        expected.setSum(150d);

        assertThat(statistics).isEqualToComparingFieldByField(expected);
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

    @Test
    public void getStatisticsCalculatesStatististicsForLargeNumberOfTransactions() {
        List<Transaction> transactions = IntStream.range(0, 10_000)
                .mapToObj(this::createTransaction)
                .collect(toList());

        transactions.forEach(statisticsService::add);

        Statistics expected = new Statistics();
        expected.setCount(10_000);
        expected.setMin(0d);
        expected.setMax(9_999d);
        expected.setAvg(transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble());
        expected.setSum(transactions.stream().mapToDouble(Transaction::getAmount).sum());

        Statistics statistics = statisticsService.get();
        assertThat(statistics).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getStatisticsCalculatesStatististicsConcurrentlyForLargeNumberOfTransactions() {
        List<Transaction> transactions = IntStream.range(0, 10_000)
                .mapToObj(this::createTransaction)
                .collect(toList());

        transactions.forEach(t -> new Thread(() -> statisticsService.add(t)).start());

        Statistics expected = new Statistics();
        expected.setCount(10_000);
        expected.setMin(0d);
        expected.setMax(9_999d);
        expected.setAvg(transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble());
        expected.setSum(transactions.stream().mapToDouble(Transaction::getAmount).sum());

        Statistics statistics = statisticsService.get();
        assertThat(statistics).isEqualToComparingFieldByField(expected);
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