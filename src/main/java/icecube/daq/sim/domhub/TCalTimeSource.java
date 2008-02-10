/*
 * class: TCalTimeSource
 *
 * Version $Id: TCalTimeSource.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: August 14 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class provides time stamps in units of 1/10 ns at a fixed frequency
 * modulated by a guassian smearing.
 *
 * @version $Id: TCalTimeSource.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class TCalTimeSource
{

    /**
     * Log object for this class.
     */
    private static final Log log = LogFactory.getLog(TCalTimeSource.class);

    /**
     * Default time between records, in seconds
     */
    private static final double DEFAULT_WAIT_TIME = 1.0;

    /**
     * Default Guassian smearing of time, in seconds
     */
    private static final double DEFAULT_TIME_SMEAR = 0.0;

    /**
     * Random number generator
     */
    private Random random = new Random();

    /**
     * Average time between records
     */
    private double waitTime;

    /**
     * Deviation of the wait time from the average
     */
    private double timeSmear;

    /**
     * Default constructor.
     */
    public TCalTimeSource() {
        this(DEFAULT_WAIT_TIME, DEFAULT_TIME_SMEAR);
    }

    /**
     * Constructor that takes a wait time and a time smear.
     * @param waitTime Average time between records, in seconds
     * @param timeSmear Time smear of record, also in seconds
     */
    public TCalTimeSource(double waitTime, double timeSmear) {
        this.waitTime = waitTime;
        this.timeSmear = timeSmear;

        if (log.isInfoEnabled()) {
            log.info("Constructing a TCalTimeSource with:\n"
                     + "    Wait time  = " + this.waitTime + "\n"
                     + "    Time smear = " + this.timeSmear);
        }

    }

    /**
     * Generate a random waiting time in units of 1/10 ns
     * based on a Gaussian distribution with a mean given
     * by waitTime and a sigma given by timeSmear (both in
     * units of seconds).
     * @return a waiting time in units of 1/10 ns
     */
    public long nextTime() {
        double time = 1e10*waitTime;
        if (0 != timeSmear) {
            time += 1e10*timeSmear*random.nextGaussian();
        }
        return ((long) time);
    }

    /**
     * Reset the random number generator with a seed.
     * @param seed random number generator seed
     */
    public void setRandomSeed(long seed) {
        random = new Random(seed);
    }

    public void setTimeSmear(double timeSmear) {
        this.timeSmear = timeSmear;
    }

    public void setWaitTime(double waitTime) {
        this.waitTime = waitTime;
    }

}
