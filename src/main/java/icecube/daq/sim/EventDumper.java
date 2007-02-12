/*
 * class: PayloadDumper
 *
 * Version $Id: EventDumper.java,v 1.1 2005/11/01 22:36:37 toale Exp $
 *
 * Date: January 27 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.ILoadablePayload;
import icecube.daq.payload.PayloadReader;
import icecube.daq.payload.MasterPayloadFactory;
import icecube.daq.payload.PayloadDestination;
import icecube.daq.payload.FilePayloadDestination;
import icecube.daq.payload.PayloadInterfaceRegistry;
import icecube.daq.payload.splicer.Payload;
import icecube.daq.eventbuilder.IEventPayload;
import icecube.daq.eventbuilder.IReadoutDataPayload;
import icecube.daq.trigger.IHitDataPayload;
import icecube.icebucket.logging.LoggingConsumer;

import java.io.IOException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class dumps payloads to a StringFilePayloadDestination
 *
 * @version $Id: EventDumper.java,v 1.1 2005/11/01 22:36:37 toale Exp $
 * @author pat
 */
public class EventDumper
{

    /**
     * Log object.
     */
    private static final Log log = LogFactory.getLog(EventDumper.class);

    /**
     * Name of inice hit file.
     */
    String iniceHitFile = null;

    /**
     * Name of icetop hit file.
     */
    String icetopHitFile = null;

    /**
     * Payload reader.
     */
    PayloadReader reader = null;

    /**
     * Factory for creating payloads.
     */
    MasterPayloadFactory inputFactory = null;

    /**
     * Destination to dump inice hits to.
     */
    PayloadDestination iniceHitDestination = null;

    /**
     * Destination to dump icetop hits to.
     */
    PayloadDestination icetopHitDestination = null;

    /**
     * Event count.
     */
    int eventCount = 0;

    /**
     * Count of inice hits.
     */
    int iniceHitCount = 0;

    /**
     * Count if icetop hits.
     */
    int icetopHitCount = 0;

    /**
     * constructor, sets up IO
     * @param iniceHitFile file for inice hits
     * @param icetopHitFile file for icetop hits
     */
    public EventDumper(String iniceHitFile, String icetopHitFile) {

        inputFactory = new MasterPayloadFactory();
        try {
            iniceHitDestination = new FilePayloadDestination(iniceHitFile);
            icetopHitDestination = new FilePayloadDestination(icetopHitFile);
        } catch (IOException e) {
            log.error("Error opening PayloadDestination", e);
        }

    }

    /**
     * read and dump
     */
    private void dump(String eventFile){

        reader = new PayloadReader(eventFile);
        try {
            reader.open();
        } catch (IOException e) {
            log.error("Error opening PayloadReader", e);
        }

        ByteBuffer buffer = ByteBuffer.allocate(320000);
        boolean go = true;
        while (go) {
            buffer.clear();
            Payload payload = null;
            try {
                reader.readNextPayload(buffer);
                payload =  inputFactory.createPayload(0,buffer);
                payload.loadPayload();
            } catch (EOFException eof) {
                log.info("Reached end of file\n"
                       + "   Read " + eventCount + " events\n"
                       + "   Wrote " + iniceHitCount + " inice hits\n"
                       + "     and " + icetopHitCount + " icetop hits");
            } catch (Exception e) {
                log.error("Error loading EventPayload", e);
                payload = null;
            }

            if (payload == null) {
                go = false;
            } else {
                if (payload.getPayloadInterfaceType() == PayloadInterfaceRegistry.I_EVENT_PAYLOAD) {
                    eventCount++;
                    processEvent((IEventPayload) payload);
                } else {
                    log.warn("Input payload is not an EventPayload, skipping");
                }
            }

        }

    }

    private void processEvent(IEventPayload payload) {

        Vector readoutDataPayloads = payload.getReadoutDataPayloads();
        for (int i=0; i<readoutDataPayloads.size(); i++) {
            IReadoutDataPayload readoutDataPayload = (IReadoutDataPayload) readoutDataPayloads.get(i);
            try {
                ((ILoadablePayload) readoutDataPayload).loadPayload();
            } catch (Exception e) {
                log.error("Error loading ReadoutDataPayload");
            }
            Vector dataPayloads = readoutDataPayload.getDataPayloads();
            for (int j=0; j<dataPayloads.size(); j++) {
                IHitDataPayload hitDataPayload = (IHitDataPayload) dataPayloads.get(j);
                String domId = hitDataPayload.getDOMID().getDomIDAsString();

                if (PositionRegistry.isInice(domId)) {
                    log.info("Hit (" + i + "," + j + ") from DOM " + domId + " is inice");
                    try {
                        iniceHitDestination.writePayload((Payload) hitDataPayload);
                    } catch (IOException e) {
                        log.error("Error writing payload", e);
                    }
                    iniceHitCount++;

                } else {
                    log.info("Hit (" + i + "," + j + ") from DOM " + domId + " is icetop");
                    try {
                        icetopHitDestination.writePayload((Payload) hitDataPayload);
                    } catch (IOException e) {
                        log.error("Error writing payload", e);
                    }
                    icetopHitCount++;
                }
            }
        }


    }

    /**
     * main, creates PayloadDumper and starts dumping
     * @param args
     */
    public static void main(String args[]) {

        LoggingConsumer.installDefault();

        // Setup dumper
        EventDumper dumper = null;
        if (args.length >= 3) {
            dumper = new EventDumper(args[0], args[1]);
        } else {
            System.err.println("Usage: PayloadDumper <iniceHitFile> <icetopHitFile> <eventFile1> [<eventFile2> ...]");
            return;
        }

        // Dump each event file
        for (int i=2; i<args.length; i++) {
            dumper.dump(args[i]);
        }

    }

}
