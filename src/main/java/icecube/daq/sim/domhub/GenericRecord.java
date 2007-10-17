/*
 * class: GenericRecord
 *
 * Version $Id: GenericRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class implements the behavior of a generic record, i.e. it is comparable.
 *
 * @version $Id: GenericRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericRecord
        implements IGenericRecord
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(GenericRecord.class);

    /**
     * UTC time of the generic record, in units of 1/10 nanoseconds
     */
    protected long utcTime;

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
