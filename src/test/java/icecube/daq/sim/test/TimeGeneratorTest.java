/*
 * class: TimeGeneratorTest
 *
 * Version $Id: TimeGeneratorTest.java,v 1.1 2005/06/06 20:11:51 toale Exp $
 *
 * Date: March 1 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.TimeGenerator;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any TimeGenerator object should pass.
 *
 * @author pat
 * @version $Id: TimeGeneratorTest.java,v 1.1 2005/06/06 20:11:51 toale Exp $
 */
public class TimeGeneratorTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private TimeGenerator testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TimeGeneratorTest(String name)
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
        testObject = new TimeGenerator(10.0);
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
    public void testAverage()
    {
        double sum1 = 0.0;
        double sum2 = 0.0;
        int n = 10000;
        double err = 0.01;

        for (int i=0; i<n; i++) {
            double time = testObject.nextTime();
            sum1 += time;
            sum2 += time*time;
        }

        double avg = sum1/n;
        double sig = Math.sqrt((sum2 - sum1*sum1/n)/(n-1));

        double rate = testObject.getRate();
        assertEquals("Bad rate", (double) n / 1000.0, rate);

        double delta1 = Math.abs(1.0/rate - avg)/rate;
        double delta2 = Math.abs(1.0/rate - sig)/rate;

        final boolean debug = false;
        if (debug) {
            System.out.println("Average = " + avg + " delta = " + delta1);
            System.out.println("Sigma   = " + sig + " delta = " + delta2);
        }

        assertTrue((delta1 < err) && (delta2 < err));

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TimeGeneratorTest.class);
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
