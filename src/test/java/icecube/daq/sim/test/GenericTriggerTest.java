/*
 * class: GenericTriggerTest
 *
 * Version $Id: GenericTriggerTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 7 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.GenericTrigger;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericTrigger object should pass.
 *
 * @author pat
 * @version $Id: GenericTriggerTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class GenericTriggerTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private GenericTrigger testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public GenericTriggerTest(String name)
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
        testObject = new GenericTrigger();
        testObject.setFirstTime(20);
        testObject.setLastTime(40);
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
        testObject = new GenericTrigger();
        super.tearDown();
    }

    /**
     * Test compareTo().
     */
    public void testCompareTo()
    {
        GenericTrigger triggerLess  = new GenericTrigger();
        GenericTrigger triggerEqual = new GenericTrigger();
        GenericTrigger triggerMore  = new GenericTrigger();

        triggerLess.setFirstTime(10);
        triggerLess.setLastTime(50);

        triggerEqual.setFirstTime(20);
        triggerEqual.setLastTime(30);

        triggerMore.setFirstTime(30);
        triggerMore.setLastTime(35);

        int less  = testObject.compareTo(triggerLess);
        int equal = testObject.compareTo(triggerEqual);
        int more  = testObject.compareTo(triggerMore);

        assertTrue( (less > 0) && (equal == 0) && (more < 0));

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(GenericTriggerTest.class);
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
