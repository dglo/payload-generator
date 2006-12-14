/*
 * class: Driver
 *
 * Version $Id: Driver.java,v 1.3 2005/07/20 18:36:10 toale Exp $
 *
 * Date: June 2 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.FileOutputStream;

import icecube.icebucket.logging.LoggingConsumer;

/**
 * This class ...does what?
 *
 * @version $Id: Driver.java,v 1.3 2005/07/20 18:36:10 toale Exp $
 * @author pat
 */
public class Driver
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(Driver.class);


    // input types
    private static final String INPUT_TYPE_OPT   = "-input";
    private static final String SIMPLE_MODE      = "simple";
    private static final String F2K_MODE         = "f2k";
    private static final String RANDOM_MODE      = "random";

    // output types
    private static final String OUTPUT_TYPE_OPT  = "-output";
    private static final String FILE_MODE        = "file";
    private static final String CHANNEL_MODE     = "channel";

    // payload types
    private static final String PAYLOAD_OPT      = "-payload";
    private static final String HIT_DATA_PAYLOAD = "HitDataPayload";
    private static final String HIT_PAYLOAD      = "HitPayload";

    // number of payloads
    private static final String MAX_HIT_OPT      = "-number";
    private static final int    MAX_HIT_DEFAULT  = 1000;

    private String inputMode;
    private String outputMode;
    private int maxHit = -1;
    private String payloadType = HIT_DATA_PAYLOAD;

    private IGenerator generator;
    private ISource hitSource;

    private String inputFileName;
    private String outputFileName;


    public Driver() {

    }

    /**
     * generate hits
     */
    private void generate() {

        // setup output channel
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(outputFileName);
        } catch (IOException e) {
            log.fatal("ERROR while opening output file: " + outputFileName, e);
        }
        FileChannel outputChannel = output.getChannel();

        // setup source
        if (inputMode.equalsIgnoreCase(SIMPLE_MODE)) {
            log.info("  Reading data from simple file: " + inputFileName);
            hitSource = new SimpleFileReader(inputFileName);
        } else if (inputMode.equalsIgnoreCase(F2K_MODE)) {
            log.info("  Reading data from f2k file: " + inputFileName);
            hitSource = new F2kFileReader(inputFileName, 1, 1, 0.0f, 0.0);
        } else if (inputMode.equalsIgnoreCase(RANDOM_MODE)) {
            log.info("  Generating random data");
            hitSource = new RandomSource();
        }

        // generate
        boolean ok = true;
        int nHit = 0;
        while (ok) {

            GenericHit hit = (GenericHit) hitSource.nextPayload();
            if (null != hit) {
                try {
                    outputChannel.write(generator.generatePayload(hit));
                } catch (IOException e) {
                    log.fatal("ERROR while writing output", e);
                }
                nHit++;
                if ((maxHit > 0) && (nHit >= maxHit)) {
                    ok = false;
                }
            } else {
                ok = false;
            }

        }

        // close output channel
        try {
            outputChannel.close();
        } catch (IOException e) {
            log.fatal("ERROR while closing output file", e);
        }
    }

    private boolean parseArguments(String args[]) {

        if (args.length == 0) {
            usage();
        }

        boolean inputOk  = false;
        boolean outputOk = false;

        for (int i=0; i<args.length; i++) {
            String arg = args[i];

            /*
             * Setup source
             */
            if (arg.equalsIgnoreCase(INPUT_TYPE_OPT)) {
                if (args.length > i+1) {
                    i++;
                    String inputType = args[i];

                    // get input mode
                    boolean needsFile = false;
                    if (inputType.equalsIgnoreCase(SIMPLE_MODE)) {
                        inputMode = SIMPLE_MODE;
                        needsFile = true;
                    } else if (inputType.equalsIgnoreCase(F2K_MODE)) {
                        inputMode = F2K_MODE;
                        needsFile = true;
                    } else if (inputType.equalsIgnoreCase(RANDOM_MODE)) {
                        inputMode = RANDOM_MODE;
                    } else {
                        log.error("Unknown input mode");
                        usage();
                    }

                    // if necessary, get input file
                    if (needsFile) {
                        if (args.length > i+1) {
                            i++;
                            inputFileName = args[i];
                        } else {
                            log.error("Missing " + INPUT_TYPE_OPT + " option");
                            usage();
                        }
                    }

                    inputOk = true;
                } else {
                    log.error("Missing " + INPUT_TYPE_OPT + " option");
                    usage();
                }

            /*
             * Setup sink
             */
            } else if (arg.equalsIgnoreCase(OUTPUT_TYPE_OPT)) {
                if (args.length > i+1) {
                    i++;
                    String outputType = args[i];

                    // get output mode
                    if (outputType.equalsIgnoreCase(FILE_MODE)) {
                        outputMode = FILE_MODE;
                    }

                    // get file name
                    if (args.length > i+1) {
                        i++;
                        outputFileName = args[i];
                    } else {
                        log.error("Missing " + OUTPUT_TYPE_OPT + " option");
                        usage();
                    }

                    outputOk = true;
                } else {
                    log.error("Missing " + OUTPUT_TYPE_OPT + " option");
                    usage();
                }

            /*
             * Setup other options
             */
            } else if (arg.equalsIgnoreCase(MAX_HIT_OPT)) {
                if (args.length > i+1) {
                    i++;
                    maxHit = Integer.parseInt(args[i]);
                } else {
                    log.error("Missing " + MAX_HIT_OPT + " option");
                    usage();
                }
            } else if (arg.equalsIgnoreCase(PAYLOAD_OPT)) {
                if (args.length > i+1) {
                    i++;
                    String payload = args[i];
                    if (payload.equalsIgnoreCase(HIT_DATA_PAYLOAD)) {
                        payloadType = HIT_DATA_PAYLOAD;
                    } else if (payload.equalsIgnoreCase(HIT_PAYLOAD)) {
                        payloadType = HIT_PAYLOAD;
                    } else {
                        log.error("Unknown payload type");
                        usage();
                    }
                } else {
                    log.error("Missing " + PAYLOAD_OPT + " option");
                }

            /*
             * what's this?
             */
            } else {
                log.error("Unknown argument");
                usage();
            }
        }

        if (inputMode.equalsIgnoreCase(RANDOM_MODE)) {
            if (-1 == maxHit) {
                maxHit = MAX_HIT_DEFAULT;
            }
        }

        if ((!inputOk) || (!outputOk)) {
            usage();
        }

        if (payloadType.equalsIgnoreCase(HIT_DATA_PAYLOAD)) {
            generator = new HitDataGenerator();
        } else {
            generator = new HitGenerator();
        }

        return true;
    }

    private void usage() {
        log.error("Driver for HitGenerator:");
        log.error("   " + INPUT_TYPE_OPT + " <'" + SIMPLE_MODE + "' <file name> | '" + F2K_MODE + "' <file name> | '"
                                                + RANDOM_MODE + "'>");
        log.error("   " + OUTPUT_TYPE_OPT + " <'" + FILE_MODE + "' <file name>>");
        log.error("  [" + PAYLOAD_OPT + " <'" + HIT_PAYLOAD + "' | '" + HIT_DATA_PAYLOAD + "'>]");
        log.error("  [" + MAX_HIT_OPT + " <max number of hits>]");
        System.exit(-1);
    }

    public static void main(String args[]) {

        LoggingConsumer.installDefault();

        Driver driver = new Driver();
        if (driver.parseArguments(args)) {
            driver.generate();
        }

    }

}
