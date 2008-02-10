/*
 * interface: IFactory
 *
 * Version $Id: IFactory.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * This interface defines a factory for converting generic records to real records.
 *
 * @version $Id: IFactory.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public interface IFactory
{

    /**
     * Get the next buffer for this stream.
     *
     * @return ByteBuffer with next record
     */
    ByteBuffer nextBuffer();

    /**
     * Get a list of buffers with times from the lastTime to lastTime+period
     * @param period length of time in ns
     * @return List of ByteBuffers of records
     */
    List nextBuffers(long period);

}
