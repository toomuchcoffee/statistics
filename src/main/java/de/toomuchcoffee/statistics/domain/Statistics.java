package de.toomuchcoffee.statistics.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
    private long count;
    private double sum;
    private double min;
    private double max;
    private double avg;
}
