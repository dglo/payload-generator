/*
 * class: EngFmtGenerator
 *
 * Version $Id: DomHubHeader.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of a DomHub record header.
 *
 * @version $Id: DomHubHeader.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class DomHubHeader
{

    public static final int HIT_FORMAT_ID  = 2;
    public static final int MONI_FORMAT_ID = 102;
    public static final int TCAL_FORMAT_ID = 201;
    public static final int SN_FORMAT_ID   = 400;

    /**
     * Size (in bytes) of fields in wrapped record
     */
    private static final int WRAPPER_LENGTH_SIZE = 4;
    private static final int WRAPPER_FORMAT_SIZE = 4;
    private static final int DOMID_SIZE          = 8;

    private static final int WRAPPER_ENVELOPE_SIZE = WRAPPER_LENGTH_SIZE
                                                     + WRAPPER_FORMAT_SIZE
                                                     + DOMID_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int WRAPPER_LENGTH_OFFSET = 0;
    private static final int WRAPPER_FORMAT_OFFSET = WRAPPER_LENGTH_OFFSET + WRAPPER_LENGTH_SIZE;
    private static final int DOMID_OFFSET          = WRAPPER_FORMAT_OFFSET + WRAPPER_FORMAT_SIZE;

    private DomHubHeader() {
        
    }

    public static int getSize() {
        return WRAPPER_ENVELOPE_SIZE;
    }

    public static void fillTcalHeader(int tcalSize, String domId, ByteBuffer record) {

        // Assume that the buffer position is properly set and make sure that there is enough space
        if (record.remaining() < (WRAPPER_ENVELOPE_SIZE + tcalSize)) return;
        
        record.putInt(WRAPPER_ENVELOPE_SIZE + tcalSize);
        record.putInt(TCAL_FORMAT_ID);
        record.putLong(Long.parseLong(domId, 16));

    }

    /**
     * create a header
     * @param recordLength length of record to wrap
     * @param format record format
     * @param domId DOM id of hit
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createHeader(int recordLength, int format, long domId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(WRAPPER_ENVELOPE_SIZE);

        int totalLength = WRAPPER_ENVELOPE_SIZE + recordLength;

        // wrapper
        byteBuffer.putInt(WRAPPER_LENGTH_OFFSET, totalLength);
        byteBuffer.putInt(WRAPPER_FORMAT_OFFSET, format);
        byteBuffer.putLong(DOMID_OFFSET, domId);

        // flip buffer before returning it
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * Create a hit record header.
     * @param hitSize size of hit record
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createHitHeader(int hitSize, long domId) {
        return createHeader(hitSize, HIT_FORMAT_ID, domId);
    }

    /**
     * Create a tcal record header.
     * @param tcalSize size of tcal record
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createTcalHeader(int tcalSize, long domId) {
        return createHeader(tcalSize, TCAL_FORMAT_ID, domId);

    }

    /**
     * Create a moni record header.
     * @param moniSize size of moni record
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createMoniHeader(int moniSize, long domId) {
        return createHeader(moniSize, MONI_FORMAT_ID, domId);
    }

    /**
     * Create a supernova record header.
     * @param moniSize size of moni record
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createSNHeader(int moniSize, long domId) {
        return createHeader(moniSize, SN_FORMAT_ID, domId);
    }

}
