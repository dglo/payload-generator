/*
 * Version $Id: RandomTriggerRequest.java,v 1.7 2005/12/13 20:19:34 dglo Exp $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.ISourceID;
import icecube.daq.payload.SourceIdRegistry;

import icecube.daq.trigger.IReadoutRequestElement;

import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Random trigger request generator.
 */
public class RandomTriggerRequest
    implements ISource
{
    /** Default hit rate (1 kHz in units of 1/10 ns) */
    public static final double DEFAULT_RATE = 0.0001;
    /** Default number of DOM Strings being simulated. */
    public static final int DEFAULT_NUM_STRINGS = 3;
    /** Default number of DOMs being simulated. */
    public static final int DEFAULT_DOMS_PER_STRING = 16;

    /** Default trigger type. */
    public static final int DEFAULT_TRIGGER_TYPE = 1;
    /** Default trigger configuration ID. */
    public static final int DEFAULT_TRIGGER_CONFIG_ID = 0;
    /** Default trigger request source ID */
    public static final int DEFAULT_SOURCE_ID =
        SourceIdRegistry.GLOBAL_TRIGGER_SOURCE_ID;

    /** List of possible readout request types. */
    public static final int[] READOUT_TYPES = new int[] {
        IReadoutRequestElement.READOUT_TYPE_GLOBAL,
        IReadoutRequestElement.READOUT_TYPE_II_GLOBAL,
        IReadoutRequestElement.READOUT_TYPE_IT_GLOBAL,
        IReadoutRequestElement.READOUT_TYPE_II_STRING,
        IReadoutRequestElement.READOUT_TYPE_II_MODULE,
    };

    /** DOM ID used to represent all DOMs */
    private static final int ALL_DOMS = -1;

    private Random randSrc;
    private int numStrings;
    private int domsPerString;
    private int sourceId;

    private TimeGenerator timeGen;
    private TriggerHitSource[] hitSources;

    /** List of possible readout request source IDs. */
    private int[] readoutSourceIds;

    private int nextTriggerId = 1;
    private long prevTime;

    /**
     * Create a random trigger request generator.
     */
    public RandomTriggerRequest()
    {
        this(DEFAULT_NUM_STRINGS, DEFAULT_DOMS_PER_STRING);
    }

    /**
     * Create a random trigger request generator.
     *
     * @param seed random number seed value
     */
    public RandomTriggerRequest(long seed)
    {
        this(DEFAULT_NUM_STRINGS, DEFAULT_DOMS_PER_STRING, seed);
    }

    /**
     * Create a random trigger request generator.
     *
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     */
    public RandomTriggerRequest(int numStrings, int domsPerString)
    {
        this(DEFAULT_RATE, numStrings, domsPerString);
    }

    /**
     * Create a random trigger request generator.
     *
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     * @param seed random number seed value
     */
    public RandomTriggerRequest(int numStrings, int domsPerString, long seed)
    {
        this(DEFAULT_RATE, numStrings, domsPerString, DEFAULT_SOURCE_ID, seed);
    }

    /**
     * Create a random trigger request generator.
     *
     * @param rate rate at which hits are generated
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     */
    public RandomTriggerRequest(double rate, int numStrings, int domsPerString)
    {
        this(rate, numStrings, domsPerString, DEFAULT_SOURCE_ID);
    }

    /**
     * Create a random trigger request generator.
     *
     * @param rate rate at which hits are generated
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     * @param sourceId source of trigger requests
     */
    public RandomTriggerRequest(double rate, int numStrings, int domsPerString,
                                int sourceId)
    {
        this(rate, numStrings, domsPerString, sourceId, new Random());
    }

    /**
     * Create a random trigger request generator.
     *
     * @param rate rate at which hits are generated
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     * @param sourceId source of trigger requests
     * @param seed random number seed value
     */
    public RandomTriggerRequest(double rate, int numStrings, int domsPerString,
                                int sourceId, long seed)
    {
        this(rate, numStrings, domsPerString, sourceId, new Random(seed));
    }

    /**
     * Create a random trigger request generator.
     *
     * @param rate rate at which hits are generated
     * @param numStrings number of DOM Strings to simulate
     * @param domsPerString number of DOMs per string
     * @param sourceId source of trigger requests
     * @param randSrc random number source
     */
    public RandomTriggerRequest(double rate, int numStrings, int domsPerString,
                                int sourceId, Random randSrc)
    {
        // silently fix erroneous number of strings
        if (numStrings < 1) {
            numStrings = 1;
        } else if (numStrings > domsPerString) {
            numStrings = domsPerString;
        }

        this.numStrings = numStrings;
        this.domsPerString = domsPerString;
        this.sourceId = sourceId;
        this.randSrc = randSrc;

        timeGen = new TimeGenerator(rate, randSrc);
        hitSources = new TriggerHitSource[numStrings];

        int firstDom = 0;
        for (int i = 0; i < numStrings; i++) {
            final int srcId = SourceIdRegistry.STRINGPROCESSOR_SOURCE_ID + i;

            hitSources[i] = new TriggerHitSource(rate, firstDom, domsPerString,
                                                 srcId, randSrc);
            firstDom += domsPerString;
        }

        prevTime = System.currentTimeMillis();
    }

    /**
     * Get specified hit payload source.
     *
     * @param index index of desired hit payload source
     *
     * @return hit payload source
     */
    public ISource getHitSource(int index)
    {
        if (index < 0 || index > hitSources.length) {
            return null;
        }

        return hitSources[index];
    }

    /**
     * Get all the hit payload sources.
     *
     * @return array of hit payload sources
     */
    public ISource[] getHitSources()
    {
        return hitSources;
    }

    /**
     * Generate a random readout request source ID.
     *
     * @return random readout request source ID
     */
    private int generateReadoutRequestDOM(int readoutType)
    {
        int domId;

        if (readoutType != IReadoutRequestElement.READOUT_TYPE_II_MODULE) {
            domId = ALL_DOMS;
        } else {
            domId = randSrc.nextInt(numStrings * domsPerString);
        }

        return domId;
    }

    /**
     * Generate a random readout request type.
     *
     * @return random readout request type
     */
    private int generateReadoutRequestType()
    {
        final int idx = randSrc.nextInt(READOUT_TYPES.length);
        return READOUT_TYPES[idx];
    }

    /**
     * Generate a random readout request source ID.
     *
     * @return random readout request source ID
     */
    private int generateReadoutRequestSource(int readoutType)
    {
        int srcId;

        switch (readoutType) {
        case IReadoutRequestElement.READOUT_TYPE_GLOBAL:
        case IReadoutRequestElement.READOUT_TYPE_II_GLOBAL:
        case IReadoutRequestElement.READOUT_TYPE_IT_GLOBAL:
            srcId = -1;
            break;
        default:
            if (readoutSourceIds == null) {
                throw new Error("List of target source IDs has not been set");
            }

            final int idx = randSrc.nextInt(readoutSourceIds.length);
            srcId = readoutSourceIds[idx];
            break;
        }

        return srcId;
    }

    /**
     * Get a random integer for the specified range.
     *
     * @param lowBound lower bound of range
     * @param upBound upper bound of range
     *
     * @return random value
     */
    public int getRandomInt(int lowBound, int upBound)
    {
        int range, offset;
        if (lowBound < upBound) {
            range = upBound - lowBound;
            offset = lowBound;
        } else {
            range = lowBound - upBound;
            offset = upBound;
        }

        int randVal;
        if (range == 0) {
            randVal = offset;
        } else {
            randVal = randSrc.nextInt(range) + offset;
        }

        return randVal;
    }

    /**
     * Has the list of source IDs been initialized?
     *
     * @return <tt>true</tt> if list of source IDs has been set
     */
    public boolean isInitialized()
    {
        return (readoutSourceIds != null);
    }

    /**
     * Generate the next trigger request payload
     * with a randomly generated timestamp.
     *
     * @return generic trigger request object
     */
    public Generic nextPayload()
    {
        return nextPayload(prevTime + (long) timeGen.nextTime() + 1);
    }

    /**
     * Generate the next trigger request payload, using the specified time
     * as the end of the trigger.
     *
     * @param curTime current time, used to mark the end of the trigger
     *
     * @return generic trigger request object
     */
    public Generic nextPayload(long curTime)
    {
        if (curTime < prevTime) {
            throw new RuntimeException("Time went backwards [" + curTime +
                                       " < " + prevTime + "]");
        } else if (curTime == prevTime) {
            throw new RuntimeException("Time did not advance [" + curTime +
                                       "]");
        }

        GenericTriggerRequest trigReq = new GenericTriggerRequest();
        trigReq.setTriggerUID(nextTriggerId++);
        trigReq.setTriggerType(DEFAULT_TRIGGER_TYPE);
        trigReq.setTriggerConfigId(DEFAULT_TRIGGER_CONFIG_ID);
        trigReq.setSourceId(sourceId);

        final long firstTime = prevTime;
        final long lastTime = curTime;
        prevTime = curTime + 1;

        trigReq.setFirstTime(firstTime);
        trigReq.setLastTime(lastTime);

        final int readoutType = generateReadoutRequestType();
        final int domId = generateReadoutRequestDOM(readoutType);

        GenericReadoutElement readout = new GenericReadoutElement();
        readout.setReadoutType(readoutType);
        readout.setFirstTime(firstTime);
        readout.setLastTime(lastTime);
        readout.setDomId(domId);
        readout.setSourceId(generateReadoutRequestSource(readoutType));

        Vector roList = new Vector();
        roList.add(readout);

        trigReq.setReadoutElementList(roList);

        for (int i = 0; i < hitSources.length; i++) {
            if (domId == ALL_DOMS || hitSources[i].hasDOM(domId)) {
                hitSources[i].addTrigger(trigReq);
            }
        }

        return trigReq;
    }

    /**
     * Set the list of readout source IDs.
     *
     * @param srcIds array of integer source IDs
     */
    public void setTargetSourceIds(int[] srcIds)
    {
        int[] tmpArray = new int[srcIds.length];

        System.arraycopy(srcIds, 0, tmpArray, 0, srcIds.length);

        readoutSourceIds = tmpArray;
    }

    /**
     * Set the list of readout source IDs.
     *
     * @param srcIds collection of ISourceIDs
     */
    public void setTargetSourceIds(Collection srcIds)
    {
        int[] tmpArray = new int[srcIds.size()];

        int nextNum = 0;

        Iterator iter = srcIds.iterator();
        while (iter.hasNext()) {
            ISourceID srcId = (ISourceID) iter.next();
            tmpArray[nextNum++] = srcId.getSourceID();
        }

        readoutSourceIds = tmpArray;
    }
}
