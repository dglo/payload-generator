/*
 * Version $Id: TriggerHitSourceTest.java,v 1.2 2005/12/13 20:19:34 dglo Exp $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.GenericHit;
import icecube.daq.sim.GenericTrigger;
import icecube.daq.sim.TriggerHitSource;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TriggerHitSourceTest
        extends LoggingCase
{
    public TriggerHitSourceTest(String name)
    {
        super(name);
    }

    private static final GenericTrigger buildTrigger()
    {
        GenericTrigger trig = new GenericTrigger();
        trig.setTriggerUID(1);
        trig.setTriggerType(123);
        trig.setTriggerConfigId(456);
        trig.setSourceId(789);
        trig.setFirstTime(0L);
        trig.setLastTime(100000000L);
        return trig;
    }

    public static Test suite()
    {
        return new TestSuite(TriggerHitSourceTest.class);
    }

    public void testNoTrigger()
    {
        TriggerHitSource hitSrc = new TriggerHitSource();
        assertNull("Got hit before setting trigger", hitSrc.nextPayload());
    }

    /**
     * test if timeStamps are valid and time-ordered
     */
    public void testOrdering()
    {
        TriggerHitSource hitSrc = new TriggerHitSource();
        hitSrc.addTrigger(buildTrigger());

        GenericHit hit1 = (GenericHit) hitSrc.nextPayload();
        GenericHit hit2 = (GenericHit) hitSrc.nextPayload();

        long time1 = hit1.getTimeStamp();
        long time2 = hit2.getTimeStamp();
        int  comp  = hit1.compareTo(hit2);

        assertTrue("time1 >= time2", time1 < time2);
        assertTrue("hit1 >= hit2", hit1.compareTo(hit2) < 0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
