/*
 * class: GenericRecord
 *
 * Version $Id: GenericRecord.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This class implements the behavior of a generic record, i.e. it is comparable.
 *
 * @version $Id: GenericRecord.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class GenericRecord
        implements IGenericRecord
{

    /**
     * UTC time of the generic record, in units of 1/10 nanoseconds
     */
    private long utcTime;

    /**
     * Default constructor, sets utcTime to -1
     */
    protected GenericRecord() {
        this(-1);
    }

    /**
     * Constructor that takes a utcTime
     *
     * @param utcTime UTC time for this generic
     */
    protected GenericRecord(long utcTime) {
        this.utcTime = utcTime;
    }

    /**
     * Get the UTC time of this generic.
     *
     * @return UTC time in units of 1/10 nanoseconds
     */
    public long getUtcTime() {
        return utcTime;
    }

    /**
     * Set the UTC time of this generic.
     *
     * @param utcTime UTC time in units of 1/10 nanoseconds
     */
    public void setUtcTime(long utcTime) {
        this.utcTime = utcTime;
    }

    /**
     * Defines the ordering of generic records.
     *
     * @param object generic record to compare to
     * @return -1 if this < that, +1 if this > that, and 0 if this==that
     */
    public int compareTo(Object object) {
        if (this.getUtcTime() < ((GenericRecord) object).getUtcTime()) {
            return -1;
        } else if (this.getUtcTime() > ((GenericRecord) object).getUtcTime()) {
            return +1;
        } else {
            return 0;
        }
    }

}
