/*
 * class: HitFactory
 *
 * Version $Id: TcalFactory.java,v 1.4 2006/09/27 16:08:47 toale Exp $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;

/**
 * This class turns generic tcals into real tcals, packaged as ByteBuffers.
 *
 * @version $Id: TcalFactory.java,v 1.4 2006/09/27 16:08:47 toale Exp $
 * @author pat
 */
public class TcalFactory
        implements IFactory
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(TcalFactory.class);

    /**
     * The domid for this stream.
     */
    private final long domId;

    /**
     * Generator for the stream.
     */
    private final IStreamGenerator generator;

    /**
     * The maker of ByteBuffers.
     */
    private final IRecord record;

    /**
     * Time of last record produced.
     */
    private long lastTime = -1;

    /**
     * Constructor.
     *
     * @param domId domId for this stream
     * @param generator generator of generic records
     * @param record maker of real records
     */
    public TcalFactory(long domId, IStreamGenerator generator, IRecord record) {
        this.domId = domId;
        this.generator = generator;
        this.record = record;
    }

    /**
     * Get the next buffer for this stream.
     *
     * @return ByteBuffer with next record
     */
    public ByteBuffer nextBuffer() {
        return record.generateRecord(generator.nextRecord());
    }

    /**
     * Get a list of buffers with times from the lastTime to lastTime+period
     *
     * @param target time in ms to generate up to
     *
     * @return List of ByteBuffers of records
     */
    public List nextBuffers(long target) {
        List records = new ArrayList();

        // Get all records with times earlier than target
        boolean earlier = true;
        while (earlier) {
            IGenericRecord generic = generator.nextRecord();
            long time = generic.getUtcTime();
            long timeInMillis = (long) (time/10000000.0);
            records.add(record.generateRecord(generic));
            if (timeInMillis >= target) {
                earlier = false;
            }
            lastTime = time;
        }

        return records;
    }

}
