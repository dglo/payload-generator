/*
 * Version $Id: TriggerRequestGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.payload.PayloadRegistry;

import icecube.daq.sim.TriggerRequestGenerator;
import icecube.daq.sim.GenericTriggerRequest;

import icecube.daq.trigger.impl.TriggerRequestPayload;

import java.nio.ByteBuffer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TriggerRequestGeneratorTest
        extends LoggingCase
{
    private TriggerRequestGenerator testObject;

    public TriggerRequestGeneratorTest(String name)
    {
        super(name);
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        testObject = new TriggerRequestGenerator();
    }

    public static Test suite()
    {
        return new TestSuite(TriggerRequestGeneratorTest.class);
    }

    public void testGenEmpty()
    {
        GenericTriggerRequest trigReq = new GenericTriggerRequest();

        ByteBuffer buf = testObject.generatePayload(trigReq);

        assertEquals("Bad payload length", buf.limit(), buf.getInt(0));
        assertEquals("Bad payload type",
                     PayloadRegistry.PAYLOAD_ID_TRIGGER_REQUEST,
                     buf.getInt(4));
        assertEquals("Bad payload time",
                     trigReq.getLastTime(), buf.getLong(8));
    }

    /**
     * Test that ByteBuffer can be loaded.
     */
    public void testLoad()
        throws Exception
    {
        GenericTriggerRequest trigReq = new GenericTriggerRequest();

        ByteBuffer byteBuffer = testObject.generatePayload(trigReq);
        TriggerRequestPayload payload = new TriggerRequestPayload();
        payload.initialize(0, byteBuffer, null);
        payload.loadPayload();
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(suite());
    }
}
