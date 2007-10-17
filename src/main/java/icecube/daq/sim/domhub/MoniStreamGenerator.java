/*
 * class: TcalStreamGenerator
 *
 * Version $Id: MoniStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generates generic moni records.
 *
 * @version $Id: MoniStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class MoniStreamGenerator
        implements IStreamGenerator
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(MoniStreamGenerator.class);

    /**
     * Default start time.
     */
    private static final int DEFAULT_START_TIME_UTC = 0;

    /**
     * Default moni rate, in Hz.
     */
    private static final double DEFAULT_MONI_RATE = 1.0;

    /**
     * Default supernova rate, in Hz.
     */
    private static final double DEFAULT_SUPERNOVA_RATE = 1.0;

    /**
     * Start time for this stream.
     */
    private final int startTimeUtc;

    /**
     * Moni rate.
     */
    private final double moniRate;

    /**
     * Supernova rate.
     */
    private final double supernovaRate;

    /**
     * The next moni.
     */
    private IGenericMoniRecord nextMoni = null;

    /**
     * The next supernova
     */
    private IGenericMoniRecord nextSupernova = null;

    /**
     * Default constructor, uses default startTimeUtc and moniRate.
     */
    public MoniStreamGenerator() {
        this(DEFAULT_START_TIME_UTC, DEFAULT_MONI_RATE, DEFAULT_SUPERNOVA_RATE);
    }

    /**
     * Constructor that takes moniRate and uses default startTimeUtc.
     *
     * @param moniRate moni rate to use (in Hz)
     */
    public MoniStreamGenerator(double moniRate, double supernovaRate) {
        this(DEFAULT_START_TIME_UTC, moniRate, supernovaRate);
    }

    /**
     * Constructor that takes startTimeUtc and moni rate.
     *
     * @param startTimeUtc Start time of stream (in s)
     * @param moniRate moni rate to use (in Hz)
     */
    public MoniStreamGenerator(int startTimeUtc, double moniRate, double supernovaRate) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing MoniStreamGenerator with\n"
                          + "  startTimeUtc = " + startTimeUtc + " s\n"
                          + "      moniRate = " + moniRate + " Hz"
                          + " supernovaRate = " + supernovaRate + " Hz";
            log.info(dump);
        }
        this.startTimeUtc = startTimeUtc;
        this.moniRate = moniRate;
        this.supernovaRate = supernovaRate;

        nextMoni = getNextMoniRecord();
        nextSupernova = getNextSupernovaRecord();
    }

    /**
     * Get the next record in this stream.
     * The time is the last time plus one over the moni rate.
     * (time is in 1/10 ns)
     *
     * @return next IGenericRecord
     */
    public IGenericRecord nextRecord() {
        IGenericMoniRecord next;
        if ((nextMoni == null) && (nextSupernova == null)) {
            next = null;
        } else if (nextMoni == null) {
            next = nextSupernova;
            if (log.isDebugEnabled()) {
                log.debug("Generating a Supernova record: " + next.getUtcTime());
            }
            nextSupernova = getNextSupernovaRecord();
        } else if (nextSupernova == null) {
            next = nextMoni;
            if (log.isDebugEnabled()) {
                log.debug("Generating a Monitor record: " + next.getUtcTime());
            }
            nextMoni = getNextMoniRecord();
        } else {
            if (nextMoni.compareTo(nextSupernova) < 0) {
                next = nextMoni;
                if (log.isDebugEnabled()) {
                    log.debug("Generating a Monitor record: " + next.getUtcTime());
                }
                nextMoni = getNextMoniRecord();
            } else {
                next = nextSupernova;
                if (log.isDebugEnabled()) {
                    log.debug("Generating a Supernova record: " + next.getUtcTime());
                }
                nextSupernova = getNextSupernovaRecord();
            }
        }
        return next;
    }

    /**
     * Produce the next moni record.
     * The time is the last time plus one over the moni rate.
     * (time is in 1/10 ns)
     *
     * @return IGenericMoniRecord for next moni record
     */
    private IGenericMoniRecord getNextMoniRecord() {
        if (moniRate <= 0.0) return null;
        long lastTime;
        if (nextMoni == null) {
            lastTime = (long) (startTimeUtc*1.0e10);
        } else {
            lastTime = nextMoni.getUtcTime();
        }
        long nextTime = (long) (lastTime + 1.0e10/moniRate);
        return new GenericMoniRecord(nextTime, IGenericMoniRecord.HARDWARE_STATE_EVENT);
    }

    /**
     * Produce the next supernova record.
     * The time is the last time plus one over the sn rate.
     * (time is in 1/10 ns)
     *
     * @return IGenericMoniRecord for next sn record
     */
    private IGenericMoniRecord getNextSupernovaRecord() {
        if (supernovaRate <= 0.0) return null;
        long lastTime;
        if (nextSupernova == null) {
            lastTime = (long) (startTimeUtc*1.0e10);
        } else {
            lastTime = nextSupernova.getUtcTime();
        }
        long nextTime = (long) (lastTime + 1.0e10/supernovaRate);
        return new GenericMoniRecord(nextTime, IGenericMoniRecord.SUPERNOVA_RECORD);
    }

}
