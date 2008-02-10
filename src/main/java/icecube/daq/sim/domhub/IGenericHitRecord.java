/*
 * interface: IGenericHitRecord
 *
 * Version $Id: IGenericHitRecord.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines the methods of a generic hit record.
 *
 * @version $Id: IGenericHitRecord.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public interface IGenericHitRecord
        extends IGenericRecord
{

    /**
     * Beacon hit type.
     */
    int BEACON_HIT = 1;

    /**
     * SPE hit type.
     */
    int SPE_HIT = 2;

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
