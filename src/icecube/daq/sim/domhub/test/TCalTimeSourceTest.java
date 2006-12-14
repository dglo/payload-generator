/*
 * class: TCalTimeSourceTest
 *
 * Version $Id: TCalTimeSourceTest.java,v 1.2 2005/08/17 17:57:01 toale Exp $
 *
 * Date: August 14 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.TCalTimeSource;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any TCalTimeSource object should pass.
 *
 * @author pat
 * @version $Id: TCalTimeSourceTest.java,v 1.2 2005/08/17 17:57:01 toale Exp $
 */
public class TCalTimeSourceTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private TCalTimeSource testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TCalTimeSourceTest(String name)
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
        testObject = new TCalTimeSource();
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
     * Test the different constructors.
     */
    public void testConstructors()
    {
        testObject.setRandomSeed(-1);

        long time = testObject.nextTime();
        System.out.println("Wait time =  D, time smear = D: time = " + time);

        testObject.setWaitTime(10);
        testObject.setTimeSmear(0);
        time = testObject.nextTime();
        System.out.println("Wait time = 10, time smear = 0: time = " + time);

        testObject.setWaitTime(10);
        testObject.setTimeSmear(5);
        time = testObject.nextTime();
        System.out.println("Wait time = 10, time smear = 5: time = " + time);

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TCalTimeSourceTest.class);
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
