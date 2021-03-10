package hoten.perlin.interpolator;

/**
 * CosineInterpolator.java
 *
 * @author Hoten
 * <p>
 * Source: https://github.com/connorjclark/noisy
 */
public class CosineInterpolator implements Interpolator {

    @Override
    public double interpolate(final double a, final double b, final double fractional) {
        final double ft = fractional * 3.1415927;
        final double f = (1 - Math.cos(ft)) * .5;
        return a * (1 - f) + b * f;
    }
}
