/*
 * class: TimeGenerator
 *
 * Version $Id: TimeGenerator.java,v 1.3 2005/10/25 21:02:26 toale Exp $
 *
 * Date: March 1 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.util.Random;

/**
 * This class generates Poisson distributed waiting times based on an event rate
 *
 * @version $Id: TimeGenerator.java,v 1.3 2005/10/25 21:02:26 toale Exp $
 * @author pat
 */
public class TimeGenerator
{

    /**
     * default rate
     */
    private static final double DEFAULT_RATE = 1.0;

    /**
     * poisson rate
     */
    private double rate;

    /**
     * Random number generator.
     */
    Random random = null;

    /**
     * default constructor
     */
    public TimeGenerator() {
        this(DEFAULT_RATE);
    }

    /**
     * constructor taking a rate
     * @param rate poisson rate
     */
    public TimeGenerator(double rate) {
        this.rate = rate;
        random = new Random();
    }

    public TimeGenerator(double rate, long seed) {
        this.rate = rate;
        random = new Random(seed);
    }

    public TimeGenerator(double rate, Random random) {
        this.rate = rate;
        this.random = random;
    }

    /**
     * getter for rate
     * @return rate
     */
    public double getRate() {
        return rate;
    }

    /**
     * generate the next waiting time
     * @return poisson waiting time in units of 1/[rate]
     */
    public double nextTime() {

        double point;
        do {
            point = random.nextDouble();
        } while (point <= 0.0);

        return (-Math.log(point)/rate);

    }

    /**
     * setter for rate
     * @param rate rate to use
     */
    public void setRate(double rate) {
        this.rate = rate;
    }

}
