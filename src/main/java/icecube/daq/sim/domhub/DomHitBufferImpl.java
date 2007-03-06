package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: pat
 * Date: Oct 24, 2006
 * Time: 11:43:48 AM
 *
 * Implementation of DomHitBuffer interface.
 */
public class DomHitBufferImpl implements DomHitBuffer
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(DomHitBufferImpl.class);

    /**
     * Buffer size in bytes.
     */
    private int bufferSize;

    /**
     * Element size in bytes.
     */
    private int elementSize;

    /**
     * The implementation of the buffer.
     */
    private List buffer;

    /**
     * Number of elements that can be held without rollover.
     */
    private int numElements;

    /**
     * Default constructor, uses default buffer configuration.
     */
    DomHitBufferImpl() {
        this(DEFAULT_BUFFER_SIZE, DEFAULT_ELEMENT_SIZE);
    }

    /**
     * Constructor
     *
     * @param bufferSize Size of buffer in bytes
     * @param elementSize Size of element in bytes
     */
    DomHitBufferImpl(int bufferSize, int elementSize) {
        if (bufferSize <= 0) bufferSize = DEFAULT_BUFFER_SIZE;
        if (elementSize <= 0) elementSize = DEFAULT_ELEMENT_SIZE;
        this.bufferSize = bufferSize;
        this.elementSize = elementSize;

        numElements = (int) (bufferSize/elementSize);
        if (log.isDebugEnabled()) {
            log.debug("BufferSize = " + bufferSize + " B, ElementSize = " + elementSize
                      + " B, NumElements = " + numElements);
        }

        buffer = new ArrayList();
    }

    /**
     * Set the size of the buffer.
     *
     * @param size Size of buffer in bytes
     */
    public void setBufferSize(int size) {
        if (size <= 0) size = DEFAULT_BUFFER_SIZE;
        bufferSize = size;
        numElements = (int) (bufferSize/elementSize);
    }

    /**
     * Set the size of a single element in the buffer.
     *
     * @param size Size of a buffer element in bytes
     */
    public void setElementSize(int size) {
        if (size <= 0) size = DEFAULT_ELEMENT_SIZE;
        elementSize = size;
        numElements = (int) (bufferSize/elementSize);
    }

    /**
     * Add a hit to the buffer.
     *
     * @param hit ByteBuffer containing a DOM hit
     */
    public synchronized void addHit(ByteBuffer hit) {
        if (buffer.size() >= numElements) {
            buffer.clear();
        }
        buffer.add(hit);
    }

    /**
     * Get the hits out of the buffer.
     *
     * @return ByteBuffer containing all hits in the buffer.
     */
    public synchronized ByteBuffer getHits() {
        ByteBuffer hits = ByteBuffer.allocate(bufferSize);
        Iterator iter = buffer.iterator();
        while (iter.hasNext()) {
            hits.put((ByteBuffer) iter.next());
        }
        buffer.clear();
        hits.flip();
        return hits;
    }

}
