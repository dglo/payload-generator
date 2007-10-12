/*
 * class: GenericTcalRecord
 *
 * Version $Id: GenericTcalRecord.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds generic tcal information.
 *
 * @version $Id: GenericTcalRecord.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericTcalRecord
        extends GenericRecord
        implements IGenericTcalRecord
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(GenericTcalRecord.class);

    /**
     * Default constructor, sets utcTime to -1.
     */
    public GenericTcalRecord() {
        this(-1);
    }

    /**
     * Constructor that takes a utc time.
     * @param utcTime UTC time of record (in 1/10 ns)
     */
    public GenericTcalRecord(long utcTime) {
        this.utcTime = utcTime;
    }

}
