/*
 * class: GenericTcalRecord
 *
 * Version $Id: GenericTcalRecord.java,v 1.3 2006/06/15 15:44:09 toale Exp $
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
 * @version $Id: GenericTcalRecord.java,v 1.3 2006/06/15 15:44:09 toale Exp $
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
