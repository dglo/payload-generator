/*
 * Version $Id: TriggerRequestGenerator.java 2631 2008-02-11 06:27:31Z dglo $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.PayloadRegistry;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Vector;

/**
 * Append-only byte buffer manager.
 */
class AppendBuffer
{
    /** Managed byte buffer. */
    private ByteBuffer byteBuffer;
    /** Current offset. */
    private int offset;

    /**
     * Create an append-only byte buffer.
     *
     * @param total byte buffer length
     */
    AppendBuffer(int len)
    {
        byteBuffer = ByteBuffer.allocate(len);
    }

    /**
     * Add a 4-byte integer value to the buffer.
     *
     * @param val value
     */
    void addInt(int val)
    {
        byteBuffer.putInt(offset, val);
        offset += 4;
    }

    /**
     * Add an 8-byte integer value to the buffer.
     *
     * @param val value
     */
    void addLong(long val)
    {
        byteBuffer.putLong(offset, val);
        offset += 8;
    }

    /**
     * Add a 2-byte integer value to the buffer.
     *
     * @param val value
     */
    void addShort(int val)
    {
        byteBuffer.putShort(offset, (short) val);
        offset += 2;
    }

    /**
     * Check that the current offset matches the expected value.
     *
     * @param expOffset expected offset
     */
    void checkOffset(int expOffset)
    {
        if (offset != expOffset) {
            throw new IndexOutOfBoundsException("Expected to be at offset " +
                                                expOffset + ", not " + offset);
        }
    }

    /**
     * Get the managed byte buffer.
     *
     * @return byte buffer
     */
    ByteBuffer getBuffer()
    {
        return byteBuffer;
    }
}

/**
 * Generate a ByteBuffer representation of a TriggerRequestPayload
 */
public class TriggerRequestGenerator
{
    /**
     * fields with a fixed size (in bytes)
     */
    private static final int PAYLOAD_LENGTH_SIZE = 4;
    private static final int PAYLOAD_TYPE_SIZE   = 4;
    private static final int PAYLOAD_TIME_SIZE   = 8;

    private static final int RECORD_TYPE_SIZE    = 2;
    private static final int TRIGGER_UID_SIZE    = 4;
    private static final int TRIGGER_TYPE_SIZE   = 4;
    private static final int TRIGGER_CONFIG_SIZE = 4;
    private static final int SOURCE_ID_SIZE      = 4;
    private static final int FIRST_UTCTIME_SIZE  = 8;
    private static final int LAST_UTCTIME_SIZE   = 8;

    private static final int REQUEST_TYPE_SIZE   = 2;
    private static final int NUM_ELEMENTS_SIZE   = 4;

    private static final int REQELEM_TYPE_SIZE   = 4;
    private static final int REQELEM_DOMID_SIZE  = 8;

    private static final int COMP_LENGTH_SIZE    = 4;
    private static final int COMP_TYPE_SIZE      = 2;
    private static final int COMP_NUMELEM_SIZE   = 2;

    // payload envelope sizes
    private static final int PAYLOAD_ENVELOPE_SIZE =
        PAYLOAD_LENGTH_SIZE
        + PAYLOAD_TYPE_SIZE
        + PAYLOAD_TIME_SIZE;

    // trigger request header sizes
    private static final int TRIGREQ_HEADER_SIZE =
        + RECORD_TYPE_SIZE
        + TRIGGER_UID_SIZE
        + TRIGGER_TYPE_SIZE
        + TRIGGER_CONFIG_SIZE
        + SOURCE_ID_SIZE
        + FIRST_UTCTIME_SIZE
        + LAST_UTCTIME_SIZE;

    // readout request element header sizes
    private static final int REQELEM_HEADER_SIZE =
        + REQUEST_TYPE_SIZE
        + TRIGGER_UID_SIZE
        + SOURCE_ID_SIZE
        + NUM_ELEMENTS_SIZE;

    // fixed header for trigger request buffer
    private static final int FIXED_HEADER_SIZE =
        PAYLOAD_ENVELOPE_SIZE
        + TRIGREQ_HEADER_SIZE
        + REQELEM_HEADER_SIZE;

    private static final int REQELEM_SIZE =
        REQELEM_TYPE_SIZE
        + SOURCE_ID_SIZE
        + FIRST_UTCTIME_SIZE
        + LAST_UTCTIME_SIZE
        + REQELEM_DOMID_SIZE;

    // composite payload envelope
    private static final int COMPOSITE_ENVELOPE_SIZE =
        COMP_LENGTH_SIZE
        + COMP_TYPE_SIZE
        + COMP_NUMELEM_SIZE;

    // fixed size of trigger request buffer
    private static final int FIXED_BUFFER_SIZE =
        FIXED_HEADER_SIZE
        + COMPOSITE_ENVELOPE_SIZE;

    /** payload format. */
    private int payloadType;

    /**
     * Default constructor
     */
    public TriggerRequestGenerator()
    {
        payloadType = PayloadRegistry.PAYLOAD_ID_TRIGGER_REQUEST;
    }

    /**
     * Generate a trigger request.
     *
     * @param trigReq generic trigger request container
     *
     * @return ByteBuffer representation of trigger request
     */
    public ByteBuffer generatePayload(GenericTriggerRequest trigReq)
    {
        Vector readouts = trigReq.getReadoutElementList();

        final int readoutLen;
        if (readouts == null) {
            readoutLen = 0;
        } else {
            readoutLen = readouts.size();
        }

        int payloadLength = FIXED_BUFFER_SIZE +
            (readoutLen * REQELEM_SIZE);

        AppendBuffer buf = new AppendBuffer(payloadLength);

        // payload envelope
        buf.addInt(payloadLength);
        buf.addInt(payloadType);
        buf.addLong(trigReq.getFirstTime());
        buf.checkOffset(PAYLOAD_ENVELOPE_SIZE);

        // trigger request header
        buf.addShort(4);   // XXX what is this, really?
        buf.addInt(trigReq.getTriggerUID());
        buf.addInt(trigReq.getTriggerType());
        buf.addInt(trigReq.getTriggerConfigId());
        buf.addInt(trigReq.getSourceId());
        buf.addLong(trigReq.getFirstTime());
        buf.addLong(trigReq.getLastTime());
        buf.checkOffset(PAYLOAD_ENVELOPE_SIZE + TRIGREQ_HEADER_SIZE);

        // readout request header
        buf.addShort(trigReq.getReadoutRequestType());
        buf.addInt(trigReq.getTriggerUID());
        buf.addInt(trigReq.getSourceId());
        buf.addInt(readoutLen);
        buf.checkOffset(FIXED_HEADER_SIZE);

        int expOffset = FIXED_HEADER_SIZE;

        // readout request records
        if (readoutLen > 0) {
            Iterator iter = readouts.iterator();
            while (iter.hasNext()) {
                GenericReadoutElement readout =
                    (GenericReadoutElement) iter.next();

                buf.addInt(readout.getReadoutType());
                buf.addInt(readout.getSourceId());
                buf.addLong(readout.getFirstTime());
                buf.addLong(readout.getLastTime());
                buf.addLong(readout.getDomId());

                expOffset += REQELEM_SIZE;
                buf.checkOffset(expOffset);
            }
        }

        // empty composite payload header
        buf.addInt(8);
        buf.addShort(1);
        buf.addShort(0);

        // make sure we filled the buffer
        buf.checkOffset(payloadLength);

        // no flip is necessary here since all puts use offsets
        // (position never changes)
        return buf.getBuffer();
    }
}
