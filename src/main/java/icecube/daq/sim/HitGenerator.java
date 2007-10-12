/*
 * class: HitGenerator
 *
 * Version $Id: HitGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 2 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of a HitPayload
 *
 * @version $Id: HitGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class HitGenerator
        implements IGenerator
{

    /**
     * payload format defaults
     */
    public static final int PAYLOAD_TYPE_DEFAULT = 1;

    /**
     * fields with a fixed size (in bytes)
     */
    private static final int PAYLOAD_LENGTH_SIZE = 4;
    private static final int PAYLOAD_TYPE_SIZE   = 4;
    private static final int PAYLOAD_TIME_SIZE   = 8;

    private static final int TRIGGER_TYPE_SIZE   = 4;
    private static final int TRIGGER_CONFIG_SIZE = 4;
    private static final int SOURCE_ID_SIZE      = 4;
    private static final int DOM_ID_SIZE         = 8;
    private static final int TRIGGER_MODE_SIZE   = 2;

    private static final int PAYLOAD_SIZE = PAYLOAD_LENGTH_SIZE
                                            + PAYLOAD_TYPE_SIZE
                                            + PAYLOAD_TIME_SIZE
                                            + TRIGGER_TYPE_SIZE
                                            + TRIGGER_CONFIG_SIZE
                                            + SOURCE_ID_SIZE
                                            + DOM_ID_SIZE
                                            + TRIGGER_MODE_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int PAYLOAD_LENGTH_OFFSET = 0;
    private static final int PAYLOAD_TYPE_OFFSET   = PAYLOAD_LENGTH_OFFSET + PAYLOAD_LENGTH_SIZE;
    private static final int PAYLOAD_TIME_OFFSET   = PAYLOAD_TYPE_OFFSET   + PAYLOAD_TYPE_SIZE;

    private static final int TRIGGER_TYPE_OFFSET   = PAYLOAD_TIME_OFFSET   + PAYLOAD_TIME_SIZE;
    private static final int TRIGGER_CONFIG_OFFSET = TRIGGER_TYPE_OFFSET   + TRIGGER_TYPE_SIZE;
    private static final int SOURCE_ID_OFFSET      = TRIGGER_CONFIG_OFFSET + TRIGGER_CONFIG_SIZE;
    private static final int DOM_ID_OFFSET         = SOURCE_ID_OFFSET      + SOURCE_ID_SIZE;
    private static final int TRIGGER_MODE_OFFSET   = DOM_ID_OFFSET         + DOM_ID_SIZE;

    /**
     * payload format
     */
    private int payloadType;
    private int payloadLength;

    /**
     * default constructor
     */
    public HitGenerator() {
        payloadType = PAYLOAD_TYPE_DEFAULT;
        payloadLength = PAYLOAD_SIZE;
    }

    public int getSize() {
        return payloadLength;
    }

    /**
     * generate a hit
     * @param timeStamp corrected time of hit
     * @param domId DOM id of hit
     * @param sourceId source of hit
     * @param triggerMode DOM triggering mode
     * @return ByteBuffer representation of hit
     */
    public ByteBuffer generatePayload(long timeStamp, long domId, int sourceId, int triggerMode) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(payloadLength);

        // payload envelope
        byteBuffer.putInt(PAYLOAD_LENGTH_OFFSET, payloadLength);
        byteBuffer.putInt(PAYLOAD_TYPE_OFFSET, payloadType);
        byteBuffer.putLong(PAYLOAD_TIME_OFFSET, timeStamp);

        byteBuffer.putInt(TRIGGER_TYPE_OFFSET, triggerMode);
        byteBuffer.putInt(TRIGGER_CONFIG_OFFSET, -1);
        byteBuffer.putInt(SOURCE_ID_OFFSET, sourceId);
        byteBuffer.putLong(DOM_ID_OFFSET, domId);
        byteBuffer.putShort(TRIGGER_MODE_OFFSET, (short) triggerMode);

        // flip buffer before returning it
        // no flip is necessary here since all puts use offsets (position never changes)
        //byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * generate a hit
     * @param hit generic hit container
     * @return ByteBuffer representation of hit
     */
    public ByteBuffer generatePayload(GenericHit hit) {
        return generatePayload(hit.getTimeStamp(), hit.getDomId(), hit.getSourceId(), hit.getTriggerMode());
    }

    /**
     * generate a hit
     * @param hit generic hit container
     * @return ByteBuffer representation of hit
     */
    public ByteBuffer generatePayload(GenericF2kHit hit) {
        return generatePayload(hit.getTimeStamp(), hit.getDomId(), hit.getSourceId(), hit.getTriggerMode());
    }

}
