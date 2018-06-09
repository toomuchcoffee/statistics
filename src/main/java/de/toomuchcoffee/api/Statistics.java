package de.toomuchcoffee.api;

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
    private Double min;
    private Double max;
    private Double avg;
}
