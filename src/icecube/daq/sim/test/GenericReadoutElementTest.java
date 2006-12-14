/*
 * class: GenericReadoutElementTest
 *
 * Version $Id: GenericReadoutElementTest.java,v 1.1 2005/08/09 21:11:50 toale Exp $
 *
 * Date: June 7 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.GenericReadoutElement;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericReadoutElement object should pass.
 *
 * @author pat
 * @version $Id: GenericReadoutElementTest.java,v 1.1 2005/08/09 21:11:50 toale Exp $
 */
public class GenericReadoutElementTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private GenericReadoutElement testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public GenericReadoutElementTest(String name)
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
        testObject = new GenericReadoutElement();
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
        testObject = new GenericReadoutElement();
        super.tearDown();
    }

    /**
     * Test compareTo().
     */
    public void testCompareTo()
    {
        GenericReadoutElement triggerLess  = new GenericReadoutElement();
        GenericReadoutElement triggerEqual = new GenericReadoutElement();
        GenericReadoutElement triggerMore  = new GenericReadoutElement();

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
        return new TestSuite(GenericReadoutElementTest.class);
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
