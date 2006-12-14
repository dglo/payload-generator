/*
 * interface: IGenericHitRecord
 *
 * Version $Id: IGenericHitRecord.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines the methods of a generic hit record.
 *
 * @version $Id: IGenericHitRecord.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 * @author pat
 */
public interface IGenericHitRecord
        extends IGenericRecord
{

    /**
     * Beacon hit type.
     */
    static final int BEACON_HIT = 1;

    /**
     * SPE hit type.
     */
    static final int SPE_HIT = 2;

    /**
     * Get the type of hit.
     *
     * @return type of hit
     */
    int getHitType();

    /**
     * Set the type of hit.
     *
     * @param hitType type of hit
     */
    void setHitType(int hitType);
    
}
