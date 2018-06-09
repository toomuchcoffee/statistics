package de.toomuchcoffee.api;

public class Statistics {
    private double sum;
    private Double avg;
    private Double max;
    private Double min;
    private long count;

    public double getSum() {
        return sum;
    }

    public void setSum(final double sum) {
        this.sum = sum;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(final Double avg) {
        this.avg = avg;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(final Double max) {
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public void setMin(final Double min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(final long count) {
        this.count = count;
    }
}
