/*
 * class: HitDataGeneratorTest
 *
 * Version $Id: HitDataGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 2 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.HitDataGenerator;
import icecube.daq.trigger.impl.EngineeringFormatHitDataPayload;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;

/**
 * This class defines the tests that any HitGenerator object should pass.
 *
 * @author pat
 * @version $Id: HitDataGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class HitDataGeneratorTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private HitDataGenerator testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public HitDataGeneratorTest(String name)
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
        testObject = new HitDataGenerator();
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
        assertEquals("Bad ByteBuffer position", 0, byteBuffer.position());
        assertEquals("Bad payload length",
                     byteBuffer.limit(), byteBuffer.getInt(0));
        assertEquals("Bad payload type", HitDataGenerator.PAYLOAD_TYPE_DEFAULT,
                     byteBuffer.getInt(4));
    }

    /**
     * Test that ByteBuffer can be loaded.
     */
    public void testLoad()
        throws Exception
    {
        ByteBuffer byteBuffer = testObject.generatePayload(0,0,0,0);
        EngineeringFormatHitDataPayload payload =
            new EngineeringFormatHitDataPayload();
        payload.initialize(0, byteBuffer, null);
        payload.loadPayload();
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(HitDataGeneratorTest.class);
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
