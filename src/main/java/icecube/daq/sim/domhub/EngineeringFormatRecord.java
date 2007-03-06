/*
 * class: EngFmtGenerator
 *
 * Version $Id: EngineeringFormatRecord.java,v 1.2 2006/06/03 02:39:54 toale Exp $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;

/**
 * This class produces a ByteBuffer representation of an Engineering Format hit.
 *
 * @version $Id: EngineeringFormatRecord.java,v 1.2 2006/06/03 02:39:54 toale Exp $
 * @author pat
 */
public class EngineeringFormatRecord
        implements IRecord
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(EngineeringFormatRecord.class);

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
    private static final int NFADC_DEFAULT   = 50;
    private static final int ATWD_01_DEFAULT = 0x3f;
    private static final int ATWD_23_DEFAULT = 0x03;

    // table to convert a nibble to a hex char.
    static char[] hexChar = {
       '0' , '1' , '2' , '3' ,
       '4' , '5' , '6' , '7' ,
       '8' , '9' , 'a' , 'b' ,
       'c' , 'd' , 'e' , 'f'};

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
     * Service for producing tcal timing data
     */
    private TimeConverter timeConverter;

    /**
     * Default constructor, uses default waveform sizes and time converter.
     */
    public EngineeringFormatRecord() {
        this(NFADC_DEFAULT, ATWD_01_DEFAULT, ATWD_23_DEFAULT, null);
    }

    /**
     * Constructor the takes a timeConverter, uses default waveform sizes.
     *
     * @param timeConverter utility for converting from UTC to DOM time
     */
    public EngineeringFormatRecord(TimeConverter timeConverter) {
        this(NFADC_DEFAULT, ATWD_01_DEFAULT, ATWD_23_DEFAULT, timeConverter);
    }

    /**
     * Custom waveform constructor.
     *
     * @param nFadc number of fadc samples
     * @param atwd01 atwd format for channels 0 and 1
     * @param atwd23 atwd format for channels 2 and 3
     * @param timeConverter utility for converting from UTC to DOM time
     */
    public EngineeringFormatRecord(int nFadc, int atwd01, int atwd23, TimeConverter timeConverter) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing EngineeringFormatRecord with:\n"
                        + "   nFadc = " + nFadc + "\n"
                        + "  atwd01 = " + atwd01 + "\n"
                        + "  atwd23 = " + atwd23;
            log.info(dump);
        }

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

        if (null == timeConverter) {
            this.timeConverter = new TimeConverter(0);
        } else {
            this.timeConverter = timeConverter;
        }
    }

    /**
     * Get the size of the record.
     *
     * @return size in bytes
     */
    public int getSize() {
        return eventLength;
    }

    /**
     * Calculates the length (in bytes) of the fadc field.
     *
     * @param nFadc number of fadc samples
     * @return fadc length
     */
    private static int calcFadcLength(int nFadc) {
        return 2*nFadc;
    }

    /**
     * Calculates the length (in bytes) of an atwd pair (01 or 23).
     *
     * @param atwd format of pair
     * @return length
     */
    private static int calcAtwdLength(int atwd) {
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
     * Generate a real hit record.
     *
     * @param timeStamp dom clock time of hit
     * @param triggerMode DOM triggering mode (hitType)
     * @return ByteBuffer representation of hit
     */
    private ByteBuffer generateRecord(long timeStamp, int triggerMode) {
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
        byteBuffer.position(TIME_STAMP_OFFSET);

        byte time[] = timeStampToBytes(timeStamp);
        byteBuffer.put(time);

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
     * Generate a real record from a generic record.
     *
     * @param generic generic record
     * @return ByteBuffer representation of real record
     */
    public ByteBuffer generateRecord(IGenericRecord generic) {
        if (generic instanceof GenericHitRecord) {
            GenericHitRecord hit = (GenericHitRecord) generic;
            long domTime = timeConverter.getDomClockFromUtcTime(hit.getUtcTime());
            if (log.isDebugEnabled()) {
                log.debug("Converting: utcTime = " + hit.getUtcTime() + "  domTime = " + domTime);
            }
            return generateRecord(domTime, hit.getHitType());
        } else {
            return null;
        }
    }

    public static String dumpRecord(ByteBuffer record) {
        short eventLength = record.getShort(EVENT_LENGTH_OFFSET);
        short eventFormat = record.getShort(EVENT_FORMAT_OFFSET);
        byte misc = record.get(MISC_OFFSET);
        byte nFadc = record.get(NFADC_OFFSET);
        byte atwd01 = record.get(ATWD_01_OFFSET);
        byte atwd23 = record.get(ATWD_23_OFFSET);
        byte trigger = record.get(TRIGGER_OFFSET);
        byte spare = record.get(SPARE_OFFSET);
        record.position(TIME_STAMP_OFFSET);
        byte[] time = new byte[TIME_STAMP_SIZE];
        record.get(time);
        byte[] fadcWF = new byte[calcFadcLength(nFadc)];
        byte[] atwd01WF = new byte[calcAtwdLength(atwd01)];
        byte[] atwd23WF = new byte[calcAtwdLength(atwd23)];
        record.get(fadcWF);
        record.get(atwd01WF);
        record.get(atwd23WF);

        StringBuffer buffer = new StringBuffer("EngineeringFormat dump:\n");
        buffer.append("  EventLength = " + eventLength + "\n");
        buffer.append("  EventFormat = " + eventFormat + "\n");
        buffer.append("  MiscByte    = " + misc + "\n");
        buffer.append("  NumFADC     = " + nFadc + "\n");
        buffer.append("  ATWD01Flag  = " + atwd01 + "\n");
        buffer.append("  ATWD23Flag  = " + atwd23 + "\n");
        buffer.append("  Trigger     = " + trigger + "\n");
        buffer.append("  Spare       = " + spare + "\n");
        buffer.append("  TimeStamp   = " + timeStampToLong(time) + "\n");
        buffer.append("  FADC        = 0x" + toHexString(fadcWF) + "\n");
        buffer.append("  ATWD01      = 0x" + toHexString(atwd01WF) + "\n");
        buffer.append("  ATWD23      = 0x" + toHexString(atwd23WF) + "\n");
        return buffer.toString();
    }

    public static long timeStampToLong(byte[] time6B) {
        byte time8B[] = new byte[8];
        time8B[0] = 0;
        time8B[1] = 0;
        time8B[2] = time6B[0];
        time8B[3] = time6B[1];
        time8B[4] = time6B[2];
        time8B[5] = time6B[3];
        time8B[6] = time6B[4];
        time8B[7] = time6B[5];
        ByteBuffer buffer = ByteBuffer.wrap(time8B);
        return buffer.getLong();
    }

    public static byte[] timeStampToBytes(long time) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(time);
        byte[] time8B = buffer.array();
        byte[] time6B = new byte[6];
        time6B[0] = time8B[2];
        time6B[1] = time8B[3];
        time6B[2] = time8B[4];
        time6B[3] = time8B[5];
        time6B[4] = time8B[6];
        time6B[5] = time8B[7];
        return time6B;
    }

    public static String toHexString ( byte[] b ) {
        StringBuffer sb = new StringBuffer( b.length * 2 );
        for ( int i=0; i<b.length; i++ ) {
            // look up high nibble char
            sb.append( hexChar [( b[i] & 0xf0 ) >>> 4] );

            // look up low nibble char
            sb.append( hexChar [b[i] & 0x0f] );
        }
        return sb.toString();
    }

}
