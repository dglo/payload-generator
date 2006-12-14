/*
 * Version $Id: EventInputDriverTest.java,v 1.2 2005/12/13 20:19:34 dglo Exp $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.EventInputDriver;

import java.io.File;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class EventInputDriverTest
        extends TestCase
{
    private static final int TRIGREQ_RECORD_LEN = 104;
    private static final int HIT_RECORD_LEN = 38;
    private static final int HITDATA_RECORD_LEN = 568;

    private File trigOut;
    private File hitOut;

    public EventInputDriverTest(String name)
    {
        super(name);
    }

    private void checkLengths(int numRecs, long trigFileLen, int trigRecLen,
                              long hitFileLen, int hitRecLen)
    {
        assertEquals("Unexpected trigger file length",
                     0, trigFileLen % trigRecLen);
        assertEquals("Unexpected number of trigger records in file",
                     trigRecLen * numRecs, trigFileLen);
        assertEquals("Unexpected hit data file length",
                     0, hitFileLen % hitRecLen);
        //assertEquals("Unexpected number of hit data records in file",
        //             hitRecLen * numRecs, hitFileLen);
    }

    protected void setUp()
    {
        try {
            trigOut = File.createTempFile("trigOut", "dat");
        } catch (IOException ioe) {
            trigOut = null;
        }

        try {
            hitOut = File.createTempFile("hitOut", "dat");
        } catch (IOException ioe) {
            hitOut = null;
        }
    }

    protected void tearDown()
    {
        trigOut.delete();
        hitOut.delete();
    }

    public static Test suite()
    {
        return new TestSuite(EventInputDriverTest.class);
    }

    public void testHitGen()
    {
        final int numRecs = 5;

        EventInputDriver.main(new String[] {
                                  "-n", Integer.toString(numRecs),
                                  trigOut.getAbsolutePath(),
                                  hitOut.getAbsolutePath(),
                              });

        checkLengths(numRecs, trigOut.length(), TRIGREQ_RECORD_LEN,
                     hitOut.length(), HIT_RECORD_LEN);
    }

    public void testHitDataGen()
    {
        final int numRecs = 5;

        EventInputDriver.main(new String[] {
                                  "-n", Integer.toString(numRecs),
                                  "-payload", "HitDataPayload",
                                  trigOut.getAbsolutePath(),
                                  hitOut.getAbsolutePath(),
                              });

        checkLengths(numRecs, trigOut.length(), TRIGREQ_RECORD_LEN,
                     hitOut.length(), HITDATA_RECORD_LEN);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
