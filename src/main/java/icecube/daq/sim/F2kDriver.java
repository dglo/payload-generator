/*
 * class: F2kDriver
 *
 * Version $Id: F2kDriver.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: June 6 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.icebucket.logging.LoggingConsumer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class reads an f2k file and produces either HitPayloads
 * or HitDataPayloads.
 *
 * @version $Id: F2kDriver.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class F2kDriver
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(F2kDriver.class);

    private static final double noiseRatePerOM = 800e-10; // in units of 1/10 ns

    /**
     * source of GenericHits
     */
    private ISource hitSource;

    /**
     * generator of ByteBuffer
     */
    private IGenerator timeStampGenerator;
    private IGenerator waveFormGenerator;

    private int lcMode;

    /**
     * constructor
     * @param fileName name of f2k file
     * @param lcMode local coincidence mode
     * @param nEventsInFile number of events in f2k file (number of EM lines)
     * @param lifeTime lifetime reported in f2k file (HI ! ! LIFETIME line)
     */
    public F2kDriver(String fileName, int lcMode, int nEventsInFile, float lifeTime) {
        this(new F2kFileReader(fileName, lcMode, nEventsInFile, lifeTime, noiseRatePerOM), lcMode);
    }

    /**
     * constructor
     * @param stream input stream
     * @param lcMode local coincidence mode
     * @param nEventsInFile number of events in f2k file (number of EM lines)
     * @param lifeTime lifetime reported in f2k file (HI ! ! LIFETIME line)
     */
    public F2kDriver(InputStream stream, int lcMode, int nEventsInFile, float lifeTime) {
        this(new F2kFileReader(stream, lcMode, nEventsInFile, lifeTime, noiseRatePerOM), lcMode);
    }

    /**
     * constructor
     * @param hitSource hit source
     * @param lcMode local coincidence mode
     */
    private F2kDriver(ISource hitSource, int lcMode) {
        this.hitSource = hitSource;
        this.lcMode = lcMode;

        if ((lcMode == 0) || (lcMode == 1)) {
            timeStampGenerator = null;
            //waveFormGenerator = new EngFmtGenerator();
            waveFormGenerator = new HitDataGenerator();
        } else if (lcMode == 2) {
            //timeStampGenerator = new EngFmtGenerator(0,0,0);
            //waveFormGenerator = new EngFmtGenerator();
            timeStampGenerator = new HitDataGenerator(0,0,0);
            waveFormGenerator = new HitDataGenerator();
        } else {
            //timeStampGenerator = new EngFmtGenerator(56,0,0);
            //waveFormGenerator = new EngFmtGenerator();
            timeStampGenerator = new HitDataGenerator(56,0,0);
            waveFormGenerator = new HitDataGenerator();
        }
    }

    /**
     * get next hit
     * @return IPayload (or null if EOF)
     */
    public ByteBuffer nextHit() {

        // loop until we get a hit
        boolean needOne = true;
        ByteBuffer hitBuffer = null;
        while (needOne) {
            GenericF2kHit hit = (GenericF2kHit) hitSource.nextPayload();
            if (hit == null) {
                return null;
            }

            int lcTag = hit.getLcTag();
            if (lcMode == 0) {
                // if there is no lc, every hit has a full readout
                hitBuffer = waveFormGenerator.generatePayload(hit);
                needOne = false;
                //log.debug("LCMode = 0: Got hit with LCTag = " + lcTag);
            } else if (lcMode == 1) {
                // if there is hard lc, hits with lc have a full readout
                //                      hits without lc have NO readout
                if (lcTag != 0) {
                    hitBuffer = waveFormGenerator.generatePayload(hit);
                    needOne = false;
                    //log.debug("LCMode = 1: Got hit with LCTag = "+ lcTag);
                } else {
                    //log.debug("LCMode = 1: Discarding hit with LCTag = " + lcTag);
                }
            } else {
                // if there is any other lc, hits with lc have full readout
                //                           hits without lc have partial readout
                if (lcTag == 0) {
                    hitBuffer = timeStampGenerator.generatePayload(hit);
                } else {
                    hitBuffer = waveFormGenerator.generatePayload(hit);
                }
                needOne = false;
                //log.debug("LCMode = 2/3: Got hit with LCTag = "+ lcTag);
            }
        }

        if (null == hitBuffer) {
            log.error("Hit buffer should not be null!");
        }

        return hitBuffer;
    }

    public void createHitFile(String fileName) {

        // setup output channel
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileName);
        } catch (IOException e) {
            log.fatal("ERROR while opening output file: " + fileName, e);
        }
        FileChannel outputChannel = output.getChannel();

        ByteBuffer buffer;
        while (null != (buffer = nextHit())) {
            try {
                outputChannel.write(buffer);
            } catch (IOException e) {
                log.fatal("ERROR while writing output", e);
            }
        }

        // close output channel
        try {
            outputChannel.close();
        } catch (IOException e) {
            log.fatal("ERROR while closing output file", e);
        }

    }

    private static void usage() {
        System.err.println("F2kDriver usage:");
        System.err.println("  F2kDriver <f2kFileName> <lcMode> <nEventsInFile> <lifeTime> <hitFileName>");
        System.err.println("     <f2kFileName>     name of f2k file");
        System.err.println("     <lcMode>          LC mode (0=none, 1=hard, 2=soft, 3=flabby)");
        System.err.println("     <nEventsInFile>   number of events in f2k file");
        System.err.println("     <lifeTime>        lifetime of events in seconds");
        System.err.println("     <hitFileName>     name of hit file");
        System.exit(-1);
    }

    public static void main(String[] args) {

        LoggingConsumer.installDefault();

        if (args.length != 5) {
            usage();
        }

        String f2kFileName = args[0];
        int lcMode = Integer.parseInt(args[1]);
        int nEventsInFile = Integer.parseInt(args[2]);
        float lifeTime = Float.parseFloat(args[3]);
        String hitFileName = args[4];

        F2kDriver driver = new F2kDriver(f2kFileName, lcMode, nEventsInFile, lifeTime);
        driver.createHitFile(hitFileName);

    }


}
