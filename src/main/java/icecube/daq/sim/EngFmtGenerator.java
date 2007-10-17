/*
 * class: EngFmtGenerator
 *
 * Version $Id: EngFmtGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of an Engineering Format hit.
 *
 * @version $Id: EngFmtGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class EngFmtGenerator
        implements IGenerator
{

    /**
     * fields with a fixed size (in bytes)
     */
    private static final int EVENT_LENGTH_SIZE   = 2;
    private static final int EVENT_FORMAT_SIZE   = 2;
    private static final int MISC_SIZE           = 1;
    private static final int NFADC_SIZE          = 1;
    private static final int ATWD_01_SIZE        = 1;
    private static final int ATWD_23_SIZE        = 1;
    private static final int TRIGGER_SIZE        = 1;
    private static final int SPARE_SIZE          = 1;
    private static final int TIME_STAMP_SIZE     = 6;

    private static final short EVENT_SIZE_NO_WAVEFORMS = EVENT_LENGTH_SIZE
                                                         + EVENT_FORMAT_SIZE
                                                         + MISC_SIZE
                                                         + NFADC_SIZE
                                                         + ATWD_01_SIZE
                                                         + ATWD_23_SIZE
                                                         + TRIGGER_SIZE
                                                         + SPARE_SIZE
                                                         + TIME_STAMP_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int EVENT_LENGTH_OFFSET   = 0;
    private static final int EVENT_FORMAT_OFFSET   = EVENT_LENGTH_OFFSET   + EVENT_LENGTH_SIZE;
    private static final int MISC_OFFSET           = EVENT_FORMAT_OFFSET   + EVENT_FORMAT_SIZE;
    private static final int NFADC_OFFSET          = MISC_OFFSET           + MISC_SIZE;
    private static final int ATWD_01_OFFSET        = NFADC_OFFSET          + NFADC_SIZE;
    private static final int ATWD_23_OFFSET        = ATWD_01_OFFSET        + ATWD_01_SIZE;
    private static final int TRIGGER_OFFSET        = ATWD_23_OFFSET        + ATWD_23_SIZE;
    private static final int SPARE_OFFSET          = TRIGGER_OFFSET        + TRIGGER_SIZE;
    private static final int TIME_STAMP_OFFSET     = SPARE_OFFSET          + SPARE_SIZE;

    /**
     * format defaults
     */
    private static final int EVENT_FORMAT_DEFAULT =  2;

    /**
     * waveform format defaults
     */
    private static final int NFADC_DEFAULT   = 56;
    private static final int ATWD_01_DEFAULT = 0x3f;
    private static final int ATWD_23_DEFAULT = 0x03;

    /**
     * format
     */
    private int eventFormat;

    /**
     * waveform format
     */
    private int nFadc;
    private int atwd01;
    private int atwd23;

    /**
     * length variables
     */
    private int fadcLength;
    private int atwd01Length;
    private int atwd23Length;
    private int eventLength;

    /**
     * default constructor
     */
    public EngFmtGenerator() {
        this(NFADC_DEFAULT, ATWD_01_DEFAULT, ATWD_23_DEFAULT);
    }

    /**
     * custom waveform constructor
     * @param nFadc number of fadc samples
     * @param atwd01 atwd format for channels 0 and 1
     * @param atwd23 atwd format for channels 2 and 3
     */
    public EngFmtGenerator(int nFadc, int atwd01, int atwd23) {
        // use default format
        eventFormat = EVENT_FORMAT_DEFAULT;

        // use custom waveform
        this.nFadc  = nFadc;
        this.atwd01 = atwd01;
        this.atwd23 = atwd23;

        // calculate lengths
        fadcLength    = calcFadcLength(nFadc);
        atwd01Length  = calcAtwdLength(atwd01);
        atwd23Length  = calcAtwdLength(atwd23);
        eventLength   = EVENT_SIZE_NO_WAVEFORMS + fadcLength + atwd01Length + atwd23Length;
    }

    public int getSize() {
        return eventLength;
    }

    /**
     * calculates the length (in bytes) of the fadc field
     * @param nFadc number of fadc samples
     * @return fadc length
     */
    private int calcFadcLength(int nFadc) {
        return 2*nFadc;
    }

    /**
     * calculates the lenght (in bytes) of an atwd pair (01 or 23)
     * @param atwd format of pair
     * @return length
     */
    private int calcAtwdLength(int atwd) {
        int atwdDatum;
        int atwdSamples;
        int atwdLength;

        // first channel of pair
        if ((atwd & 0x01) == 0) {
            atwdDatum = 0;
        } else if ((atwd & 0x02) == 0) {
            atwdDatum = 1;
        } else {
            atwdDatum = 2;
        }

        if ((atwd & 0x04) == 0) {
            if ((atwd & 0x08) == 0) {
                atwdSamples = 32;
            } else {
                atwdSamples = 16;
            }
        } else {
            if ((atwd & 0x08) == 0) {
                atwdSamples = 64;
            } else {
                atwdSamples = 128;
            }
        }
        atwdLength = atwdDatum*atwdSamples;

        // second channel of pair
        if ((atwd & 0x10) == 0) {
            atwdDatum = 0;
        } else if ((atwd & 0x20) == 0) {
            atwdDatum = 1;
        } else {
            atwdDatum = 2;
        }

        if ((atwd & 0x40) == 0) {
            if ((atwd & 0x80) == 0) {
                atwdSamples = 32;
            } else {
                atwdSamples = 16;
            }
        } else {
            if ((atwd & 0x80) == 0) {
                atwdSamples = 64;
            } else {
                atwdSamples = 128;
            }
        }
        atwdLength += atwdDatum*atwdSamples;

        return atwdLength;
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
        ByteBuffer byteBuffer = ByteBuffer.allocate(eventLength);

        // hit
        byteBuffer.putShort(EVENT_LENGTH_OFFSET, new Integer(eventLength).shortValue());
        byteBuffer.putShort(EVENT_FORMAT_OFFSET, new Integer(eventFormat).shortValue());
        byteBuffer.put(MISC_OFFSET, new Integer(0).byteValue());
        byteBuffer.put(NFADC_OFFSET, new Integer(nFadc).byteValue());
        byteBuffer.put(ATWD_01_OFFSET, new Integer(atwd01).byteValue());
        byteBuffer.put(ATWD_23_OFFSET, new Integer(atwd23).byteValue());
        byteBuffer.put(TRIGGER_OFFSET, new Integer(triggerMode).byteValue());
        byteBuffer.put(SPARE_OFFSET, new Integer(0).byteValue());

        byte[] timeBytes = new byte[6];
        for (int i = timeBytes.length - 1; i >= 0; i--) {
            timeBytes[i] = (byte) (timeStamp & 0xffL);
            timeStamp >>= 8;
        }

        byteBuffer.position(TIME_STAMP_OFFSET);
        byteBuffer.put(timeBytes);

        // waveform
        if (fadcLength > 0) {
            byteBuffer.put( new byte[fadcLength] );
        }
        if (atwd01Length > 0) {
            byteBuffer.put( new byte[atwd01Length] );
        }
        if (atwd23Length > 0) {
            byteBuffer.put( new byte[atwd23Length] );
        }

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
        int lcTag = hit.getLcTag() << 4;
        int hitType = triggerMode & lcTag;
        return generatePayload(hit.getTimeStamp(), hit.getDomId(), hit.getSourceId(), hitType);
    }

}
