/*
 * class: SimpleDriverTest
 *
 * Version $Id: SimpleDriverTest.java 2657 2008-02-15 23:41:14Z dglo $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.payload.IPayload;
import icecube.daq.payload.PayloadInterfaceRegistry;
import icecube.daq.payload.SourceIdRegistry;
import icecube.daq.sim.SimpleDriver;
import icecube.daq.trigger.IHitDataPayload;
import icecube.daq.trigger.IHitPayload;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any SimpleDriver object should pass.
 *
 * @author pat
 * @version $Id: SimpleDriverTest.java 2657 2008-02-15 23:41:14Z dglo $
 */
public class SimpleDriverTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private SimpleDriver testObject;

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
     * test HitPayloads
     */
    public void testHitPayloads()
    {
        java.io.InputStream stream =
            getClass().getResourceAsStream("/test.txt");
        testObject = new SimpleDriver(stream, "HitPayload");

        IPayload payload = testObject.nextHit();
        assertEquals(payload.getPayloadInterfaceType(), PayloadInterfaceRegistry.I_HIT_PAYLOAD);

        IHitPayload hitPayload = (IHitPayload) payload;
        long time = hitPayload.getHitTimeUTC().longValue();
        long domId = hitPayload.getDOMID().longValue();
        int sourceId = hitPayload.getSourceID().getSourceID();
        int triggerMode = hitPayload.getTriggerType();

        assertEquals(time, 1);
        assertEquals(domId, 1);
        assertEquals(sourceId, SourceIdRegistry.INICE_TRIGGER_SOURCE_ID);
        assertEquals(triggerMode, 2);

    }

    /**
     * test HitDataPayloads
     */
    public void testHitDataPayloads()
    {
        java.io.InputStream stream =
            getClass().getResourceAsStream("/test.txt");
        testObject = new SimpleDriver(stream, "HitDataPayload");

        IPayload payload = testObject.nextHit();
        assertEquals(payload.getPayloadInterfaceType(), PayloadInterfaceRegistry.I_HIT_DATA_PAYLOAD);

        IHitDataPayload hitPayload = (IHitDataPayload) payload;
        long time = hitPayload.getHitTimeUTC().longValue();
        long domId = hitPayload.getDOMID().longValue();
        int sourceId = hitPayload.getSourceID().getSourceID();
        int triggerMode = hitPayload.getTriggerType();

        assertEquals(time, 1);
        assertEquals(domId, 1);
        assertEquals(sourceId, SourceIdRegistry.INICE_TRIGGER_SOURCE_ID);
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
