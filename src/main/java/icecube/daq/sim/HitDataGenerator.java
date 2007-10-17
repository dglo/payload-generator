/*
 * class: HitDataGenerator
 *
 * Version $Id: HitDataGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 2 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of a HitDataPayload
 *
 * @version $Id: HitDataGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class HitDataGenerator
        implements IGenerator
{

    /**
     * payload format defaults
     */
    public static final int PAYLOAD_TYPE_DEFAULT = 10;

    /**
     * fields with a fixed size (in bytes)
     */
    private static final int PAYLOAD_LENGTH_SIZE = 4;
    private static final int PAYLOAD_TYPE_SIZE   = 4;
    private static final int PAYLOAD_TIME_SIZE   = 8;
    private static final int TRIGGER_CONFIG_SIZE = 4;
    private static final int SOURCE_ID_SIZE      = 4;

    private static final int PAYLOAD_ENVELOPE_SIZE = PAYLOAD_LENGTH_SIZE
                                                     + PAYLOAD_TYPE_SIZE
                                                     + PAYLOAD_TIME_SIZE
                                                     + TRIGGER_CONFIG_SIZE
                                                     + SOURCE_ID_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int PAYLOAD_LENGTH_OFFSET = 0;
    private static final int PAYLOAD_TYPE_OFFSET   = PAYLOAD_LENGTH_OFFSET + PAYLOAD_LENGTH_SIZE;
    private static final int PAYLOAD_TIME_OFFSET   = PAYLOAD_TYPE_OFFSET   + PAYLOAD_TYPE_SIZE;
    private static final int TRIGGER_CONFIG_OFFSET = PAYLOAD_TIME_OFFSET   + PAYLOAD_TIME_SIZE;
    private static final int SOURCE_ID_OFFSET      = TRIGGER_CONFIG_OFFSET + TRIGGER_CONFIG_SIZE;

    /**
     * waveform format defaults
     */
    private static final int NFADC_DEFAULT   = 56;
    private static final int ATWD_01_DEFAULT = 0x3f;
    private static final int ATWD_23_DEFAULT = 0x03;

    /**
     * payload format
     */
    private int payloadType;

    /**
     * length variables
     */
    private int payloadLength;

    /**
     * Engineering format generator
     */
    private EngFmtGenerator engFmtGenerator;

    /**
     * default constructor
     */
    public HitDataGenerator() {
        this(NFADC_DEFAULT, ATWD_01_DEFAULT, ATWD_23_DEFAULT);
    }

    /**
     * custom waveform constructor
     * @param nFadc number of fadc samples
     * @param atwd01 atwd format for channels 0 and 1
     * @param atwd23 atwd format for channels 2 and 3
     */
    public HitDataGenerator(int nFadc, int atwd01, int atwd23) {
        // use default format
        payloadType = PAYLOAD_TYPE_DEFAULT;
        engFmtGenerator = new EngFmtGenerator(nFadc, atwd01, atwd23);

        // calculate lengths
        payloadLength = PAYLOAD_ENVELOPE_SIZE + TestDaqHeader.getSize() + engFmtGenerator.getSize();
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
        byteBuffer.putInt(TRIGGER_CONFIG_OFFSET, -1);
        byteBuffer.putInt(SOURCE_ID_OFFSET, sourceId);

        // now engineering format
        byteBuffer.position(SOURCE_ID_OFFSET + SOURCE_ID_SIZE);
        byteBuffer.put(TestDaqHeader.createHeader(timeStamp, domId));
        byteBuffer.put(engFmtGenerator.generatePayload(timeStamp, domId, sourceId, triggerMode));

        // flip buffer before returning it
        byteBuffer.flip();
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
        int triggerMode = hit.getTriggerMode();
        int lcTag = hit.getLcTag();
        int shiftedLcTag = (lcTag << 4);
        int hitType = triggerMode | shiftedLcTag;
        return generatePayload(hit.getTimeStamp(), hit.getDomId(), hit.getSourceId(), hitType);
    }

}
