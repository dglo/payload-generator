/*
 * class: TimeConverterTest
 *
 * Version $Id: TimeConverterTest.java,v 1.1 2005/08/17 04:53:40 toale Exp $
 *
 * Date: August 16 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.TimeConverter;
import icecube.daq.sim.test.LoggingCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any TimeConverter object should pass.
 *
 * @author pat
 * @version $Id: TimeConverterTest.java,v 1.1 2005/08/17 04:53:40 toale Exp $
 */
public class TimeConverterTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private TimeConverter testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TimeConverterTest(String name)
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
        testObject = new TimeConverter(18100, 617279699625L, 866);
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
     * Test time conversions.
     */
    public void testConversions()
    {

        long tripTime = 18100;
        long offset = 617279699625L;
        long gpsOffset = 866;

        final boolean debug = false;
        if (debug) {
            System.out.println("TimeConverterTest:");
            System.out.println("   Trip time      = " + tripTime + " ns");
            System.out.println("   Offset time    = " + offset + " ns");
            System.out.println("   DOR reset time = " + gpsOffset + " s");

            long utcTime = (long) (866*1e10);
            System.out.println("UTC time = " + utcTime + " 1/10 ns");
            System.out.println("   DOR clock = " + testObject.getDorClockFromUtcTime(utcTime));
            System.out.println("   DOM clock = " + testObject.getDomClockFromUtcTime(utcTime));

            long dorClock = 0;
            System.out.println("DOR clock = " + dorClock);
            System.out.println("   UTC time  = " + testObject.getUtcTimeFromDorClock(dorClock));
            System.out.println("   DOM clock = " + testObject.getDomClockFromDorClock(dorClock));

            long domClock = 0;
            System.out.println("DOM clock = " + domClock);
            System.out.println("   UTC time  = " + testObject.getUtcTimeFromDomClock(domClock));
            System.out.println("   DOR clock = " + testObject.getDorClockFromDomClock(domClock));
        }
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TimeConverterTest.class);
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
