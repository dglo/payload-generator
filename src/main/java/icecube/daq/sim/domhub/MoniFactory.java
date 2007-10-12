/*
 * class: HitFactory
 *
 * Version $Id: MoniFactory.java 2125 2007-10-12 18:27:05Z ksb $
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
 * This class turns generic moni records into real moni records, packaged as ByteBuffers.
 *
 * @version $Id: MoniFactory.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class MoniFactory
        implements IFactory
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(MoniFactory.class);

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
    public MoniFactory(long domId, IStreamGenerator generator, IRecord record) {

        if (log.isInfoEnabled()) {
            String dump = "Constructing MoniFactory with:\n"
                          + "  domId = " + domId;
            log.info(dump);
        }

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
