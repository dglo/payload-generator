/*
 * class: RandomSource
 *
 * Version $Id: RandomSource.java,v 1.6 2005/11/07 18:05:07 dglo Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.SourceIdRegistry;

import java.util.Random;

/**
 * This class produces GenericHits with random time stamps and dom ids
 *
 * @version $Id: RandomSource.java,v 1.6 2005/11/07 18:05:07 dglo Exp $
 * @author pat
 */
public class RandomSource
        implements ISource
{

    private static final double RATE_DEFAULT        = 0.0001;   // 1 kHz in units of 1/10 ns
    private static final int    NDOMS_DEFAULT       = 60;
    private static final int    SOURCEID_DEFAULT    =
        SourceIdRegistry.STRINGPROCESSOR_SOURCE_ID;
    private static final int    TRIGGERMODE_DEFAULT = 2;

    private TimeGenerator timeGenerator;
    private Random domGenerator;

    private long lastTime = 0;
    private int nDoms;
    private int sourceId;
    private int triggerMode;

    /**
     * default constructor
     */
    public RandomSource() {
        this(RATE_DEFAULT, NDOMS_DEFAULT, SOURCEID_DEFAULT, TRIGGERMODE_DEFAULT);
    }

    /**
     * custom constructor
     * @param rate Poisson rate per DOM
     * @param nDoms number of DOMs
     * @param sourceId source of payload
     * @param triggerMode triggering mode of DOM
     */
    public RandomSource(double rate, int nDoms, int sourceId, int triggerMode) {
        domGenerator     = new Random();
        timeGenerator    = new TimeGenerator(rate*nDoms, domGenerator);
        this.nDoms       = nDoms;
        this.sourceId    = sourceId;
        this.triggerMode = triggerMode;
    }

    public RandomSource(double rate, int nDoms, int sourceId, int triggerMode, long seed) {
        domGenerator     = new Random(seed);
        timeGenerator    = new TimeGenerator(rate*nDoms, domGenerator);
        this.nDoms       = nDoms;
        this.sourceId    = sourceId;
        this.triggerMode = triggerMode;
    }

    /**
     * generate another hit
     * @return generic hit with random timeStamp and domId
     */
    public Generic nextPayload() {
        return nextPayload((long) (lastTime + timeGenerator.nextTime()));
    }

    /**
     * generate another hit
     * @param timeStamp timestamp for the generated hit
     * @return generic hit with specified timeStamp and domId
     */
    public Generic nextPayload(long timeStamp) {
        lastTime = timeStamp;
        long domId = (long) domGenerator.nextInt(nDoms);
        return new GenericHit(timeStamp, domId, sourceId, triggerMode);
    }

}
