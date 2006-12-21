/*
 * class: F2kFileReaderTest
 *
 * Version $Id: F2kFileReaderTest.java,v 1.5 2005/07/20 18:36:34 toale Exp $
 *
 * Date: February 25 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.F2kFileReader;
import icecube.daq.sim.GenericF2kHit;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any F2kFileReader object should pass.
 *
 * @author pat
 * @version $Id: F2kFileReaderTest.java,v 1.5 2005/07/20 18:36:34 toale Exp $
 */
public class F2kFileReaderTest
        extends TestCase
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
        testObject = new F2kFileReader("payload-generator/src/icecube/daq/sim/test/test.f2k", 1, 1, 0.0f, 0.0);
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
        System.out.println("(" + hit.getDomId() + "," + hit.getStringId() + "," + hit.getTimeStamp()
                           + ") LCTag = " + hit.getLcTag());
        if (hit.getLcTag() == 0) {
            nWithoutLC++;
        } else {
            nWithLC++;
        }
        while (null != (hit = (GenericF2kHit) testObject.nextPayload())) {
            System.out.println("(" + hit.getDomId() + "," + hit.getStringId() + "," + hit.getTimeStamp()
                               + ") LCTag = " + hit.getLcTag());
            if (hit.getLcTag() == 0) {
                nWithoutLC++;
            } else {
                nWithLC++;
            }
            time2 = hit.getTimeStamp();
        }

        System.out.println("Number with LC    = " + nWithLC);
        System.out.println("Number without LC = " + nWithoutLC);

        System.out.println("First TimeStamp = " + time1);
        System.out.println("Last  TimeStamp = " + time2);

        assertTrue((18230 == time1) && (44317 == time2));
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