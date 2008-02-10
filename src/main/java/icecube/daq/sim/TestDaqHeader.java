/*
 * class: EngFmtGenerator
 *
 * Version $Id: TestDaqHeader.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of a TestDAQ hit record header.
 *
 * @version $Id: TestDaqHeader.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public final class TestDaqHeader
{

    /**
     * fields with a fixed size (in bytes)
     */
    private static final int WRAPPER_LENGTH_SIZE = 4;
    private static final int WRAPPER_ID_SIZE     = 4;
    private static final int DOM_ID_SIZE         = 8;
    private static final int SKIPPED_SIZE        = 8;
    private static final int CALIB_TIME_SIZE     = 8;

    private static final int WRAPPER_ENVELOPE_SIZE = WRAPPER_LENGTH_SIZE
                                                     + WRAPPER_ID_SIZE
                                                     + DOM_ID_SIZE
                                                     + SKIPPED_SIZE
                                                     + CALIB_TIME_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int WRAPPER_LENGTH_OFFSET = 0;
    private static final int WRAPPER_ID_OFFSET     = WRAPPER_LENGTH_OFFSET + WRAPPER_LENGTH_SIZE;
    private static final int DOM_ID_OFFSET         = WRAPPER_ID_OFFSET     + WRAPPER_ID_SIZE;
    private static final int SKIPPED_OFFSET        = DOM_ID_OFFSET         + DOM_ID_SIZE;
    private static final int CALIB_TIME_OFFSET     = SKIPPED_OFFSET        + SKIPPED_SIZE;

    /**
     * format defaults
     */
    private static final int WRAPPER_ID_DEFAULT =  2;

    private TestDaqHeader() {

    }

    public static int getSize() {
        return WRAPPER_ENVELOPE_SIZE;
    }

    /**
     * create a header
     * @param timeStamp corrected time of hit
     * @param domId DOM id of hit
     * @return ByteBuffer representation of hit
     */
    public static ByteBuffer createHeader(long timeStamp, long domId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(WRAPPER_ENVELOPE_SIZE);

        // wrapper
        byteBuffer.putInt(WRAPPER_LENGTH_OFFSET, WRAPPER_ENVELOPE_SIZE);
        byteBuffer.putInt(WRAPPER_ID_OFFSET, WRAPPER_ID_DEFAULT);
        byteBuffer.putLong(DOM_ID_OFFSET, domId);
        byteBuffer.putLong(SKIPPED_OFFSET, 0);
        byteBuffer.putLong(CALIB_TIME_OFFSET, timeStamp);

        return byteBuffer;
    }

}
