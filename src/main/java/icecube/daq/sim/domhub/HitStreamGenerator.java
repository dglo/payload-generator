/*
 * class: HitStreamGenerator
 *
 * Version $Id: HitStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Random;

/**
 * This class generates generic hit records.
 *
 * @version $Id: HitStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class HitStreamGenerator
        implements IStreamGenerator
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(HitStreamGenerator.class);

    /**
     * Default start time.
     */
    private static final int DEFAULT_START_TIME_UTC = 0;

    /**
     * Default beacon rate, in Hz.
     */
    private static final double DEFAULT_BEACON_RATE = 5.0;

    /**
     * Default spe rate, in Hz.
     */
    private static final double DEFAULT_SPE_RATE = 20.0;

    /**
     * Default random number seed.
     */
    private static final long DEFAULT_SEED = 0;

    /**
     * Start time for this stream.
     */
    private final int startTimeUtc;

    /**
     * Beacon rate.
     */
    private final double beaconRate;

    /**
     * Spe rate.
     */
    private final double speRate;

    /**
     * The next beacon hit.
     */
    private IGenericHitRecord nextBeaconHit;

    /**
     * The next spe hit.
     */
    private IGenericHitRecord nextSpeHit;

    /**
     * Random number generator.
     */
    private Random random;

    /**
     * Default constructor, uses defaults for startTimeUtc, beaconRate, speRate, and random number seed.
     */
    public HitStreamGenerator() {
        this(DEFAULT_START_TIME_UTC, DEFAULT_BEACON_RATE, DEFAULT_SPE_RATE, DEFAULT_SEED);
    }

    /**
     * Constructor that takes beacon rate and spe rate and uses default for startTimeUtc and random number seed.
     *
     * @param beaconRate beacon rate to use (in Hz)
     * @param speRate spe rate to use (in Hz)
     */
    public HitStreamGenerator(double beaconRate, double speRate) {
        this(DEFAULT_START_TIME_UTC, beaconRate, speRate, DEFAULT_SEED);
    }

    /**
     * Constructor that takes startTimeUtc, beacon rate, spe rate, and random number seed.
     *
     * @param startTimeUtc Start time of stream (in s)
     * @param beaconRate beacon rate to use (in Hz)
     * @param speRate spe rate to use (in Hz)
     * @param seed random number seed
     */
    public HitStreamGenerator(int startTimeUtc, double beaconRate, double speRate, long seed) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing HitStreamGenerator with\n"
                          + "  startTimeUtc = " + startTimeUtc + " s\n"
                          + "    beaconRate = " + beaconRate + " Hz\n"
                          + "       speRate = " + speRate + " Hz\n"
                          + "          seed = " + seed;
            log.info(dump);
        }

        this.startTimeUtc = startTimeUtc;
        this.beaconRate = beaconRate;
        this.speRate = speRate;

        random = new Random(seed);
        nextBeaconHit = getNextBeaconHit();
        nextSpeHit = getNextSpeHit();
    }

    /**
     * Get the next hit record in this stream.
     *
     * @return next IGenericRecord
     */
    public IGenericRecord nextRecord() {
        IGenericHitRecord next;
        if ((nextBeaconHit == null) && (nextSpeHit == null)) {
            next = null;
        } else if (nextBeaconHit == null) {
            next = nextSpeHit;
            if (log.isDebugEnabled()) {
                log.debug("Generating a SPE Hit: " + next.getUtcTime());
            }
            nextSpeHit = getNextSpeHit();
        } else if (nextSpeHit == null) {
            next = nextBeaconHit;
            if (log.isDebugEnabled()) {
                log.debug("Generating a Beacon Hit: " + next.getUtcTime());
            }
            nextBeaconHit = getNextBeaconHit();
        } else {
            if (nextBeaconHit.compareTo(nextSpeHit) < 0) {
                next = nextBeaconHit;
                if (log.isDebugEnabled()) {
                    log.debug("Generating a Beacon Hit: " + next.getUtcTime());
                }
                nextBeaconHit = getNextBeaconHit();
            } else {
                next = nextSpeHit;
                if (log.isDebugEnabled()) {
                    log.debug("Generating a SPE Hit: " + next.getUtcTime());
                }
                nextSpeHit = getNextSpeHit();
            }
        }
        return next;
    }

    /**
     * Produce the next beacon hit.
     * The time is the last time plus one over the beacon rate.
     * (time is in 1/10 ns)
     *
     * @return IGenericHitRecord for next beacon hit
     */
    private IGenericHitRecord getNextBeaconHit() {
        if (beaconRate <= 0.0) return null;
        long lastTime;
        if (nextBeaconHit == null) {
            lastTime = (long) (startTimeUtc*1.0e10);
        } else {
            lastTime = nextBeaconHit.getUtcTime();
        }
        long nextTime = (long) (lastTime + 1.0e10/beaconRate);
        return new GenericHitRecord(nextTime, IGenericHitRecord.BEACON_HIT);
    }

    /**
     * Produce the next spe hit.
     * The time is the last time plus a random (exponential) waiting time.
     * (time is in 1/10 ns)
     *
     * @return IGenericHitRecord for next spe hit
     */
    private IGenericHitRecord getNextSpeHit() {
        if (speRate <= 0.0) return null;
        long lastTime;
        if (nextSpeHit == null) {
            lastTime = (long) (startTimeUtc*1.0e10);
        } else {
            lastTime = nextSpeHit.getUtcTime();
        }
        double point;
        do {
            point = random.nextDouble();
        } while (point <= 0.0);
        long nextTime = (long) (lastTime - 1.0e10*Math.log(point)/speRate);
        return new GenericHitRecord(nextTime, IGenericHitRecord.SPE_HIT);
    }

}
