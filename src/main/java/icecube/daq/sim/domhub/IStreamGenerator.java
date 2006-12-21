/*
 * interface: IStreamGenerator
 *
 * Version $Id: IStreamGenerator.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines a stream generator.
 *
 * @version $Id: IStreamGenerator.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 * @author pat
 */
public interface IStreamGenerator
{

    /**
     * Get the next record in this stream.
     *
     * @return next IGenericRecord
     */
    IGenericRecord nextRecord();

}
