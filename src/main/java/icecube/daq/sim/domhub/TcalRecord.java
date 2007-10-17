/*
 * class: TCalGenerator
 *
 * Version $Id: TcalRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: August 13 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * This class generates TCAL records based on a template which may or may not
 * represent a valid tcal record.
 *
 * @version $Id: TcalRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class TcalRecord implements IRecord
{

    /**
     * Log object for this class.
     */
    private static final Log log = LogFactory.getLog(TcalRecord.class);

    /**
     * Hex representation of ASCII characters used in GPS string
     */
    private static final byte ASCII_SOH      = 0x01; // Start of header
    private static final byte ASCII_COLON    = 0x3a; // Separator
    private static final byte ASCII_SPACE    = 0x20; // Quality: < 1   microsecond
    private static final byte ASCII_PERIOD   = 0x2e; // Quality: < 10  microseconds
    private static final byte ASCII_ASTERISK = 0x2a; // Quality: < 100 microseconds
    private static final byte ASCII_POUND    = 0x23; // Quality: < 1   millisecond
    private static final byte ASCII_QUESTION = 0x3f; // Quality: > 1   millisecond
    private static final byte ASCII_ZERO     = 0x30; // Digits 0-9;
    private static final byte ASCII_ONE      = 0x31;
    private static final byte ASCII_TWO      = 0x32;
    private static final byte ASCII_THREE    = 0x33;
    private static final byte ASCII_FOUR     = 0x34;
    private static final byte ASCII_FIVE     = 0x35;
    private static final byte ASCII_SIX      = 0x36;
    private static final byte ASCII_SEVEN    = 0x37;
    private static final byte ASCII_EIGHT    = 0x38;
    private static final byte ASCII_NINE     = 0x39;

    /**
     * Size (in bytes) of fields in record
     */
    private static final int RECORD_LENGTH_SIZE  = 2;
    private static final int RECORD_FORMAT_SIZE  = 1;
    private static final int RECORD_BLANK_SIZE   = 1;
    private static final int CLOCK_COUNT_SIZE    = 8; // shows up 4 times in record
    private static final int WAVEFORM_DATA_SIZE  = 64*2; // shows up 2 times in record
    private static final int GPS_STRING_SIZE     = 14;
    private static final int GPS_DOR_COUNT_SIZE  = 8;

    /**
     * Derived sizes (RECORD_LENGTH does not include RECORD_HEADER_SIZE)
     */
    private static final int RECORD_HEADER_SIZE = RECORD_LENGTH_SIZE + RECORD_FORMAT_SIZE + RECORD_BLANK_SIZE;
    private static final int RECORD_LENGTH      = 4*CLOCK_COUNT_SIZE + 2*WAVEFORM_DATA_SIZE;
    private static final int GPS_DATA_SIZE      = GPS_STRING_SIZE + GPS_DOR_COUNT_SIZE;
    private static final int TOTAL_LENGTH       = RECORD_HEADER_SIZE + RECORD_LENGTH + GPS_DATA_SIZE;

    /**
     * Offsets into buffer
     */
    private static final int RECORD_LENGTH_OFFSET  = 0;
    private static final int RECORD_FORMAT_OFFSET  = RECORD_LENGTH_OFFSET + RECORD_LENGTH_SIZE;
    private static final int RECORD_BLANK_OFFSET   = RECORD_FORMAT_OFFSET + RECORD_FORMAT_SIZE;
    private static final int DOR_TX_OFFSET         = RECORD_BLANK_OFFSET + RECORD_BLANK_SIZE;
    private static final int DOR_RX_OFFSET         = DOR_TX_OFFSET + CLOCK_COUNT_SIZE;
    private static final int DOR_WF_OFFSET         = DOR_RX_OFFSET + CLOCK_COUNT_SIZE;
    private static final int DOM_RX_OFFSET         = DOR_WF_OFFSET + WAVEFORM_DATA_SIZE;
    private static final int DOM_TX_OFFSET         = DOM_RX_OFFSET + CLOCK_COUNT_SIZE;
    private static final int DOM_WF_OFFSET         = DOM_TX_OFFSET + CLOCK_COUNT_SIZE;
    private static final int GPS_STRING_OFFSET     = DOM_WF_OFFSET + WAVEFORM_DATA_SIZE;
    private static final int GPS_DOR_COUNT_OFFSET  = GPS_STRING_OFFSET + GPS_STRING_SIZE;

    /**
     * Default values for fixed fields
     */
    private static final short RECORD_LENGTH_DEFAULT  = 0x00e0;
    private static final byte  RECORD_FORMAT_DEFAULT  = 0x01;
    private static final byte  RECORD_BLANK_DEFAULT   = 0x00;
    private static final short DOR_WAVEFORM_DEFAULT[] = {
        0x0208, 0x0208, 0x0209, 0x0208, 0x0209, 0x0207, 0x0208, 0x0208,
        0x0209, 0x0208, 0x0208, 0x0207, 0x0207, 0x0206, 0x0208, 0x0206,
        0x0208, 0x0208, 0x0208, 0x0206, 0x0207, 0x0207, 0x0208, 0x020a,
        0x0213, 0x0227, 0x0246, 0x026c, 0x0295, 0x02c0, 0x02e8, 0x030d,
        0x032c, 0x0348, 0x0352, 0x0342, 0x031a, 0x02e2, 0x029f, 0x025a,
        0x0216, 0x01d8, 0x01a0, 0x016f, 0x0148, 0x012f, 0x0127, 0x0128,
        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000
    };
    private static final short DOM_WAVEFORM_DEFAULT[] = {
        0x01ff, 0x01fe, 0x0200, 0x01ff, 0x0200, 0x0202, 0x0201, 0x0200,
        0x0200, 0x0200, 0x0201, 0x0202, 0x0201, 0x0200, 0x0200, 0x01ff,
        0x01ff, 0x0200, 0x0200, 0x01ff, 0x0200, 0x0200, 0x0200, 0x0203,
        0x0207, 0x0215, 0x0229, 0x0245, 0x0262, 0x0280, 0x029e, 0x02b9,
        0x02d2, 0x02e7, 0x02f1, 0x02ec, 0x02d1, 0x02aa, 0x027c, 0x024b,
        0x0218, 0x01eb, 0x01c0, 0x019b, 0x017c, 0x0168, 0x0160, 0x015f,
        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000,
        0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000, 0x0000
    };

    /**
     * Values for initializing record factory
     */
    private static final long DOR_TX_TEMPLATE = 19640336501L;
    private static final long DOR_RX_TEMPLATE = 19640337530L;
    private static final long DOM_RX_TEMPLATE = 14589485741L;
    private static final long DOM_TX_TEMPLATE = 14589486351L;
    private long gpsOffset;

    /**
     * variables for record fields
     */
    private short recordLength  = RECORD_LENGTH_DEFAULT;
    private byte  recordFormat  = RECORD_FORMAT_DEFAULT;
    private byte  recordBlank   = RECORD_BLANK_DEFAULT;
    private long  dorTx;
    private long  dorRx;
    private short dorWf[]       = DOR_WAVEFORM_DEFAULT;
    private long  domRx;
    private long  domTx;
    private short domWf[]       = DOM_WAVEFORM_DEFAULT;
    private byte  gpsString[]   = new byte[14];
    private long  dorGps;

    /**
     * Service for producing tcal timing data
     */
    private TimeConverter timeConverter;

    /**
     * Default constructor, uses 0 offset and template tcal record.
     */
    public TcalRecord() {
        this(0);
    }

    /**
     * Constructor that takes a configured TimeConverter object.
     *
     * @param timeConverter time converter service to use
     */
    public TcalRecord(TimeConverter timeConverter) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing TcalRecord";
            log.info(dump);
        }
        this.timeConverter = timeConverter;
    }

    /**
     * Constructor that takes the gpsOffset.
     *
     * @param gpsOffset IceCube UTC time at which the DOR counter was reset (in seconds since Jan 1)
     */
    public TcalRecord(long gpsOffset) {
        this(DOR_TX_TEMPLATE, DOR_RX_TEMPLATE, DOM_RX_TEMPLATE, DOM_TX_TEMPLATE, gpsOffset);
    }

    /**
     * Constructor that takes dor and dom parameters and gpsOffset.
     *
     * @param dorTx a DOR send count
     * @param dorRx a DOR recieve count
     * @param domRx a DOM recieve count
     * @param domTx a DOM send count
     * @param gpsOffset gps offset time
     */
    public TcalRecord(long dorTx, long dorRx, long domRx, long domTx, long gpsOffset) {
        this.gpsOffset = gpsOffset;

        timeConverter = new TimeConverter(dorTx, dorRx, domRx, domTx, this.gpsOffset);
    }

    /**
     * Generate a ByteBuffer containing a tcal record at time utcTime.
     *
     * @param utcTime UTC time of record, in units of 1/10 ns
     * @return a valid tcal record, in the form of a ByteBuffer
     */
    private ByteBuffer generateRecord(long utcTime) {

        // allocate byte buffer
        ByteBuffer buffer = ByteBuffer.allocate(TOTAL_LENGTH);

        // get record based on this utcTime
        dorTx = timeConverter.getDorClockFromUtcTime(utcTime);
        dorRx = timeConverter.getDorRxFromDorTx(dorTx);
        domRx = timeConverter.getDomRxFromDorTx(dorTx);
        domTx = timeConverter.getDomTxFromDomRx(domRx);
        // do an integer divide to get a whole number of seconds
        long gpsTime = utcTime/10000000000L;
        dorGps = timeConverter.getDorClockFromUtcTime(10000000000L*gpsTime);
        gpsString = formGpsString(gpsTime);

        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // write to buffer
        buffer.putShort(RECORD_LENGTH_OFFSET, recordLength);
        buffer.put(RECORD_FORMAT_OFFSET, recordFormat);
        buffer.put(RECORD_BLANK_OFFSET, recordBlank);
        buffer.putLong(DOR_TX_OFFSET, dorTx);
        buffer.putLong(DOR_RX_OFFSET, dorRx);
        for (int i=0; i<dorWf.length; i++) {
            buffer.putShort(DOR_WF_OFFSET + 2*i, dorWf[i]);
        }
        buffer.putLong(DOM_RX_OFFSET, domRx);
        buffer.putLong(DOM_TX_OFFSET, domTx);
        for (int i=0; i<domWf.length; i++) {
            buffer.putShort(DOM_WF_OFFSET + 2*i, domWf[i]);
        }

        buffer.order(ByteOrder.BIG_ENDIAN);

        for (int i=0; i<gpsString.length; i++) {
            buffer.put(GPS_STRING_OFFSET + i, gpsString[i]);
        }
        buffer.putLong(GPS_DOR_COUNT_OFFSET, dorGps);

        return buffer;
    }

    public static void fillRecord(long utcTime, String domId, ByteBuffer record) {

        // check capacity of record
        if (record.remaining() < TOTAL_LENGTH) return;

        // calculate the needed time stamps
        long dorTx = TimeConverterService.getDorClockFromUtcTime(utcTime, domId);
        long dorRx = TimeConverterService.getDorRxFromDorTx(dorTx, domId);
        long domRx = TimeConverterService.getDomRxFromDorTx(dorTx, domId);
        long domTx = TimeConverterService.getDomTxFromDomRx(domRx, domId);

        long gpsTime = utcTime/10000000000L;
        long dorGps = TimeConverterService.getDorClockFromUtcTime(10000000000L*gpsTime, domId);
        byte[] gpsString = formGpsString(gpsTime);

        // write the record
        record.order(ByteOrder.LITTLE_ENDIAN);
        record.putShort(RECORD_LENGTH_DEFAULT);
        record.put(RECORD_FORMAT_DEFAULT);
        record.put(RECORD_BLANK_DEFAULT);
        record.putLong(dorTx);
        record.putLong(dorRx);
        for (int i=0; i<DOR_WAVEFORM_DEFAULT.length; i++) {
            record.putShort(DOR_WAVEFORM_DEFAULT[i]);
        }
        record.putLong(domRx);
        record.putLong(domTx);
        for (int i=0; i<DOM_WAVEFORM_DEFAULT.length; i++) {
            record.putShort(DOM_WAVEFORM_DEFAULT[i]);
        }
        record.order(ByteOrder.BIG_ENDIAN);
        for (int i=0; i<gpsString.length; i++) {
            record.put(gpsString[i]);
        }
        record.putLong(dorGps);

    }

    /**
     * Create a byte array with a proper GPS string based on a utcTime.
     *
     * @param utcTime time of GPS record
     * @return byte array with a formated GPS string
     */
    private static byte[] formGpsString(long utcTime) {

        long gpsTime = utcTime;
        byte gpsArray[] = new byte[14];

        // fill SOH byte
        gpsArray[0] = ASCII_SOH;

        // get number of days
        // todo- figure out the one-day-offset issue (for now add a day here)
        long nDays = 1 + (gpsTime/(24*60*60));
        gpsTime %= (24*60*60);

        char days[] =  String.valueOf(nDays).toCharArray();
        if (nDays < 10) {
            gpsArray[1] = ASCII_ZERO;
            gpsArray[2] = ASCII_ZERO;
            gpsArray[3] = getAscii(days[0]);
        } else if (nDays < 100) {
            gpsArray[1] = ASCII_ZERO;
            gpsArray[2] = getAscii(days[0]);
            gpsArray[3] = getAscii(days[1]);
        } else {
            gpsArray[1] = getAscii(days[0]);
            gpsArray[2] = getAscii(days[1]);
            gpsArray[3] = getAscii(days[2]);
        }
        gpsArray[4] = ASCII_COLON;

        // get number of hours
        long nHours = gpsTime / (60*60);
        gpsTime %= (60*60);

        char hours[] =  String.valueOf(nHours).toCharArray();
        if (nHours < 10) {
            gpsArray[5] = ASCII_ZERO;
            gpsArray[6] = getAscii(hours[0]);
        } else {
            gpsArray[5] = getAscii(hours[0]);
            gpsArray[6] = getAscii(hours[1]);
        }
        gpsArray[7] = ASCII_COLON;

        // get number of minutes
        long nMins = gpsTime / 60;
        gpsTime %= 60;

        char mins[] = String.valueOf(nMins).toCharArray();
        if (nMins < 10) {
            gpsArray[8] = ASCII_ZERO;
            gpsArray[9] = getAscii(mins[0]);
        } else {
            gpsArray[8] = getAscii(mins[0]);
            gpsArray[9] = getAscii(mins[1]);
        }
        gpsArray[10] = ASCII_COLON;

        // get number of seconds
        long nSecs = gpsTime;

        char secs[] = String.valueOf(nSecs).toCharArray();
        if (nSecs < 10) {
            gpsArray[11] = ASCII_ZERO;
            gpsArray[12] = getAscii(secs[0]);
        } else {
            gpsArray[11] = getAscii(secs[0]);
            gpsArray[12] = getAscii(secs[1]);
        }

        // fill quality bit, an ASCII_SPACE is the best quality
        gpsArray[13] = ASCII_SPACE;

        if (log.isInfoEnabled()) {
            Charset charset = Charset.forName("US-ASCII");
            CharBuffer charBuffer = charset.decode(ByteBuffer.wrap(gpsArray));
            String gpsString = charBuffer.toString();
            byte bs[] = gpsString.getBytes();

            log.debug("Forming GPS string for time = " + utcTime + " seconds\n"
                     + "    Days    = " + nDays + "\n"
                     + "    Hours   = " + nHours + "\n"
                     + "    Minutes = " + nMins + "\n"
                     + "    Seconds = " + nSecs + "\n"
                     + "    String  = " + gpsString + "\n"
                     + "            = 0x0" + Integer.toHexString(bs[0])
                     + Integer.toHexString(bs[1]) + Integer.toHexString(bs[2]) + Integer.toHexString(bs[3])
                     + Integer.toHexString(bs[4]) + Integer.toHexString(bs[5]) + Integer.toHexString(bs[6])
                     + Integer.toHexString(bs[7]) + Integer.toHexString(bs[8]) + Integer.toHexString(bs[9])
                     + Integer.toHexString(bs[10]) + Integer.toHexString(bs[11]) + Integer.toHexString(bs[12])
                     + Integer.toHexString(bs[13]));

        }

        return gpsArray;

    }

    /**
     * Get the ASCII byte code for a decimal
     * @param ucode char representation of digit
     * @return ASCII representation of digit (in hex)
     */
    private static byte getAscii(char ucode) {
        if (ucode == '0') {
            return ASCII_ZERO;
        } else if (ucode == '1') {
            return ASCII_ONE;
        } else if (ucode == '2') {
            return ASCII_TWO;
        } else if (ucode == '3') {
            return ASCII_THREE;
        } else if (ucode == '4') {
            return ASCII_FOUR;
        } else if (ucode == '5') {
            return ASCII_FIVE;
        } else if (ucode == '6') {
            return ASCII_SIX;
        } else if (ucode == '7') {
            return ASCII_SEVEN;
        } else if (ucode == '8') {
            return ASCII_EIGHT;
        } else if (ucode == '9') {
            return ASCII_NINE;
        } else {
            return ((byte) 0xff);
        }
    }

    /**
     * Get the size of the record.
     *
     * @return size in bytes
     */
    public int getSize() {
        return recordLength;
    }

    /**
     * Generate a real record from a generic record.
     *
     * @param generic generic record
     *
     * @return ByteBuffer representation of real record
     */
    public ByteBuffer generateRecord(IGenericRecord generic) {
        if (generic instanceof GenericTcalRecord) {
            GenericTcalRecord tcal = (GenericTcalRecord) generic;
            return generateRecord(tcal.getUtcTime());
        } else {
            return null;
        }
    }
    public static String dumpRecord(ByteBuffer record) {
        short recordLength = record.getShort(RECORD_LENGTH_OFFSET);
        byte recordFormat = record.get(RECORD_FORMAT_OFFSET);
        byte recordBlank = record.get(RECORD_BLANK_OFFSET);
        long dorTx = record.getLong(DOR_TX_OFFSET);
        long dorRx = record.getLong(DOR_RX_OFFSET);
        byte[] dorWf = new byte[128];
        for (int i=0; i<128; i++) {
            dorWf[i] = record.get(DOR_WF_OFFSET + i);
        }
        long domRx = record.getLong(DOM_RX_OFFSET);
        long domTx = record.getLong(DOM_TX_OFFSET);
        byte[] domWf = new byte[128];
        for (int i=0; i<64; i++) {
            domWf[i] = record.get(DOM_WF_OFFSET + i);
        }
        byte[] gpsString = new byte[14];
        for (int i=0; i<14; i++) {
            gpsString[i] = record.get(GPS_STRING_OFFSET + i);
        }
        long dorGps = record.getLong(GPS_DOR_COUNT_OFFSET);

        StringBuffer buffer = new StringBuffer("TCal dump:\n");
        buffer.append("  RecordLength = " + recordLength + "\n");
        buffer.append("  RecordFormat = " + recordFormat + "\n");
        buffer.append("         Blank = " + recordBlank + "\n");
        buffer.append("         DorTx = " + dorTx + "\n");
        buffer.append("         DorRx = " + dorRx + "\n");
        buffer.append("         DorWF = 0x" + EngineeringFormatRecord.toHexString(dorWf) + "\n");
        buffer.append("         DomRx = " + domRx + "\n");
        buffer.append("         DomTx = " + domTx + "\n");
        buffer.append("         DomWF = 0x" + EngineeringFormatRecord.toHexString(domWf) + "\n");
        buffer.append("     GpsString = 0x" + EngineeringFormatRecord.toHexString(gpsString) + "\n");
        buffer.append("        DorGps = " + dorGps);
        return buffer.toString();
    }

}
