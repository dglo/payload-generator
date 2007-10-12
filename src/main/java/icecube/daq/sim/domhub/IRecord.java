/*
 * interface: IRecord
 *
 * Version $Id: IRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;

/**
 * This interface defines a real DAQ record.
 *
 * @version $Id: IRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface IRecord
{

    /**
     * Get the size of the record.
     *
     * @return size in bytes
     */
    int getSize();

    /**
     * Generate a real record from a generic record.
     *
     * @param generic generic record
     * @return ByteBuffer representation of real record
     */
    ByteBuffer generateRecord(IGenericRecord generic);

}
