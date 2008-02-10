/*
 * class: EngFmtGenerator
 *
 * Version $Id: DomHubHeader.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of a DomHub record header.
 *
 * @version $Id: DomHubHeader.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public final class DomHubHeader
{

    public static final int HIT_FORMAT_ID = 101;
    public static final int TCAL_FORMAT_ID = 201;

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

    /**
     * create a header
     * @param format record format
     * @param domId DOM id of hit
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createHeader(int format, long domId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(WRAPPER_ENVELOPE_SIZE);

        // wrapper
        byteBuffer.putInt(WRAPPER_LENGTH_OFFSET, WRAPPER_ENVELOPE_SIZE);
        byteBuffer.putInt(WRAPPER_FORMAT_OFFSET, format);
        byteBuffer.putLong(DOMID_OFFSET, domId);

        // flip buffer before returning it
        byteBuffer.flip();
        return byteBuffer;
    }

    /**
     * Create a hit record header.
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createHitHeader(long domId) {
        return createHeader(HIT_FORMAT_ID, domId);
    }

    /**
     * Create a tcal record header.
     * @param domId dom id
     * @return ByteBuffer representation of header
     */
    public static ByteBuffer createTcalHeader(long domId) {
        return createHeader(TCAL_FORMAT_ID, domId);
    }

}
