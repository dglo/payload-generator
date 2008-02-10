/*
 * class: Driver
 *
 * Version $Id: SimpleFileReader.java 2629 2008-02-11 05:48:36Z dglo $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class creates GenericHits from a simple text file of the form:
 *
 * timeStamp domId sourceId triggerMode
 *
 * @version $Id: SimpleFileReader.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class SimpleFileReader
        implements ISource
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(SimpleFileReader.class);

    /**
     * reader
     */
    private BufferedReader reader;

    /**
     * hit counter
     */
    private int numberOfHits;

    /**
     * constructor
     * @param fileName name of text file to read
     */
    public SimpleFileReader(String fileName) {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            log.fatal("ERROR while opening input file: " + fileName, e);
        }
        numberOfHits = 0;
    }

    public SimpleFileReader(InputStream input) {
        reader = new BufferedReader(new InputStreamReader(input));
        numberOfHits = 0;
    }

    /**
     * get next hit
     * @param timeStamp ignored
     * @return a generic hit or null if EOF
     */
    public Generic nextPayload(long timeStamp) {
        return nextPayload();
    }

    /**
     * get next hit
     * @return a generic hit or null if EOF
     */
    public Generic nextPayload() {

        // read next line
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            log.fatal("ERROR while reading input file", e);
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

        // split line and insure that we have 4 fields
        String[] fields = line.split("\\s+");
        if (fields.length != 4) {
            log.error("ERROR while splitting line");
            close();
            return null;
        }

        // form generic hit
        numberOfHits++;
        long timeStamp   = Long.parseLong(fields[0]);
        long domId       = Long.parseLong(fields[1]);
        int  sourceId    = Integer.parseInt(fields[2]);
        int  triggerMode = Integer.parseInt(fields[3]);

        log.info("Hit " + numberOfHits + ":");
        log.info("       TimeStamp   = " + timeStamp);
        log.info("       DomId       = " + domId);
        log.info("       SourceId    = " + sourceId);
        log.info("       TriggerMode = " + triggerMode);

        return new GenericHit(timeStamp, domId, sourceId, triggerMode);

    }

    public int getNumberOfHits() {
        return numberOfHits;
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
