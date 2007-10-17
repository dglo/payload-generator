/*
 * interface: IDataSource
 *
 * Version $Id: IDataSource.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: August 15 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;

/**
 * This interface defines a source of a simulated data stream
 *
 * @version $Id: IDataSource.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface IDataSource
{

    /**
     * Start the source producing data.
     */
    void start();

    /**
     * Stop the source.
     */
    void stop();

    /**
     * Get the next data buffer.
     * @return a ByteBuffer containing the next record
     */
    ByteBuffer next();

}
