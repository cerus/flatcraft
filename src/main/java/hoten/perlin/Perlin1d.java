package hoten.perlin;

import hoten.perlin.interpolator.CosineInterpolator;
import hoten.perlin.interpolator.Interpolator;
import java.math.BigInteger;
import java.util.Random;

/**
 * Perlin1d.java
 *
 * @author Hoten
 * <p>
 * Source: https://github.com/connorjclark/noisy
 */
public class Perlin1d {

    private final Interpolator interpolator;
    private int p1;
    private int p2;
    private int p3;
    private final int seed;
    private final int octaves;
    private final double persistence;
    private int[] p1s, p2s, p3s;//primes

    public Perlin1d(final double persistence, final int octaves, final int seed, final Interpolator interpolator) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        this.interpolator = interpolator;
        this.setPrimes(seed);
    }

    public Perlin1d(final double persistence, final int octaves, final int seed) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        this.interpolator = new CosineInterpolator();
        this.setPrimes(seed);
    }

    //returns a noise array which loops seamlessly
    //see http://webstaff.itn.liu.se/~stegu/TNM022-2005/perlinnoiselinks/perlin-noise-math-faq.html
    public double[] tile(final double[] noise) {
        //treat noise as a function with domain -t to t, where t = noise.length / 2
        //i.e. noise[0] = f(-t), noise[noise.length-1] = f(t)
        final int t = noise.length / 2;
        final double[] tiled = new double[t];
        for (int z = 0; z < t; z++) {
            tiled[z] = ((t - z) * noise[z + t] + (z) * noise[z]) / (t);
        }
        return tiled;
    }

    public double[] createTiledArray(final int size) {
        final double[] tiledNoise = this.tile(this.createRawArray(size * 2));
        this.clamp(tiledNoise);
        return tiledNoise;
    }

    public double[] createArray(final int size) {
        final double[] noise = this.createRawArray(size);
        this.clamp(noise);
        return noise;
    }

    //not clamped
    private double[] createRawArray(final int size) {
        final double[] y = new double[size];
        final int regionWidth = 3;
        final int smallSeed = Math.abs(this.seed / 1000);
        for (int i = 0; i < size; i++) {
            final double nx = 1.0 * i / size * regionWidth;
            y[i] = this.perlinNoise1(smallSeed + nx);
        }
        return y;
    }

    //stretched range of the noise from 0.0 to 1.0
    private void clamp(final double[] noise) {
        final int size = noise.length;
        double max = -1, min = 1;
        for (int i = 0; i < size; i++) {
            max = Math.max(max, noise[i]);
            min = Math.min(min, noise[i]);
        }
        final double range = max - min;
        for (int i = 0; i < size; i++) {
            noise[i] = (noise[i] - min) / range;
        }
    }

    /**
     * Generates three random primes for each octave. These numbers are used in
     * the noise function, and contribute to it's quasi-nondeterministic nature.
     */
    private void setPrimes(final int seed) {
        this.p1s = new int[this.octaves];
        this.p2s = new int[this.octaves];
        this.p3s = new int[this.octaves];
        for (int i = 0; i < this.octaves; ++i) {
            final Random ran = new Random(i + seed);
            this.p1s[i] = BigInteger.probablePrime(23, ran).intValue();
            this.p2s[i] = BigInteger.probablePrime(24, ran).intValue();
            this.p3s[i] = BigInteger.probablePrime(25, ran).intValue();
        }
    }

    private double perlinNoise1(final double x) {
        double total = 0;
        int f = 1;
        double a = 1;
        for (int i = 0; i < this.octaves; ++i) {
            //set this octave's primes. this saves on array lookups
            this.p1 = this.p1s[i];
            this.p2 = this.p2s[i];
            this.p3 = this.p3s[i];
            total += this.interpolatedNoise1(x * f) * a;
            f *= 2;
            a *= this.persistence;
        }
        return total;
    }

    private double noise1(int x) {
        x = (x << 13) ^ x;
        return (1.0 - ((x * (x * x * this.p1 + this.p2) + this.p3) & 0x7fffffff) / 1073741824.0);
    }

    private double smoothedNoise1(final int x) {
        return this.noise1(x) / 2 + this.noise1(x - 1) / 4 + this.noise1(x + 1) / 4;
    }

    private double interpolatedNoise1(final double x) {
        final int intX = (int) x;
        final double fractionalX = x - intX;

        final double v1 = this.smoothedNoise1(intX);
        final double v2 = this.smoothedNoise1(intX + 1);

        return this.interpolate(v1, v2, fractionalX);
    }

    private double interpolate(final double v1, final double v2, final double fractionalX) {
        return this.interpolator.interpolate(v1, v2, fractionalX);
    }
}
