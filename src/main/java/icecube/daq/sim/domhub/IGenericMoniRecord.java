/*
 * interface: IGenericTcalRecord
 *
 * Version $Id: IGenericMoniRecord.java,v 1.2 2006/08/07 15:38:09 toale Exp $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines the methods of a generic moni record.
 *
 * @version $Id: IGenericMoniRecord.java,v 1.2 2006/08/07 15:38:09 toale Exp $
 * @author pat
 */
public interface IGenericMoniRecord
        extends IGenericRecord
{

    /**
     * Hardware state event format id
     */
    static final short HARDWARE_STATE_EVENT = 200;

    /**
     * Supernova record format id
     */
    static final short SUPERNOVA_RECORD = 300;

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
