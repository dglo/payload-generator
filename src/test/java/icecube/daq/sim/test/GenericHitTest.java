/*
 * class: GenericHitTest
 *
 * Version $Id: GenericHitTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.GenericHit;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericHit object should pass.
 *
 * @author pat
 * @version $Id: GenericHitTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class GenericHitTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private GenericHit testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public GenericHitTest(String name)
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
        testObject = new GenericHit(1000, 1, 1, 1);
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
     * Test compareTo().
     */
    public void testCompareTo()
    {
        GenericHit hitLess  = new GenericHit(500, 2, 1, 1);
        GenericHit hitEqual = new GenericHit(1000, 2, 1, 1);
        GenericHit hitMore  = new GenericHit(2000, 2, 1, 1);

        int less  = testObject.compareTo(hitLess);
        int equal = testObject.compareTo(hitEqual);
        int more  = testObject.compareTo(hitMore);

        assertTrue( (less > 0) && (equal == 0) && (more < 0));

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(GenericHitTest.class);
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
