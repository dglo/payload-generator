/*
 * class: EngFmtGeneratorTest
 *
 * Version $Id: EngFmtGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.EngFmtGenerator;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;

/**
 * This class defines the tests that any EngFmtGenerator object should pass.
 *
 * @author pat
 * @version $Id: EngFmtGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class EngFmtGeneratorTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private EngFmtGenerator testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public EngFmtGeneratorTest(String name)
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
        testObject = new EngFmtGenerator();
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
     * test if ByteBuffer has the correct length.
     */
    public void testLength()
    {
        ByteBuffer byteBuffer = testObject.generatePayload(0,0,0,0);
        assertEquals("For buffer " + byteBuffer,
                     byteBuffer.getShort(0), byteBuffer.capacity());
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(EngFmtGeneratorTest.class);
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
