/*
 * class: RandomSourceTest
 *
 * Version $Id: RandomSourceTest.java,v 1.2 2005/06/08 19:53:53 toale Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.RandomSource;
import icecube.daq.sim.GenericHit;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any RandomSource object should pass.
 *
 * @author pat
 * @version $Id: RandomSourceTest.java,v 1.2 2005/06/08 19:53:53 toale Exp $
 */
public class RandomSourceTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private RandomSource testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public RandomSourceTest(String name)
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
        testObject = new RandomSource();
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
     * test if timeStamps are valid and time-ordered
     */
    public void testOrdering()
    {
        GenericHit hit1 = (GenericHit) testObject.nextPayload();
        GenericHit hit2 = (GenericHit) testObject.nextPayload();

        long time1 = hit1.getTimeStamp();
        long time2 = hit2.getTimeStamp();
        int  comp  = hit1.compareTo(hit2);

        System.out.println("Time1 = " + time1);
        System.out.println("Time2 = " + time2);
        System.out.println("Comp  = " + comp);

        assertTrue(hit1.compareTo(hit2) < 0);
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(RandomSourceTest.class);
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
