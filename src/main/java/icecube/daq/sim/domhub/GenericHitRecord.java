/*
 * class: GenericHit
 *
 * Version $Id: GenericHitRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds generic hit information.
 *
 * @version $Id: GenericHitRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericHitRecord
        extends GenericRecord
        implements IGenericHitRecord
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(GenericHitRecord.class);

    /**
     * Type of hit.
     */
    private int hitType;

    /**
     * Default constructor, sets utcTime to -1 and hitType to SPE_HIT.
     */
    public GenericHitRecord() {
        this(-1, SPE_HIT);
    }

    /**
     * Constructor that takes both a utcTime and a hitType.
     *
     * @param utcTime UTC time of this record (int 1/10 ns)
     * @param hitType hit type
     */
    public GenericHitRecord(long utcTime, int hitType) {
        this.utcTime = utcTime;
        this.hitType = hitType;
    }

    /**
     * Get the type of hit.
     *
     * @return type of hit
     */
    public int getHitType() {
        return hitType;
    }

    /**
     * Set the type of hit.
     *
     * @param hitType type of hit
     */
    public void setHitType(int hitType) {
        this.hitType = hitType;
    }

}
