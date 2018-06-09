package de.toomuchcoffee.statistics.service;


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

import de.toomuchcoffee.statistics.domain.Statistics;
import de.toomuchcoffee.statistics.domain.Transaction;

@RunWith(MockitoJUnitRunner.class)
public class StatisticsServiceTest {

    private StatisticsService statisticsService = new StatisticsService();

    @Test
    public void addTransactionAddsToCount() {
        Transaction transaction = new Transaction(0, Instant.now().toEpochMilli());

        assertThat(statisticsService.get().getCount()).isEqualTo(0);

        statisticsService.add(transaction);

        assertThat(statisticsService.get().getCount()).isEqualTo(1);
    }

    @Test
    public void addTransactionDoesNotAddToCountIfOlderThan60sec() {
        long now = Instant.now().toEpochMilli();
        long longAgo = now - 60_001L;

        Transaction transaction = new Transaction(0, longAgo);

        assertThat(statisticsService.get().getCount()).isEqualTo(0);

        statisticsService.add(transaction);

        assertThat(statisticsService.get().getCount()).isEqualTo(0);
    }

    @Test
    public void getStatisticsCalculatesStatisticsFromOneExistingTransaction() {
        Transaction transaction = createTransaction(1_234.56);
        statisticsService.add(transaction);

        Statistics statistics = statisticsService.get();

        Statistics expected = new Statistics(1, 1_234.56, 1_234.56, 1_234.56, 1_234.56);

        assertThat(statistics).isEqualToComparingFieldByField(expected);

    }

    @Test
    public void getStatisticsCalculatesStatisticsFromTwoExistingTransaction() {
        statisticsService.add(createTransaction(50d));
        statisticsService.add(createTransaction(1d));
        statisticsService.add(createTransaction(99d));

        Statistics statistics = statisticsService.get();

        Statistics expected = new Statistics(3, 150d, 1d, 99d, 50d);

        assertThat(statistics).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getStatisticsCalculatesStatisticsOnlyFromTransactionsOfLast60sec() {
        statisticsService.add(createTransaction(1d));
        statisticsService.add(createTransaction(99d));
        statisticsService.add(new Transaction(1000d, Instant.now().toEpochMilli() - 59_500));

        Statistics expected = new Statistics(2, 100d, 1d, 99d, 50d);

        await()
                .atMost(Duration.ONE_SECOND)
                .untilAsserted(() -> {
                    Statistics statistics = statisticsService.get();
                    assertThat(statistics).isEqualToComparingFieldByField(expected);
                });

    }

    @Test
    public void getStatisticsCalculatesEmptyStatisticsWhenAllTransactionsAreOlderThan60sec() {
        statisticsService.add(new Transaction(1d, Instant.now().toEpochMilli() - 59_500));
        statisticsService.add(new Transaction(99d, Instant.now().toEpochMilli() - 59_500));
        statisticsService.add(new Transaction(1000d, Instant.now().toEpochMilli() - 59_500));

        Statistics expected = new Statistics(0, 0, 0, 0, 0);

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

        Statistics expected = new Statistics(
                10_000,
                transactions.stream().mapToDouble(Transaction::getAmount).sum(),
                0d,
                9_999d,
                transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble()
        );

        Statistics statistics = statisticsService.get();
        assertThat(statistics).isEqualToComparingFieldByField(expected);
    }

    @Test
    public void getStatisticsCalculatesStatististicsConcurrentlyForLargeNumberOfTransactions() {
        List<Transaction> transactions = IntStream.range(0, 10_000)
                .mapToObj(this::createTransaction)
                .collect(toList());

        transactions.forEach(t -> new Thread(() -> statisticsService.add(t)).start());

        Statistics expected = new Statistics(
                10_000,
                transactions.stream().mapToDouble(Transaction::getAmount).sum(),
                0d,
                9_999d,
                transactions.stream().mapToDouble(Transaction::getAmount).average().getAsDouble()
        );

        Statistics statistics = statisticsService.get();
        assertThat(statistics).isEqualToComparingFieldByField(expected);
    }

    private Transaction createTransaction(double amount) {
        return new Transaction(amount, Instant.now().toEpochMilli());
    }

}