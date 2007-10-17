/*
 * Version $Id: EventInputDriver.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.common.DAQCmdInterface;

import icecube.daq.payload.ISourceID;
import icecube.daq.payload.SourceIdRegistry;

import icecube.icebucket.logging.LoggingConsumer;

import java.io.IOException;
import java.io.FileOutputStream;

import java.nio.channels.FileChannel;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * Write triggers and corresponding hits to files,
 * to be used as input to the event builder.
 */
public class EventInputDriver
{
    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(EventInputDriver.class);

    // payload types
    private static final String HIT_DATA_PAYLOAD = "HitDataPayload";
    private static final String HIT_PAYLOAD = "HitPayload";

    // number of targets
    private static final int DEFAULT_NUM_ICETOP = 2;
    private static final int DEFAULT_NUM_INICE = 6;

    // number of payloads
    private static final int DEFAULT_NUM_TRIGGERS  = 1000;

    private int numIceTop;
    private int numInIce;
    private int numTrigs;
    private String payloadType;
    private boolean seedSet;
    private long randomSeed;

    private String trigOutName;
    private String[] hitOutNames;

    /**
     * Create an eventBuilder input file generator.
     *
     * @param args command-line arguments
     */
    public EventInputDriver(String[] args)
    {
        processArgs(args);
    }

    /**
     * Generate hits.
     */
    private void generate()
    {
        RandomTriggerRequest trigSrc;
        if (!seedSet) {
            trigSrc = new RandomTriggerRequest(hitOutNames.length, 20);
        } else {
            trigSrc = new RandomTriggerRequest(hitOutNames.length, 20,
                                               randomSeed);
        }
        trigSrc.setTargetSourceIds(getDestinations(numIceTop, numInIce));

        ISource[] hitSrcs = trigSrc.getHitSources();
        if (hitSrcs.length != hitOutNames.length) {
            System.err.println("Mismatch between number of hit files (" +
                               hitOutNames.length +
                               ") and number of hit sources (" +
                               hitSrcs.length + ")");
            System.exit(1);
        }

        TriggerRequestGenerator trigGen = new TriggerRequestGenerator();

        IGenerator hitGen;
        if (payloadType.equalsIgnoreCase(HIT_DATA_PAYLOAD)) {
            hitGen = new HitDataGenerator();
        } else {
            hitGen = new HitGenerator();
        }

        // set up trigger output channel
        FileOutputStream trigOut;
        try {
            trigOut = new FileOutputStream(trigOutName);
        } catch (IOException e) {
            log.fatal("ERROR while opening trigger output file: " +
                      trigOutName, e);
            return;
        }
        FileChannel trigChannel = trigOut.getChannel();

        log.info("  Generating random trigger requests");

        // generate trigger requests
        for (int n = 0; n < numTrigs; n++) {
            GenericTriggerRequest trigReq =
                (GenericTriggerRequest) trigSrc.nextPayload();
            if (trigReq == null) {
                break;
            }

            try {
                trigChannel.write(trigGen.generatePayload(trigReq));
            } catch (IOException e) {
                log.fatal("ERROR while writing trigger", e);
            }
        }

        // close trigger output channel
        try {
            trigChannel.close();
        } catch (IOException e) {
            log.error("ERROR while closing trigger output file " +
                      trigOutName, e);
        }

        log.info("  Generating random hits");

        // generate hits
        for (int i = 0; i < hitOutNames.length; i++) {
            // set up hit output channel
            FileOutputStream hitOut;
            try {
                hitOut = new FileOutputStream(hitOutNames[i]);
            } catch (IOException e) {
                log.fatal("ERROR while opening hit output file #" + i +
                          ": " + hitOutNames[i], e);
                return;
            }
            FileChannel hitChannel = hitOut.getChannel();

            while (true) {
                GenericHit hit = (GenericHit) hitSrcs[i].nextPayload();
                if (hit == null) {
                    break;
                }

                try {
                    hitChannel.write(hitGen.generatePayload(hit));
                } catch (IOException e) {
                    log.fatal("ERROR while writing hit", e);
                }
            }

            // close hit output channel
            try {
                hitChannel.close();
            } catch (IOException e) {
                log.error("ERROR while closing hit output file #" + i +
                          ": " + hitOutNames[i], e);
            }
        }
    }

    /**
     * Get a collection of targets.
     *
     * @param numIceTop number of IceTop data handlers
     * @param numInIce number of InIce string processors
     *
     * @return collection of source IDs
     */
    private static final Collection getDestinations(int numIceTop,
                                                    int numInIce)
    {
        ArrayList list = new ArrayList();

        final String iceTopDAQ = DAQCmdInterface.DAQ_ICETOP_DATA_HANDLER;
        for (int i = 0; i < numIceTop; i++) {
            final ISourceID srcId =
                SourceIdRegistry.getISourceIDFromNameAndId(iceTopDAQ, i);
            list.add(srcId);
        }

        final String inIceDAQ = DAQCmdInterface.DAQ_STRINGPROCESSOR;
        for (int i = 0; i < numInIce; i++) {
            final ISourceID srcId =
                SourceIdRegistry.getISourceIDFromNameAndId(inIceDAQ, i);
            list.add(srcId);
        }

        return list;
    }

    /**
     * Process command-line arguments.
     *
     * @param args command-line arguments
     */
    private void processArgs(String[] args)
    {
        numIceTop = DEFAULT_NUM_ICETOP;
        numInIce = DEFAULT_NUM_INICE;
        numTrigs = DEFAULT_NUM_TRIGGERS;
        payloadType = HIT_PAYLOAD;
        trigOutName = null;
        hitOutNames = null;

        ArrayList tmpHitNames = new ArrayList();

        boolean usage = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].length() >= 2 && args[i].charAt(0) == '-') {
                if (args[i].charAt(1) == 'i') {
                    i++;

                    if (i >= args.length) {
                        System.err.println("No argument found for" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                    try {
                        numInIce = Integer.parseInt(args[i]);
                    } catch (NumberFormatException nfe) {
                        numInIce = -1;
                    }

                    if (numInIce <= 0) {
                        System.err.println("Illegal number of in-ice targets" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                } else if (args[i].charAt(1) == 'n') {
                    i++;

                    if (i >= args.length) {
                        System.err.println("No argument found for" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                    try {
                        numTrigs = Integer.parseInt(args[i]);
                    } catch (NumberFormatException nfe) {
                        numTrigs = -1;
                    }

                    if (numTrigs <= 0) {
                        System.err.println("Illegal number of triggers '" +
                                           args[i] + "'");
                        usage = true;
                        break;
                    }

                } else if (args[i].charAt(1) == 'p') {
                    i++;

                    if (i >= args.length) {
                        System.err.println("No argument found for" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                    if (args[i].equalsIgnoreCase(HIT_DATA_PAYLOAD)) {
                        payloadType = HIT_DATA_PAYLOAD;
                    } else if (args[i].equalsIgnoreCase(HIT_PAYLOAD)) {
                        payloadType = HIT_PAYLOAD;
                    } else {
                        System.err.println("Unknown payload type '" + args[i] +
                                           "'");
                        usage = true;
                        break;
                    }

                } else if (args[i].charAt(1) == 's') {
                    i++;

                    if (i >= args.length) {
                        System.err.println("No argument found for" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                    try {
                        randomSeed = Long.parseLong(args[i]);
                        seedSet = true;
                    } catch (NumberFormatException nfe) {
                        seedSet = false;
                    }

                    if (!seedSet) {
                        System.err.println("Illegal random seed value '" +
                                           args[i] + "'");
                        usage = true;
                        break;
                    }

                } else if (args[i].charAt(1) == 't') {
                    i++;

                    if (i >= args.length) {
                        System.err.println("No argument found for" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                    try {
                        numIceTop = Integer.parseInt(args[i]);
                    } catch (NumberFormatException nfe) {
                        numIceTop = -1;
                    }

                    if (numIceTop <= 0) {
                        System.err.println("Illegal number of icetop targets" +
                                           " '" + args[i] + "'");
                        usage = true;
                        break;
                    }

                } else {
                   System.err.println("Unknown option '" + args[i] + "'");
                    usage = true;
                }
            } else if (trigOutName == null) {
                trigOutName = args[i];
            } else {
                tmpHitNames.add(args[i]);
            }
        }

        if (trigOutName == null) {
            System.err.println("Trigger file name not specified");
            usage = true;
        } else if (tmpHitNames.size() == 0) {
            System.err.println("Hit file name(s) not specified");
            usage = true;
        }

        if (usage) {
            System.err.println("Usage: " + getClass().getName() +
                               " [-i <number-of-inice-targets>]" +
                               " [-n <number-of-triggers>]" +
                               " [-payload <" + HIT_PAYLOAD + " | " +
                               HIT_DATA_PAYLOAD + ">]" +
                               " [-s <random-seed>]" +
                               " [-t <number-of-icetop-targets>]" +
                               " triggerFile hitFile [hitFile ...]" +
                               "");
            System.exit(1);
        }

        hitOutNames = new String[tmpHitNames.size()];
        tmpHitNames.toArray(hitOutNames);
    }

    /**
     * Generate eventBuilder input files.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args)
    {
        LoggingConsumer.installDefault();

        EventInputDriver driver = new EventInputDriver(args);
        driver.generate();
    }
}
