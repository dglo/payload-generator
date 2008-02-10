package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: pat
 * Date: Oct 24, 2006
 * Time: 11:29:06 AM
 *
 * This interface defines a emulation of the DOM ring buffer.
 * It is configured with a maximum size (in bytes) and an element size
 * (also in bytes). The default values are 1 MB for the buffer and 1 kB
 * for an element. The number of elements that can be stored is given
 * by:
 *
 *     bufferSize/elementSize   (default = 1024)
 *
 * A single hit can be added to the buffer (as a ByteBuffer) and the
 * whole buffer can be readout (again as a single ByteBuffer).
 */
public interface DomHitBuffer
{

    /**
     * Default buffer size is 1 MB.
     */
    int DEFAULT_BUFFER_SIZE = 1*1024*1024;

    /**
     * Default element size is 1 kB.
     */
    int DEFAULT_ELEMENT_SIZE = 1*1024;

    /**
     * Set the size of the buffer.
     *
     * @param size Size of buffer in bytes
     */
    void setBufferSize(int size);

    /**
     * Set the size of a single element in the buffer.
     *
     * @param size Size of a buffer element in bytes
     */
    void setElementSize(int size);

    /**
     * Add a hit to the buffer.
     *
     * @param hit ByteBuffer containing a DOM hit
     */
    void addHit(ByteBuffer hit);

    /**
     * Get the hits out of the buffer.
     *
     * @return ByteBuffer containing all hits in the buffer.
     */
    ByteBuffer getHits();

}
