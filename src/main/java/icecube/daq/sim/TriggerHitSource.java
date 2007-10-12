/*
 * Version $Id: TriggerHitSource.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.SourceIdRegistry;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class produces semi-random GenericHits based on
 * parameters described by a GenericTrigger
 */
public class TriggerHitSource
    implements ISource
{
    /** Default hit rate (1 kHz in units of 1/10 ns) */
    public static final double DEFAULT_RATE = 0.0001;
    /** Default ID of first DOM in the string. */
    public static final int DEFAULT_FIRST_DOM = 0;
    /** Default number of DOMs being simulated. */
    public static final int DEFAULT_NUM_DOMS = 60;
    /** Default hit source ID */
    public static final int DEFAULT_SOURCE_ID =
        SourceIdRegistry.STRINGPROCESSOR_SOURCE_ID;
    /** Default trigger mode */
    public static final int DEFAULT_TRIGGER_MODE = 2;

    /** ID of first DOM in the string. */
    private int firstDom;
    /** Number of DOMs being simulated. */
    private int numDoms;
    /** Hit source ID. */
    private int sourceId;
    /** Trigger mode. */
    private int triggerMode;

    /** Random number source. */
    private Random randSrc;
    /** Time generator. */
    private TimeGenerator timeGen;

    /** List of trigger used to generate hits. */
    private ArrayList triggerList;
    /** Next available hit time. */
    private long nextTime;

    /**
     * Create a trigger hit generator.
     */
    public TriggerHitSource()
    {
        this(DEFAULT_RATE, DEFAULT_FIRST_DOM, DEFAULT_NUM_DOMS,
             DEFAULT_SOURCE_ID, DEFAULT_TRIGGER_MODE, new Random());
    }

    /**
     * Create a trigger hit generator.
     *
     * @param rate rate at which hits are generated
     * @param firstDom ID of first DOM in the string
     * @param numDoms number of DOMs to simulate
     * @param randSrc random number source
     */
    public TriggerHitSource(double rate, int firstDom, int numDoms,
                            Random randSrc)
    {
        this(rate, firstDom, numDoms, DEFAULT_SOURCE_ID, DEFAULT_TRIGGER_MODE,
             randSrc);
    }

    /**
     * Create a trigger hit generator.
     *
     * @param rate rate at which hits are generated
     * @param firstDom ID of first DOM in the string
     * @param numDoms number of DOMs to simulate
     * @param sourceId source of hits
     * @param randSrc random number source
     */
    public TriggerHitSource(double rate, int firstDom, int numDoms,
                            int sourceId, Random randSrc)
    {
        this(rate, firstDom, numDoms, sourceId, DEFAULT_TRIGGER_MODE, randSrc);
    }

    /**
     * Create a trigger hit generator.
     *
     * @param rate rate at which hits are generated
     * @param firstDom ID of first DOM in the string
     * @param numDoms number of DOMs to simulate
     * @param sourceId source of hits
     * @param triggerMode trigger mode
     * @param randSrc random number source
     */
    public TriggerHitSource(double rate, int firstDom, int numDoms,
                            int sourceId, int triggerMode, Random randSrc)
    {
        this.randSrc = randSrc;
        this.firstDom = firstDom;
        this.numDoms = numDoms;
        this.sourceId = sourceId;
        this.triggerMode = triggerMode;

        this.timeGen = new TimeGenerator(rate * (double) numDoms, randSrc);

        this.triggerList = new ArrayList();
    }

    /**
     * Use the specified trigger as a template to generate hits.
     *
     * @param trigger template trigger
     */
    public void addTrigger(GenericTrigger trigger)
    {
        triggerList.add(trigger);
    }

    /**
     * Does this hit source cover the specified DOM?
     *
     * @param domId DOM ID
     *
     * @return <tt>true</tt> if hits for the specified DOM are generated
     *         by this source
     */
    public boolean hasDOM(int domId)
    {
        return (domId >= firstDom && domId < (firstDom + numDoms));
    }

    /**
     * Generate another hit.
     *
     * @return generic hit with random timeStamp and domId
     */
    public Generic nextPayload()
    {
        if (triggerList.size() == 0) {
            return null;
        }

        GenericTrigger trigger = (GenericTrigger) triggerList.get(0);
        if (trigger == null) {
            return null;
        }

        if (nextTime < trigger.getFirstTime()) {
            nextTime = trigger.getFirstTime();
        } else if (nextTime >= trigger.getLastTime()) {
            triggerList.remove(0);

            if (triggerList.size() == 0) {
                trigger = null;
            } else {
                trigger = (GenericTrigger) triggerList.get(0);
            }

            if (trigger == null) {
                return null;
            }

            nextTime = trigger.getFirstTime() + (long) timeGen.nextTime();
            if (nextTime > trigger.getLastTime()) {
                nextTime = trigger.getLastTime() - 1;
            }
        }

        long timeStamp = (long) nextTime;

        nextTime += timeGen.nextTime();
        if (nextTime > trigger.getLastTime()) {
            nextTime = trigger.getLastTime();
        }

        return nextPayload(timeStamp);
    }

    /**
     * Generate another hit.
     *
     * @param curTime hit time
     *
     * @return generic hit with random timeStamp and domId
     */
    /**
     * Generate the next trigger request payload, using the specified time
     * as the end of the trigger.
     *
     * @param curTime current time, used to mark the end of the trigger
     *
     * @return generic trigger request object
     */
    public Generic nextPayload(long timeStamp)
    {
        long domId = (long) (randSrc.nextInt(numDoms) + firstDom);
        return new GenericHit(timeStamp, domId, sourceId, triggerMode);
    }
}
