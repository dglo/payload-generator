/*
 * interface: IDataSource
 *
 * Version $Id: IDataSource.java,v 1.1 2005/08/17 04:53:40 toale Exp $
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
 * @version $Id: IDataSource.java,v 1.1 2005/08/17 04:53:40 toale Exp $
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
