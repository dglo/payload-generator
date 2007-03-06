/*
 * class: MoniRecord
 *
 * Version $Id: MoniRecord.java,v 1.4 2006/08/15 14:40:16 toale Exp $
 *
 * Date: June 12 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;

/**
 * This class produces valid MONI records. For now it only produces DOM_HARDWARE_STATE records.
 *
 * @version $Id: MoniRecord.java,v 1.4 2006/08/15 14:40:16 toale Exp $
 * @author pat
 */
public class MoniRecord implements IRecord
{

    /**
     * Log object for this class.
     */
    private static final Log log = LogFactory.getLog(MoniRecord.class);

    public static final short DOM_ASCII_LOG_MESSAGE          = 0x00CB;
    public static final short DOM_HARDWARE_STATE             = 0x00C8;
    public static final short DOM_CONFIGURATION              = 0x00C9;
    public static final short DOM_CONFIGURATION_STATE_CHANGE = 0x00CA;
    public static final short GENERIC_RECORD                 = 0x00CC;
    public static final short SUPERNOVA_RECORD               = 0x012C;

    private static final int EVENT_LENGTH_SIZE = 2;
    private static final int EVENT_TYPE_SIZE   = 2;
    private static final int TIMESTAMP_SIZE    = 6;
    private static final int HEADER_LENGTH = EVENT_LENGTH_SIZE + EVENT_TYPE_SIZE + TIMESTAMP_SIZE;

    private static final int DOM_HARDWARE_STATE_LENGTH = 72;
    // todo: this is rate dependent!!!
    private static final int SUPERNOVA_LENGTH = 625;

    private static final int EVENT_LENGTH_OFFSET = 0;
    private static final int EVENT_TYPE_OFFSET = EVENT_LENGTH_OFFSET + EVENT_LENGTH_SIZE;
    private static final int TIMESTAMP_OFFSET = EVENT_TYPE_OFFSET + EVENT_TYPE_SIZE;
    private static final int RECORD_OFFSET = TIMESTAMP_OFFSET + TIMESTAMP_SIZE;

    private short format;

    /**
     * Service for producing tcal timing data
     */
    private TimeConverter timeConverter;

    /**
     * Constructor that takes a configured TimeConverter object.
     *
     * @param timeConverter time converter service to use
     */
    public MoniRecord(TimeConverter timeConverter) {
        this(timeConverter, DOM_HARDWARE_STATE);
    }

    public MoniRecord(TimeConverter timeConverter, short format) {
        if (log.isInfoEnabled()) {
            String dump = "Constructing MoniRecord with format " + format;
            log.info(dump);
        }
        this.timeConverter = timeConverter;
        this.format = format;
    }

    /**
     * Get the size of the record.
     *
     * @return size in bytes
     */
    public int getSize() {
        if (format == 200) {
            return HEADER_LENGTH + DOM_HARDWARE_STATE_LENGTH;
        } else if (format == 300) {
            return HEADER_LENGTH + SUPERNOVA_LENGTH;
        } else {
            return -1;
        }
    }

    /**
     * Generate a real record from a generic record.
     *
     * @param generic generic record
     *
     * @return ByteBuffer representation of real record
     */
    public ByteBuffer generateRecord(IGenericRecord generic) {
        short format = ((IGenericMoniRecord) generic).getFormatId();
        if (format == IGenericMoniRecord.HARDWARE_STATE_EVENT) {
            return generateHardwareStateRecord(generic);
        } else if (format == IGenericMoniRecord.SUPERNOVA_RECORD) {
            return generateSupernovaRecord(generic);
        } else {
            return null;
        }
    }

    public ByteBuffer generateHardwareStateRecord(IGenericRecord generic) {
        short size = HEADER_LENGTH + DOM_HARDWARE_STATE_LENGTH;
        ByteBuffer record = ByteBuffer.allocate(size);

        // put in header
        record.putShort(EVENT_LENGTH_OFFSET, size);
        record.putShort(EVENT_TYPE_OFFSET, DOM_HARDWARE_STATE);

        // get 6B timestamp
        record.position(TIMESTAMP_OFFSET);
        long utcTime = generic.getUtcTime();
        long domTime = timeConverter.getDomClockFromUtcTime(utcTime);
        if (log.isDebugEnabled()) {
            log.debug("MONI Record: UTCtime= " + utcTime + "  DOMtime= " + domTime);
        }
        byte time[] = EngineeringFormatRecord.timeStampToBytes(domTime);
        record.put(time);

        // fill in empty record
        record.position(RECORD_OFFSET);
        record.put(new byte[DOM_HARDWARE_STATE_LENGTH]);

        record.flip();
        return record;
    }

    public ByteBuffer generateSupernovaRecord(IGenericRecord generic) {
        short size = HEADER_LENGTH + SUPERNOVA_LENGTH;
        ByteBuffer record = ByteBuffer.allocate(size);

        // put in header
        record.putShort(EVENT_LENGTH_OFFSET, size);
        record.putShort(EVENT_TYPE_OFFSET, SUPERNOVA_RECORD);

        // get 6B timestamp
        record.position(TIMESTAMP_OFFSET);
        long utcTime = generic.getUtcTime();
        long domTime = timeConverter.getDomClockFromUtcTime(utcTime);
        if (log.isDebugEnabled()) {
            log.debug("MONI Record: UTCtime= " + utcTime + "  DOMtime= " + domTime);
        }
        byte time[] = EngineeringFormatRecord.timeStampToBytes(domTime);
        record.put(time);

        // fill in empty record
        record.position(RECORD_OFFSET);
        record.put(new byte[SUPERNOVA_LENGTH]);

        record.flip();
        return record;
    }

    public static boolean isSuperNova(ByteBuffer buffer) {
        short format = buffer.getShort(EVENT_TYPE_OFFSET);
        if (format == SUPERNOVA_RECORD) {
            return true;
        } else {
            return false;
        }
    }
}
