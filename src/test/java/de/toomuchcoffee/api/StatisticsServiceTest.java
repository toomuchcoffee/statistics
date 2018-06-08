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
}