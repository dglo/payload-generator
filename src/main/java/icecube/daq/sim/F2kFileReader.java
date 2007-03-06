/*
 * class: F2kParser
 *
 * Version $Id: F2kFileReader.java,v 1.7 2005/11/07 18:05:07 dglo Exp $
 *
 * Date: February 25 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * This class parses an f2k file and creats a list of GenericHits
 *
 * @version $Id: F2kFileReader.java,v 1.7 2005/11/07 18:05:07 dglo Exp $
 * @author pat
 */
public class F2kFileReader
        implements ISource
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(F2kFileReader.class);

    /**
     * strings used to parse file
     */
    private static final String ARRAY_LINE       = "^ARRAY.*";
    private static final String OM_LINE          = "^OM.*";
    private static final String EVENT_START_LINE = "^EM.*";
    private static final String EVENT_END_LINE   = "^EE.*";
    private static final String HIT_LINE         = "^HT.*";

    /**
     * max size of amanda events
     */
    private static final int EVENT_LENGTH = 400000;  // 40 mus un units of 1/10 ns

    /**
     * reader of f2k file
     */
    private BufferedReader f2kReader;

    /**
     * geometry info
     */
    private int nStrings;
    private int nModules;

    /**
     * default rates
     */
    private static final double DEFAULT_NOISE_RATE_PER_MODULE = 800.0e-10; // 800 Hz in units of 1/10 ns
    private double noiseRatePerModule = DEFAULT_NOISE_RATE_PER_MODULE;

    private long startTime = 0;
    private long stopTime  = 0;
    private int  nEvents   = 0;

    /**
     * random number generators
     */
    private TimeGenerator eventTimeGenerator;
    private TimeGenerator noiseTimeGenerator;

    /**
     * list of hits
     */
    private List eventHitList = new ArrayList();
    private SortedSet hitSet = new TreeSet();
    private List finalList = new ArrayList();

    private int[] om2string;

    /**
     * local coincidence mode:
     *   0 = no lc
     *   1 = hard lc
     *   2 = soft lc
     *   3 = flabby lc
     */
    private int lcMode = 0;

    /**
     * size of local coincidence window in 1/10 ns
     */
    private long lcWindow = 8000;

    /**
     * number of neighbors looked at
     */
    private int lcDistance = 1;

    private long lastEventStartTime = -EVENT_LENGTH; // units are 1/10 ns
    private long lastEventEndTime = 0;               // units are 1/10 ns
    private Random random = new Random();

    /**
     * constructor that takes the name of the f2k file, the local coincidence mode, the event rate, and the
     * noise rate per DOM
     * @param f2kFile name of file containing f2k formated events
     * @param lcMode local coincidence mode
     * @param nEventsInFile number of events in f2k file
     * @param lifeTime lifetime of file in s
     * @param noiseRatePerModule noise rate per DOM in units of 1/10 ns
     */
    public F2kFileReader(String f2kFile, int lcMode, int nEventsInFile, float lifeTime, double noiseRatePerModule) {
        this(openFile(f2kFile), lcMode, nEventsInFile, lifeTime, noiseRatePerModule);
    }

    /**
     * constructor that takes the f2k file stream, the local coincidence mode, the event rate, and the
     * noise rate per DOM
     * @param f2kStream file stream containing f2k formated events
     * @param lcMode local coincidence mode
     * @param nEventsInFile number of events in f2k file
     * @param lifeTime lifetime of file in s
     * @param noiseRatePerModule noise rate per DOM in units of 1/10 ns
     */
    public F2kFileReader(InputStream f2kStream, int lcMode, int nEventsInFile, float lifeTime, double noiseRatePerModule) {
        this(openStream(f2kStream), lcMode, nEventsInFile, lifeTime, noiseRatePerModule);
    }

    private F2kFileReader(BufferedReader f2kReader, int lcMode, int nEventsInFile, float lifeTime, double noiseRatePerModule) {

        this.f2kReader = f2kReader;
        this.lcMode = lcMode;
        this.noiseRatePerModule = noiseRatePerModule;

        double eventRate = nEventsInFile/(lifeTime*1e10);
        eventTimeGenerator = new TimeGenerator(eventRate);


        log.info("Processing F2K file with " + nEventsInFile + " events");
        log.info("   LC mode   = " + lcMode);
        log.info("   LifeTime  = " + lifeTime + " seconds");
        log.info("   NoiseRate = " + noiseRatePerModule + " per OM (units are 1/10 ns)");

        // parse header
        try {
            parseHeader();
        } catch (IOException e) {
            log.fatal("Error reading header!", e);
        }

        // parse events
        try {
            parseEvents(nEventsInFile);
        } catch (IOException e) {
            log.error("Error parsing events!", e);
        }

        // close file reader
        try {
            close();
        } catch (IOException e) {
            log.error("Error closing file!", e);
        }

        // now generate noise hits
        if (noiseRatePerModule > 0) {
            generateNoise();
        }

        // now merge the event hits into the hitSet
        hitSet.addAll(eventHitList);

        // scale all times to lifeTime
        if (lifeTime > 0.0) {
            double lastTime = ((GenericF2kHit) hitSet.last()).getTimeStamp();
            double scale = (lifeTime*1e10)/lastTime;
            scaleTimes(scale);
        }

        // finally check local coincidence
        if (lcMode != 0) {
            checkLC();
        }

    }

    /**
     * check for local coincidence within an event
     */
    private void checkLC() {

        log.info("Checking LC:");
        log.info("  FinalList size = " + finalList.size());

        Set lcSet = new HashSet();

        // loop over all hits
        for (int i=0; i<finalList.size(); i++) {

            GenericF2kHit hit1 = (GenericF2kHit) finalList.get(i);
            long domId1 = hit1.getDomId();
            int string1 = hit1.getStringId();
            long time1  = hit1.getTimeStamp();

            // loop over all remaining hits
            for (int j=i+1; j<finalList.size(); j++) {
                GenericF2kHit hit2 = (GenericF2kHit) finalList.get(j);
                long domId2 = hit2.getDomId();
                int string2 = hit2.getStringId();
                long time2  = hit2.getTimeStamp();

                // first check time and break if time2-time1 > lcWindow
                if (time2 < time1) {
                    log.fatal("Hits are not time-ordered!: " + time1 + " " + time2);
                } else if ((time2-time1) <= lcWindow) {
                    // now check string
                    if (string1 == string2) {
                        // then check domId
                        if ((Math.abs(domId2-domId1) != 0) && (Math.abs(domId2-domId1) <= lcDistance)) {
                            hit1.setLcTag(lcMode);
                            hit2.setLcTag(lcMode);
                            lcSet.add(hit1);
                            lcSet.add(hit2);
                        }
                    }
                } else {
                    break;
                }

            }

        }

        log.info("  Number of Hits with LC = " + lcSet.size());

    }

    /**
     * close file
     * @throws IOException if there is an error closing
     */
    private void close()
            throws IOException {

        double time = 1.0e-10*(stopTime - startTime);
        log.info("Generated " + nEvents + " events over " + time + " seconds");
        log.info("  " + eventHitList.size() + " hits from events");

        f2kReader.close();
    }

    /**
     * get local coincidence time window
     * @return length of window in 1/10 ns
     */
    public long getLcWindow() {
        return lcWindow;
    }

    /**
     * generate noise hits from 0 to lifeTime seconds
     * domId is uniform from 1 to nModules
     */
    private void generateNoise() {

        log.info("Generating noise:");

        // create generator
        noiseTimeGenerator = new TimeGenerator(nModules*noiseRatePerModule);

        long firstHitTime = 0;

        long lastEventHitTime = ((GenericF2kHit) eventHitList.get(eventHitList.size()-1)).getTimeStamp();

        int nNoiseHits = 0;
        long lastHitTime = 0;
        while (lastHitTime < (lastEventHitTime + EVENT_LENGTH)) {
            long currentHitTime = lastHitTime + (long) noiseTimeGenerator.nextTime();

            // make a hit
            GenericF2kHit hit = new GenericF2kHit();
            int domIndex = random.nextInt(nModules);
            hit.setDomId(1 + domIndex);
            hit.setStringId(om2string[domIndex]);
            hit.setTimeStamp(currentHitTime);
            hit.setTriggerMode(2);
            hit.setSourceId(4000);
            hit.setLcTag(0);

            // add to set and update last time
            hitSet.add(hit);
            lastHitTime = currentHitTime;
            nNoiseHits++;

            if (firstHitTime == 0) {
                firstHitTime = currentHitTime;
            }

        }

        log.info("  " + nNoiseHits + " noise hits generated");
        log.info("   from " + firstHitTime + " to " + lastHitTime);

    }

    /**
     * return the next available hit
     * @param timeStamp ignored
     * @return a Generic hit
     */
    public Generic nextPayload(long timeStamp) {
        return nextPayload();
    }

    /**
     * return the next available hit
     * @return a Generic hit
     */
    public Generic nextPayload() {

        if (hitSet.isEmpty()) {
            return null;
        } else {
            GenericF2kHit hit = (GenericF2kHit) hitSet.first();
            hitSet.remove(hit);
            return hit;
        }

    }

    private static BufferedReader openFile(String name) {

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(name));
        } catch (FileNotFoundException e) {
            log.fatal("File not found!", e);
            reader = null;
        }

        return reader;
    }

    private static BufferedReader openStream(InputStream stream) {

        return new BufferedReader(new InputStreamReader(stream));
    }

    /**
     * parse f2k event and create list of GenericHits
     * adds noise hits between the end of the last event and the current one
     * @param nEventsInFile number of events to read
     * @throws IOException when there is an error reading
     */
    private void parseEvents(int nEventsInFile)
            throws IOException {

        // loop over number of events
        for (int i=0; i<nEventsInFile; i++) {

            // loop over lines until we get a complete event
            boolean goodEvent = false;
            do {

                String line = f2kReader.readLine();

                if (line == null) {
                    // hopefully this is EOF
                    throw new IOException();
                } else if (Pattern.matches(EVENT_START_LINE, line)) {

                    //log.info("Found Start of New Event");

                    // generate a UTC time for this event in units of 1/10 ns
                    long nextEventStartTime = lastEventEndTime + (long) eventTimeGenerator.nextTime();

                    log.info("Event " + nEvents + " starts at " + nextEventStartTime);

                    if (startTime == 0) {
                        startTime = nextEventStartTime;
                    }
                    stopTime = nextEventStartTime;
                    nEvents++;

                    // update times
                    lastEventStartTime = nextEventStartTime;
                    lastEventEndTime = lastEventStartTime + EVENT_LENGTH; // units are 1/10 ns

                    // now loop over hits
                    boolean gotHits = true;
                    while (gotHits) {
                        line = f2kReader.readLine();

                        if (line == null) {
                            log.error("NULL");
                        } else if (Pattern.matches(HIT_LINE, line)) {

                            // new hit
                            //log.info("Found new hit");
                            GenericF2kHit hit = new GenericF2kHit();

                            String [] fields = line.split("\\s+");
                            int domNum = Integer.parseInt(fields[1]);
                            hit.setDomId((long) domNum);
                            hit.setStringId(om2string[domNum - 1]);
                            hit.setTriggerMode(2);
                            hit.setSourceId(4000);
                            hit.setLcTag(0);
                            if (Pattern.matches("\\?", fields[5])) {
                                log.warn("Yikes, unknown time");
                            } else {
                                // units are 1/10 ns
                                long time = (long) (10.0F*Float.parseFloat(fields[5]));
                                hit.setTimeStamp( lastEventStartTime + time );
                                eventHitList.add(hit);
                            }

                        } else if (Pattern.matches(EVENT_END_LINE, line)) {
                            // end of event
                            //log.info("Found End of Event");
                            gotHits = false;
                            goodEvent = true;
                        }
                    }
                }

            } while (!goodEvent);

        }


    }

    /**
     * parse f2k header for geometry information
     * @throws IOException when there is an error reading
     */
    private void parseHeader()
            throws IOException {

        boolean inNeed = true;
        while (inNeed) {
            String line = f2kReader.readLine();

            if (line == null) {
                IOException e = new IOException();
                log.error("Line is null", e);
                throw e;
            } else if (Pattern.matches(ARRAY_LINE, line)) {

                // geometry information
                String [] fields = line.split("\\s+");
                nStrings = Integer.parseInt(fields[5]);
                nModules = Integer.parseInt(fields[6]);

                log.info("Found Geometry Information: nModules = " + nModules);
                log.info("                            nStrings = " + nStrings);

                om2string = new int[nModules];

            } else if (Pattern.matches(OM_LINE, line)) {

                // om position information
                String [] fields = line.split("\\s+");
                int domNum = Integer.parseInt(fields[1]);
                int string = Integer.parseInt(fields[3]);
                om2string[domNum - 1] = string;

                // loop over other om lines
                for (int i=1; i<nModules; i++) {
                    line = f2kReader.readLine();

                    fields = line.split("\\s+");
                    domNum = Integer.parseInt(fields[1]);
                    string = Integer.parseInt(fields[3]);
                    om2string[domNum - 1] = string;
                }

                inNeed = false;

            }
        }

    }

    private void scaleTimes(double scale) {

        log.info("Scaling hits by " + scale);

        Iterator iter = hitSet.iterator();
        while (iter.hasNext()) {
            GenericF2kHit hit = (GenericF2kHit) iter.next();
            long oldTime = hit.getTimeStamp();
            long newTime = (long) (scale*oldTime);
            hit.setTimeStamp(newTime);
            finalList.add(hit);
        }

        long lastTime = ((GenericF2kHit) finalList.get(finalList.size()-1)).getTimeStamp();
        log.info("Last time is now " + lastTime);
    }

    /**
     * set local coincidence time window
     * @param lcWindow length of window in 1/10 ns
     */
    public void setLcWindow(long lcWindow) {
        this.lcWindow = lcWindow;
    }

}
