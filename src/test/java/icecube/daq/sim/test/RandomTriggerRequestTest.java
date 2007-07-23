/*
 * Version $Id: RandomTriggerRequestTest.java,v 1.3 2005/12/13 20:19:34 dglo Exp $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.common.DAQCmdInterface;

import icecube.daq.payload.ISourceID;
import icecube.daq.payload.SourceIdRegistry;

import icecube.daq.sim.GenericHit;
import icecube.daq.sim.GenericTrigger;
import icecube.daq.sim.RandomTriggerRequest;
import icecube.daq.sim.TriggerHitSource;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

public class RandomTriggerRequestTest
        extends LoggingCase
{
    public RandomTriggerRequestTest(String name)
    {
        super(name);
    }

    private Collection getTargetSourceIds(int num)
    {
        ArrayList list = new ArrayList();

        for (int i = 0; i < num; i++) {
            final String daqName;
            if ((i & 0x1) == 0x0) {
                daqName = DAQCmdInterface.DAQ_STRINGPROCESSOR;
            } else {
                daqName = DAQCmdInterface.DAQ_ICETOP_DATA_HANDLER;
            }

            final ISourceID srcId =
                SourceIdRegistry.getISourceIDFromNameAndId(daqName, i);
            list.add(srcId);
        }

        return list;
    }

    public static Test suite()
    {
        return new TestSuite(RandomTriggerRequestTest.class);
    }

    public void testTrigger()
    {
        RandomTriggerRequest trigSrc = new RandomTriggerRequest();
        trigSrc.setTargetSourceIds(getTargetSourceIds(5));

        long prevTime = 0;
        for (int i = 0; i < 3; i++) {
            GenericTrigger trig = (GenericTrigger) trigSrc.nextPayload();
            assertNotNull("Didn't get a trigger", trig);
            assertEquals("Unexpected trigger UID",
                         i + 1, trig.getTriggerUID());
            assertEquals("Unexpected trigger type",
                         RandomTriggerRequest.DEFAULT_TRIGGER_TYPE,
                         trig.getTriggerType());
            assertEquals("Unexpected trigger type",
                         RandomTriggerRequest.DEFAULT_TRIGGER_CONFIG_ID,
                         trig.getTriggerConfigId());
            assertEquals("Unexpected source ID",
                         RandomTriggerRequest.DEFAULT_SOURCE_ID,
                         trig.getSourceId());

            final long firstTime = trig.getFirstTime();
            final long lastTime = trig.getLastTime();

            // first time is unpredictable, so kludge it
            if (prevTime == 0) {
                prevTime = firstTime;
            }

            assertEquals("Unexpected first time", prevTime, firstTime);
            assertTrue("Last time not after first time", firstTime < lastTime);

            prevTime = lastTime + 1;
        }
    }

    public void testHits()
    {
        RandomTriggerRequest trigSrc = new RandomTriggerRequest(1, 10);
        trigSrc.setTargetSourceIds(getTargetSourceIds(5));

        TriggerHitSource hitSrc = (TriggerHitSource) trigSrc.getHitSource(0);
        assertNull("Hit returned before first trigger", hitSrc.nextPayload());

        long prevTime = 0;
        for (int i = 0; i < 3; i++) {
            GenericTrigger trig = (GenericTrigger) trigSrc.nextPayload();
            assertNotNull("Didn't get a trigger", trig);

            final long firstTime = trig.getFirstTime();
            final long lastTime = trig.getLastTime();
       
            for (int h = 0; true; h++) {
                GenericHit hit = (GenericHit) hitSrc.nextPayload();
                if (hit == null) {
                    break;
                }

                final long hitTime = hit.getTimeStamp();

                assertFalse("Hit#" + h + " time " + hitTime +
                           " falls before trigger first time " + firstTime,
                           hitTime < firstTime);
                assertFalse("Hit#" + h + " time " + hitTime +
                           " falls after trigger last time " + lastTime,
                           hit.getTimeStamp() > lastTime);
            }

            prevTime = lastTime;
        }
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
