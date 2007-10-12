/*
 * class: F2kFileReaderTest
 *
 * Version $Id: F2kFileReaderTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: February 25 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.F2kFileReader;
import icecube.daq.sim.GenericF2kHit;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any F2kFileReader object should pass.
 *
 * @author pat
 * @version $Id: F2kFileReaderTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class F2kFileReaderTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private F2kFileReader testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public F2kFileReaderTest(String name)
    {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception if super class setUp fails.
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        java.io.InputStream stream =
            getClass().getResourceAsStream("/test.f2k");
        testObject = new F2kFileReader(stream, 1, 1, 0.0f, 0.0);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception if super class tearDown fails.
     */
    protected void tearDown()
            throws Exception
    {
        testObject = null;
        super.tearDown();
    }

    /**
     * Explanation of test.
     */
    public void testHits()
    {

        int nWithLC = 0;
        int nWithoutLC = 0;

        GenericF2kHit hit = (GenericF2kHit) testObject.nextPayload();
        long time1 = hit.getTimeStamp();
        long time2 = -1;

        final boolean debug = false;
        if (debug) {
            System.out.println("(" + hit.getDomId() + "," + hit.getStringId() +
                               "," + hit.getTimeStamp()
                               + ") LCTag = " + hit.getLcTag());
        }

        if (hit.getLcTag() == 0) {
            nWithoutLC++;
        } else {
            nWithLC++;
        }
        while (null != (hit = (GenericF2kHit) testObject.nextPayload())) {
            if (debug) {
                System.out.println("(" + hit.getDomId() + "," +
                                   hit.getStringId() + "," +
                                   hit.getTimeStamp() + ") LCTag = " +
                                   hit.getLcTag());
            }
            if (hit.getLcTag() == 0) {
                nWithoutLC++;
            } else {
                nWithLC++;
            }
            time2 = hit.getTimeStamp();
        }

        assertEquals("Bad number with LC", 0, nWithLC);
        assertEquals("Bad number without LC", 257, nWithoutLC);

        assertEquals("Bad first timestamp", 18230, time1);
        assertEquals("Bad last timestamp", 44317, time2);
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(F2kFileReaderTest.class);
    }

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the arguments with which to execute this method.
     */
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }
}
