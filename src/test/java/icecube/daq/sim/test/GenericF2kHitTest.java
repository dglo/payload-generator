/*
 * class: GenericF2kHitTest
 *
 * Version $Id: GenericF2kHitTest.java,v 1.1 2005/06/22 03:53:04 toale Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.GenericF2kHit;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericF2kHit object should pass.
 *
 * @author pat
 * @version $Id: GenericF2kHitTest.java,v 1.1 2005/06/22 03:53:04 toale Exp $
 */
public class GenericF2kHitTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private GenericF2kHit testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public GenericF2kHitTest(String name)
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
        testObject = new GenericF2kHit(1000, 1, 1, 1, 1, 0);
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
        GenericF2kHit hitLess  = new GenericF2kHit(500, 2, 1, 1, 1, 0);
        GenericF2kHit hitEqual = new GenericF2kHit(1000, 2, 1, 1, 1, 0);
        GenericF2kHit hitMore  = new GenericF2kHit(2000, 2, 1, 1, 1, 0);

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
