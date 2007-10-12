/*
 * class: TcalStreamGenerator
 *
 * Version $Id: TcalStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class generates generic hit records.
 *
 * @version $Id: TcalStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class TcalStreamGenerator
        implements IStreamGenerator
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(TcalStreamGenerator.class);

    /**
     * Default start time.
     */
    private static final int DEFAULT_START_TIME_UTC = 0;

    /**
     * Default tcal rate, in Hz.
     */
    private static final double DEFAULT_TCAL_RATE = 0.1;

    /**
     * Start time for this stream.
     */
    private final int startTimeUtc;

    /**
     * Tcal rate.
     */
    private final double tcalRate;

    /**
     * The next tcal.
     */
    private IGenericTcalRecord nextTcal;

    /**
     * Default constructor, uses default startTimeUtc and tcalRate.
     */
    public TcalStreamGenerator() {
        this(DEFAULT_START_TIME_UTC, DEFAULT_TCAL_RATE);
    }

    /**
     * Constructor that takes tcalRate and uses default startTimeUtc.
     *
     * @param tcalRate tcal rate to use (in Hz)
     */
    public TcalStreamGenerator(double tcalRate) {
        this(DEFAULT_START_TIME_UTC, tcalRate);
    }

    /**
     * Constructor that takes startTimeUtc and tcal rate.
     *
     * @param startTimeUtc Start time of stream (in s)
     * @param tcalRate tcal rate to use (in Hz)
     */
    public TcalStreamGenerator(int startTimeUtc, double tcalRate) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing TcalStreamGenerator with\n"
                          + "  startTimeUtc = " + startTimeUtc + " s\n"
                          + "      tcalRate = " + tcalRate + " Hz";
            log.info(dump);
        }
        this.startTimeUtc = startTimeUtc;
        this.tcalRate = tcalRate;
    }

    /**
     * Get the next record in this stream.
     * The time is the last time plus one over the tcal rate.
     * (time is in 1/10 ns)
     *
     * @return next IGenericRecord
     */
    public IGenericRecord nextRecord() {
        if (tcalRate <= 0.0) return null;
        long lastTime;
        if (nextTcal == null) {
            lastTime = (long) (startTimeUtc*1.0e10);
        } else {
            lastTime = nextTcal.getUtcTime();
        }
        long nextTime = (long) (lastTime + 1.0e10/tcalRate);
        nextTcal = new GenericTcalRecord(nextTime);
        if (log.isDebugEnabled()) {
            log.debug("Generating a Tcal: " + nextTcal.getUtcTime());
        }
        return nextTcal;
    }

}
