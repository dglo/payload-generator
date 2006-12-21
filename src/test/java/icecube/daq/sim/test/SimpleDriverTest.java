/*
 * class: SimpleDriverTest
 *
 * Version $Id: SimpleDriverTest.java,v 1.1 2005/06/07 19:38:29 toale Exp $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.SimpleDriver;
import icecube.daq.trigger.IHitPayload;
import icecube.daq.trigger.IHitDataPayload;
import icecube.daq.payload.IPayload;
import icecube.daq.payload.PayloadInterfaceRegistry;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any SimpleDriver object should pass.
 *
 * @author pat
 * @version $Id: SimpleDriverTest.java,v 1.1 2005/06/07 19:38:29 toale Exp $
 */
public class SimpleDriverTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private SimpleDriver testObject;

    /**
     * file of simple hits
     */
    private String testFile = "payload-generator/src/icecube/daq/sim/test/test.txt";

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public SimpleDriverTest(String name)
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
        testObject = new SimpleDriver(testFile);
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
    public void testHitPayloads()
    {
        testObject = new SimpleDriver(testFile, "HitPayload");

        IPayload payload = testObject.nextHit();
        assertEquals(payload.getPayloadInterfaceType(), PayloadInterfaceRegistry.I_HIT_PAYLOAD);

        IHitPayload hitPayload = (IHitPayload) payload;
        long time = hitPayload.getHitTimeUTC().getUTCTimeAsLong();
        long domId = hitPayload.getDOMID().getDomIDAsLong();
        int sourceId = hitPayload.getSourceID().getSourceID();
        int triggerMode = hitPayload.getTriggerType();

        assertEquals(time, 1);
        assertEquals(domId, 1);
        assertEquals(sourceId, 4000);
        assertEquals(triggerMode, 2);

    }

    /**
     * test HitDataPayloads
     */
    public void testHitDataPayloads()
    {
        testObject = new SimpleDriver(testFile, "HitDataPayload");

        IPayload payload = testObject.nextHit();
        assertEquals(payload.getPayloadInterfaceType(), PayloadInterfaceRegistry.I_HIT_DATA_PAYLOAD);

        IHitDataPayload hitPayload = (IHitDataPayload) payload;
        long time = hitPayload.getHitTimeUTC().getUTCTimeAsLong();
        long domId = hitPayload.getDOMID().getDomIDAsLong();
        int sourceId = hitPayload.getSourceID().getSourceID();
        int triggerMode = hitPayload.getTriggerType();

        assertEquals(time, 1);
        assertEquals(domId, 1);
        assertEquals(sourceId, 4000);
        assertEquals(triggerMode, 2);

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(SimpleDriverTest.class);
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
