/*
 * class: F2kDriverTest
 *
 * Version $Id: F2kDriverTest.java,v 1.2 2005/07/20 18:36:53 toale Exp $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.F2kDriver;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;

/**
 * This class defines the tests that any F2kDriver object should pass.
 *
 * @author pat
 * @version $Id: F2kDriverTest.java,v 1.2 2005/07/20 18:36:53 toale Exp $
 */
public class F2kDriverTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private F2kDriver testObject;

    /**
     * file of simple hits
     */
    private String testFile = "payload-generator/src/icecube/daq/sim/test/test.f2k";

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public F2kDriverTest(String name)
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
        testObject = new F2kDriver(testFile, 1, 1, 0.0f);
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
     * test HitPayloads
     */
    public void testBuffer()
    {
        testObject = new F2kDriver(testFile, 1, 1, 0.0f);

        ByteBuffer buffer = testObject.nextHit();
        if (null == buffer) {
            System.out.println("BUFFER IS NULL!!!");
        } else {
            System.out.println("Capacity = " + buffer.capacity());
        }
        while (null != (buffer = testObject.nextHit())) {
            if (null == buffer) {
                System.out.println("BUFFER IS NULL!!!");
            } else {
                System.out.println("Capacity = " + buffer.capacity());
            }
        }

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(F2kDriverTest.class);
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
