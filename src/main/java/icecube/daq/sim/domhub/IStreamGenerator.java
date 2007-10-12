/*
 * interface: IStreamGenerator
 *
 * Version $Id: IStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

/**
 * This interface defines a stream generator.
 *
 * @version $Id: IStreamGenerator.java 2125 2007-10-12 18:27:05Z ksb $
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
