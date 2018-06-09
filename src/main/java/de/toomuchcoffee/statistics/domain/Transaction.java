package de.toomuchcoffee.statistics.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Transaction {
    private final double amount;
    private final long timestamp;
}
