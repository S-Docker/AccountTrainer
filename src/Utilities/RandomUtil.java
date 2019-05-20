package Utilities;

import java.util.Random;

public class RandomUtil {

    private Random random;

    public RandomUtil() {
        this(new Random());
    }

    public RandomUtil(Random random) {
        super();
        this.random = random;
    }

    /**
     * Generates a random integer with normal distribution using the supplied
     * deviation and mean with a cap based on the 68–95–99.7 rule
     *
     * @param stdDeviation
     * @param mean
     * @return integer A random integer with normal distribution using the
     *         supplied deviation and mean capped based on the 68–95–99.7 rule
     */
    public int gRandom(int stdDeviation, int mean) {
        return Math.toIntExact(Math.round(gRandom((double) stdDeviation, (double) mean)));
    }

    /**
     * Generates a random integer with normal distribution using the supplied
     * deviation and mean with a custom cap
     *
     * @param stdDeviation
     * @param mean
     * @param minCap
     * @param maxCap
     * @return integer A random integer with normal distribution using the
     *         supplied deviation and mean capped by supplied values
     */
    public int gRandom(int stdDeviation, int mean, int minCap, int maxCap) {
        return Math.toIntExact(
                Math.round(gRandom((double) stdDeviation, (double) mean, (double) minCap, (double) maxCap)));
    }

    /**
     * Generates a random double with normal distribution using the supplied
     * deviation and mean with a custom cap
     *
     * @param stdDeviation
     * @param mean
     * @param minCap
     * @param maxCap
     * @return double A random double with normal distribution using the
     *         supplied deviation and mean capped by supplied values
     */
    public double gRandom(double stdDeviation, double mean, double minCap, double maxCap) {
        double result;
        do {
            result = random.nextGaussian() * stdDeviation + mean;
        } while (result < minCap || result > maxCap);

        return result;
    }

    /**
     * Generates a random double with normal distribution using the supplied
     * deviation and mean with a cap based on the 68–95–99.7 rule
     *
     * @param stdDeviation
     * @param mean
     * @return double A random double with normal distribution using the
     *         supplied deviation and mean capped based on the 68–95–99.7 rule
     */
    public double gRandom(double stdDeviation, double mean) {
        double max = stdDeviation * 3; // 68–95–99.7 rule

        double result;
        do {
            result = random.nextGaussian() * stdDeviation + mean;
        } while (Math.abs(result - mean) > max);

        return result;
    }

	/**
    * Returns a random integer based on normal distribution between two integers
	﻿ *
     * @param min
	﻿ * Minimum integer (inclusive)
	 * @param max
	 * Maximum integer (inclusive)
	 * @return An integer between min and max﻿﻿ (inclusive)
	 */
    public int gRandomBetween(int min, int max) {
        return Math.toIntExact(Math.round(gRandomBetween((double) min, (double) max)));
    }

    /**
     * Returns a random double based on normal distribution between two doubles
     *
     * @param min
     *            The minimum double (inclusive)
     * @param max
     *            The maximum double (inclusive)
     * @return A double between min and max (inclusive)
     */
    public double gRandomBetween(double min, double max) {
        if (max - min == 0) {
            return 0;
        } else if (max - min < 0) {
            min += (max - (max = min));
        }

        double deviation = (max - min) / 6d;
        double mean = (max - min) / 2d + min;

        return gRandom(deviation, mean);
    }

    public Random getRandom() {
        return random;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

}