/*
 * interface: IGenericTcalRecord
 *
 * Version $Id: IGenericMoniRecord.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines the methods of a generic moni record.
 *
 * @version $Id: IGenericMoniRecord.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public interface IGenericMoniRecord
        extends IGenericRecord
{

    /**
     * Hardware state event format id
     */
    short HARDWARE_STATE_EVENT = 200;

    /**
     * Supernova record format id
     */
    short SUPERNOVA_RECORD = 300;

    /**
     * Get the format id.
     *
     * @return format id of this record
     */
    short getFormatId();

    /**
     * Set the format id.
     *
     * @param formatId format id of this record
     */
    void setFormatId(short formatId);

}
