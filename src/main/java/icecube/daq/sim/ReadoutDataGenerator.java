/*
 * class: ReadoutDataGenerator
 *
 * Version $Id: ReadoutDataGenerator.java 2631 2008-02-11 06:27:31Z dglo $
 *
 * Date: August 17 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class produces a ByteBuffer representation of a ReadoutDataPayload
 *
 * @version $Id: ReadoutDataGenerator.java 2631 2008-02-11 06:27:31Z dglo $
 * @author dglo
 */
public class ReadoutDataGenerator
{
    /**
     * payload format defaults
     */
    public static final int PAYLOAD_TYPE_DEFAULT = 11;

    /**
     * fields with a fixed size (in bytes)
     */
    private static final int PAYLOAD_LENGTH_SIZE = 4;
    private static final int PAYLOAD_TYPE_SIZE   = 4;
    private static final int PAYLOAD_TIME_SIZE   = 8;
    private static final int RECORD_TYPE_SIZE    = 2;
    private static final int TRIGGER_UID_SIZE    = 4;
    private static final int PAYLOAD_NUM_SIZE    = 2;
    private static final int PAYLOAD_LAST_SIZE   = 2;
    private static final int SOURCE_ID_SIZE      = 4;
    private static final int FIRST_UTCTIME_SIZE  = 8;
    private static final int LAST_UTCTIME_SIZE   = 8;
    private static final int COMPOSITE_LEN_SIZE  = 4;
    private static final int COMPOSITE_TYPE_SIZE = 2;
    private static final int NUM_PAYLOADS_SIZE   = 2;

    private static final int PAYLOAD_ENVELOPE_SIZE = PAYLOAD_LENGTH_SIZE
        + PAYLOAD_TYPE_SIZE
        + PAYLOAD_TIME_SIZE
        + RECORD_TYPE_SIZE
        + TRIGGER_UID_SIZE
        + PAYLOAD_NUM_SIZE
        + PAYLOAD_LAST_SIZE
        + SOURCE_ID_SIZE
        + FIRST_UTCTIME_SIZE
        + LAST_UTCTIME_SIZE
        + COMPOSITE_LEN_SIZE
        + COMPOSITE_TYPE_SIZE
        + NUM_PAYLOADS_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int PAYLOAD_LENGTH_OFFSET = 0;
    private static final int PAYLOAD_TYPE_OFFSET =
        PAYLOAD_LENGTH_OFFSET + PAYLOAD_LENGTH_SIZE;
    private static final int PAYLOAD_TIME_OFFSET =
        PAYLOAD_TYPE_OFFSET   + PAYLOAD_TYPE_SIZE;
    private static final int RECORD_TYPE_OFFSET =
        PAYLOAD_TIME_OFFSET   + PAYLOAD_TIME_SIZE;
    private static final int TRIGGER_UID_OFFSET =
        RECORD_TYPE_OFFSET   + RECORD_TYPE_SIZE;
    private static final int PAYLOAD_NUM_OFFSET =
        TRIGGER_UID_OFFSET   + TRIGGER_UID_SIZE;
    private static final int PAYLOAD_LAST_OFFSET =
        PAYLOAD_NUM_OFFSET   + PAYLOAD_NUM_SIZE;
    private static final int SOURCE_ID_OFFSET =
        PAYLOAD_LAST_OFFSET + PAYLOAD_LAST_SIZE;
    private static final int FIRST_UTCTIME_OFFSET =
        SOURCE_ID_OFFSET + SOURCE_ID_SIZE;
    private static final int LAST_UTCTIME_OFFSET =
        FIRST_UTCTIME_OFFSET + FIRST_UTCTIME_SIZE;
    private static final int COMPOSITE_LEN_OFFSET =
        LAST_UTCTIME_OFFSET + LAST_UTCTIME_SIZE;
    private static final int COMPOSITE_TYPE_OFFSET =
        COMPOSITE_LEN_OFFSET + COMPOSITE_LEN_SIZE;
    private static final int NUM_PAYLOADS_OFFSET =
        COMPOSITE_TYPE_OFFSET + COMPOSITE_TYPE_SIZE;

    /**
     * payload format
     */
    private int payloadType;

    private IGenerator hitGenerator;

    /**
     * default constructor
     */
    public ReadoutDataGenerator()
    {
        this(new HitDataGenerator());
    }

    /**
     * default constructor
     */
    public ReadoutDataGenerator(IGenerator hitGen)
    {
        // use default format
        payloadType = PAYLOAD_TYPE_DEFAULT;
        hitGenerator = hitGen;
    }

    /**
     * generate readout data
     * @param timeStamp corrected time of hit
     * @param domId DOM id of hit
     * @param sourceId source of hit
     * @param triggerMode DOM triggering mode
     * @return ByteBuffer representation of hit
     */
    public ByteBuffer generatePayload(short recType, int trigUID,
                                      short payloadNum, short payloadLast,
                                      int sourceId, long firstTime,
                                      long lastTime, List hits)
    {
        ArrayList hitBufs = new ArrayList();

        int compBytes = 0;
        short compType = -1;

        Iterator hitIter = hits.iterator();
        while (hitIter.hasNext()) {
            GenericHit hit = (GenericHit) hitIter.next();
            ByteBuffer hitBuf = hitGenerator.generatePayload(hit);

            compBytes += hitBuf.limit();
            if (compType < 0) {
                compType = (short) hitBuf.getInt(PAYLOAD_TYPE_OFFSET);
            }

            hitBufs.add(hitBuf);
        }

        final int payloadLength = PAYLOAD_ENVELOPE_SIZE + compBytes;

        ByteBuffer byteBuffer = ByteBuffer.allocate(payloadLength);

        // payload envelope
        byteBuffer.putInt(PAYLOAD_LENGTH_OFFSET, payloadLength);
        byteBuffer.putInt(PAYLOAD_TYPE_OFFSET, payloadType);
        byteBuffer.putLong(PAYLOAD_TIME_OFFSET, lastTime);
        byteBuffer.putShort(RECORD_TYPE_OFFSET, recType);
        byteBuffer.putInt(TRIGGER_UID_OFFSET, trigUID);
        byteBuffer.putShort(PAYLOAD_NUM_OFFSET, payloadNum);
        byteBuffer.putShort(PAYLOAD_LAST_OFFSET, payloadLast);
        byteBuffer.putInt(SOURCE_ID_OFFSET, sourceId);
        byteBuffer.putLong(FIRST_UTCTIME_OFFSET, firstTime);
        byteBuffer.putLong(LAST_UTCTIME_OFFSET, lastTime);

        // now composite payload
        byteBuffer.putInt(COMPOSITE_LEN_OFFSET, compBytes);
        byteBuffer.putShort(COMPOSITE_TYPE_OFFSET, compType);
        byteBuffer.putShort(NUM_PAYLOADS_OFFSET, (short) hitBufs.size());

        // now hit payloads
        byteBuffer.position(NUM_PAYLOADS_OFFSET + NUM_PAYLOADS_SIZE);
        Iterator bbIter = hitBufs.iterator();
        while (bbIter.hasNext()) {
            ByteBuffer bb = (ByteBuffer) bbIter.next();
            byteBuffer.put(bb);
        }

        // flip buffer before returning it
        byteBuffer.flip();
        return byteBuffer;
    }
}
