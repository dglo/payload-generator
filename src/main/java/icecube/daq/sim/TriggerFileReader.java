/*
 * class: Driver
 *
 * Version $Id: TriggerFileReader.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: June 2 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class creates GenericTriggers from a simple text file of the form:
 *
 *
 * @version $Id: TriggerFileReader.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class TriggerFileReader
        implements ISource
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(TriggerFileReader.class);

    /**
     * reader
     */
    private BufferedReader reader;

    /**
     * trigger counter
     */
    private int numberOfTriggers;

    /**
     * constructor
     * @param fileName name of text file to read
     */
    public TriggerFileReader(String fileName) {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            log.fatal("ERROR while opening input file: " + fileName, e);
        }
        numberOfTriggers = 0;
    }

    /**
     * constructor
     * @param stream input stream to text file to read
     */
    public TriggerFileReader(InputStream stream) {
        reader = new BufferedReader(new InputStreamReader(stream));
        numberOfTriggers = 0;
    }

    /**
     * get next trigger
     * @param timeStamp ignored
     * @return a generic trigger or null if EOF
     */
    public Generic nextPayload(long timeStamp) {
        return nextPayload();
    }

    /**
     * get next trigger
     * @return a generic trigger or null if EOF
     */
    public Generic nextPayload() {

        // read next line
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            log.info("IOException: Is this EOF?");
        }

        // if line is null, EOF
        if (line == null) {
            close();
            return null;
        }

        // skip comment lines
        boolean skip = true;
        do {
            if (line.startsWith("#")) {
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    log.fatal("ERROR while reading input file", e);
                }
            } else {
                skip = false;
            }
        } while (skip);

        // split line and insure that we have at least 6 fields
        String[] fields = line.split("\\s+");
        if (fields.length < 6) {
            log.error("ERROR while splitting line");
            close();
            return null;
        }

        // form generic trigger
        numberOfTriggers++;
        int triggerUID = Integer.parseInt(fields[0]);
        int triggerType = Integer.parseInt(fields[1]);
        int triggerConfigId = Integer.parseInt(fields[2]);
        int sourceId = Integer.parseInt(fields[3]);
        long firstTime = Long.parseLong(fields[4]);
        long lastTime = Long.parseLong(fields[5]);

        log.info("Trigger " + numberOfTriggers + ":");
        log.info("       TriggerUID      = " + triggerUID);
        log.info("       TriggerType     = " + triggerType);
        log.info("       TriggerConfigId = " + triggerConfigId);
        log.info("       SourceId        = " + sourceId);
        log.info("       FirstTime       = " + firstTime);
        log.info("       LastTime        = " + lastTime);

        GenericTrigger trigger = new GenericTrigger();
        trigger.setTriggerUID(triggerUID);
        trigger.setTriggerType(triggerType);
        trigger.setTriggerConfigId(triggerConfigId);
        trigger.setSourceId(sourceId);
        trigger.setFirstTime(firstTime);
        trigger.setLastTime(lastTime);

        // now see if there are readout elements
        Vector readouts = new Vector();
        int numberOfReadouts = (fields.length - 6)/5;
        for (int i=0; i<numberOfReadouts; i++) {

            int readoutType = Integer.parseInt(fields[6 + 5*i + 0]);
            long readoutFirstTime = Long.parseLong(fields[6 + 5*i + 1]);
            long readoutLastTime = Long.parseLong(fields[6 + 5*i + 2]);
            int domId = Integer.parseInt(fields[6 + 5*i + 3]);
            int stringId = Integer.parseInt(fields[6 + 5*i + 4]);

            log.info("       Readout: " + i);
            log.info("         ReadoutType = " + readoutType);
            log.info("         FirstTime   = " + readoutFirstTime);
            log.info("         LastTime    = " + readoutLastTime);
            log.info("         DomId       = " + domId);
            log.info("         SourceId    = " + stringId);

            GenericReadoutElement readout = new GenericReadoutElement();
            readout.setReadoutType(readoutType);
            readout.setFirstTime(readoutFirstTime);
            readout.setLastTime(readoutLastTime);
            readout.setDomId(domId);
            readout.setSourceId(stringId);

            readouts.add(readout);
        }
        trigger.setReadoutElementList(readouts);



        return trigger;

    }

    public int getNumberOfTriggers() {
        return numberOfTriggers;
    }

    /**
     * close reader
     */
    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
            log.fatal("ERROR while closing input file", e);
        }
    }

}
