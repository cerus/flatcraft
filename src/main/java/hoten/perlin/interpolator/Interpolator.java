package hoten.perlin.interpolator;

/**
 * Interpolator.java
 *
 * @author Hoten
 * <p>
 * Source: https://github.com/connorjclark/noisy
 */
public interface Interpolator {

    public double interpolate(double a, double b, double fractional);
}
