/*
 * class: SimpleDriver
 *
 * Version $Id: SimpleDriver.java,v 1.3 2005/08/29 20:52:15 toale Exp $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import icecube.daq.payload.MasterPayloadFactory;
import icecube.daq.payload.IPayload;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

/**
 * This class reads a text file containing GenericHits and produces either HitPayloads
 * or HitDataPayloads.
 *
 * @version $Id: SimpleDriver.java,v 1.3 2005/08/29 20:52:15 toale Exp $
 * @author pat
 */
public class SimpleDriver
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(SimpleDriver.class);

    /**
     * source of GenericHits
     */
    private ISource hitSource;

    /**
     * generator of ByteBuffer
     */
    private IGenerator hitGenerator;

    /**
     * payload factory
     */
    private MasterPayloadFactory payloadFactory;

    /**
     * default constructor
     * @param fileName name of file with simple hits
     */
    public SimpleDriver(String fileName) {
        this(fileName, "HitPayload");
    }

    /**
     * constructor
     * @param fileName name of file with simple hits
     * @param payloadType type of payload to generate (can be HitDataPayload or HitPayload)
     */
    public SimpleDriver(String fileName, String payloadType) {
        hitSource = new SimpleFileReader(fileName);
        if (payloadType.equalsIgnoreCase("HitDataPayload")) {
            hitGenerator = new HitDataGenerator();
        } else {
            hitGenerator = new HitGenerator();
        }
        payloadFactory = new MasterPayloadFactory();
    }

    public SimpleDriver(InputStream input, String payloadType) {
        hitSource = new SimpleFileReader(input);
        if (payloadType.equalsIgnoreCase("HitDataPayload")) {
            hitGenerator = new HitDataGenerator();
        } else {
            hitGenerator = new HitGenerator();
        }
        payloadFactory = new MasterPayloadFactory();
    }

    /**
     * get next hit
     * @return IPayload (or null if EOF)
     */
    public IPayload nextHit() {
        GenericHit genericHit = (GenericHit) hitSource.nextPayload();
        if (genericHit == null) {
            return null;
        }

        ByteBuffer hitBuffer = hitGenerator.generatePayload(genericHit);
        IPayload hitPayload = null;
        try {
            hitPayload = payloadFactory.createPayload(0, hitBuffer);
        } catch (IOException ioe) {
            log.fatal("ERROR creating payload", ioe);
        } catch (DataFormatException dfe) {
            log.fatal("ERROR creating payload", dfe);
        }

        return hitPayload;

    }

}
