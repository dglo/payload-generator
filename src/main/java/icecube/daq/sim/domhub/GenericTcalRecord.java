/*
 * class: GenericTcalRecord
 *
 * Version $Id: GenericTcalRecord.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This class holds generic tcal information.
 *
 * @version $Id: GenericTcalRecord.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class GenericTcalRecord
        extends GenericRecord
        implements IGenericTcalRecord
{

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
        super(utcTime);
    }

}
