/*
 * class: F2kDriverTest
 *
 * Version $Id: F2kDriverTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.F2kDriver;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;

/**
 * This class defines the tests that any F2kDriver object should pass.
 *
 * @author pat
 * @version $Id: F2kDriverTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class F2kDriverTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private F2kDriver testObject;

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
        java.io.InputStream stream =
            getClass().getResourceAsStream("/test.f2k");
        testObject = new F2kDriver(stream, 1, 1, 0.0f);
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
        ByteBuffer buffer = testObject.nextHit();
        while (null != (buffer = testObject.nextHit())) {
            System.out.println("Capacity = " + buffer.capacity());
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
