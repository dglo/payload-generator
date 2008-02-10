/*
 * class: GenericTcalRecord
 *
 * Version $Id: GenericMoniRecord.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This class holds generic moni information.
 *
 * @version $Id: GenericMoniRecord.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class GenericMoniRecord
        extends GenericRecord
        implements IGenericMoniRecord
{

    /**
     * Monitor record format id.
     */
    private short formatId;

    /**
     * Default constructor, sets utcTime to -1.
     */
    public GenericMoniRecord() {
        this(-1, HARDWARE_STATE_EVENT);
    }

    /**
     * Constructor that takes a utc time.
     * @param utcTime UTC time of record (in 1/10 ns)
     */
    public GenericMoniRecord(long utcTime, short formatId) {
        super(utcTime);
        this.formatId = formatId;
    }

    /**
     * Get the format id.
     *
     * @return format id of this record
     */
    public short getFormatId() {
        return formatId;
    }

    /**
     * Set the format id.
     *
     * @param formatId format id of this record
     */
    public void setFormatId(short formatId) {
        this.formatId = formatId;
    }
}
