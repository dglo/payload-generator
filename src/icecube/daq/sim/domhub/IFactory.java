/*
 * interface: IFactory
 *
 * Version $Id: IFactory.java,v 1.2 2006/09/07 16:27:11 toale Exp $
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
 * @version $Id: IFactory.java,v 1.2 2006/09/07 16:27:11 toale Exp $
 * @author pat
 */
public interface IFactory
{

    /**
     * Get the next buffer for this stream.
     *
     * @return ByteBuffer with next record
     */
    public ByteBuffer nextBuffer();

    /**
     * Get a list of buffers with times from the lastTime to lastTime+period
     * @param period length of time in ns
     * @return List of ByteBuffers of records
     */
    public List nextBuffers(long period);

}
