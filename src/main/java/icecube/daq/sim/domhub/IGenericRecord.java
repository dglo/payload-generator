/*
 * interface: IGeneric
 *
 * Version $Id: IGenericRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 8 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines a generic comparable object.
 *
 * @version $Id: IGenericRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface IGenericRecord
        extends Comparable
{

    /**
     * Get the UTC time of this generic.
     *
     * @return UTC time in units of nanoseconds
     */
    long getUtcTime();

    /**
     * Set the UTC time of this generic.
     *
     * @param utcTime UTC time in units of nanoseconds
     */
    void setUtcTime(long utcTime);

}
