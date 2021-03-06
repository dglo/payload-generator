/*
 * class: HitFactory
 *
 * Version $Id: HitFactory.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: May 25 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class turns generic hits into real hits, packaged as ByteBuffers.
 *
 * @version $Id: HitFactory.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class HitFactory
        implements IFactory
{

    /**
     * Logging object.
     */
    private static final Log log = LogFactory.getLog(HitFactory.class);

    /**
     * Generator for the stream.
     */
    private final IStreamGenerator generator;

    /**
     * The maker of ByteBuffers.
     */
    private final IRecord record;

    /**
     * Constructor.
     *
     * @param domId domId for this stream
     * @param generator generator of generic records
     * @param record maker of real records
     */
    public HitFactory(long domId, IStreamGenerator generator, IRecord record) {

        if (log.isInfoEnabled()) {
            String dump = "Constructing HitFactory with:\n"
                          + "  domId = " + domId;
            log.info(dump);
        }

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
            //log.info("Target time = " + target + "  Hit time = " + timeInMillis);
            records.add(record.generateRecord(generic));
            if (timeInMillis >= target) {
                earlier = false;
            }
        }

        return records;
    }

}
