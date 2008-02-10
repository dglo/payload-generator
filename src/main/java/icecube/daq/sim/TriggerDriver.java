/*
 * class: TriggerDriver
 *
 * Version $Id: TriggerDriver.java 2629 2008-02-11 05:48:36Z dglo $
 *
 * Date: June 8 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.IDOMID;
import icecube.daq.payload.IPayload;
import icecube.daq.payload.ISourceID;
import icecube.daq.payload.IUTCTime;
import icecube.daq.payload.impl.SourceID4B;
import icecube.daq.payload.impl.UTCTime8B;
import icecube.daq.trigger.IReadoutRequest;
import icecube.daq.trigger.IReadoutRequestElement;
import icecube.daq.trigger.impl.DOMID8B;
import icecube.daq.trigger.impl.TriggerRequestPayload;
import icecube.daq.trigger.impl.TriggerRequestPayloadFactory;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 * This class ...does what?
 *
 * @version $Id: TriggerDriver.java 2629 2008-02-11 05:48:36Z dglo $
 * @author pat
 */
public class TriggerDriver
{

    /**
     * source of GenericTriggers
     */
    private ISource hitSource;

    /**
     * payload factory
     */
    private TriggerRequestPayloadFactory payloadFactory = new TriggerRequestPayloadFactory();

    /**
     * constructor
     * @param fileName name of file with simple triggers
     */
    public TriggerDriver(String fileName) {
        hitSource = new TriggerFileReader(fileName);
    }

    /**
     * constructor
     * @param stream input stream to file with simple triggers
     */
    public TriggerDriver(InputStream stream) {
        hitSource = new TriggerFileReader(stream);
        payloadFactory = new TriggerRequestPayloadFactory();
    }

    /**
     * get next trigger
     * @return IPayload (or null if EOF)
     */
    public IPayload nextTrigger() {
        GenericTrigger genericTrigger = (GenericTrigger) hitSource.nextPayload();
        if (genericTrigger == null) {
            return null;
        }

        // first form readout request elements
        Vector readoutElements = new Vector();
        Vector readouts = genericTrigger.getReadoutElementList();
        Iterator iter = readouts.iterator();
        while (iter.hasNext()) {
            GenericReadoutElement readout = (GenericReadoutElement) iter.next();
            IDOMID domId = null;
            ISourceID sourceId = null;
            if (readout.getDomId() != -1) {
                domId = new DOMID8B(readout.getDomId());
            }
            if (readout.getSourceId() != -1) {
                sourceId = new SourceID4B(readout.getSourceId());
            }

            IReadoutRequestElement readoutElement
                    = TriggerRequestPayloadFactory.createReadoutRequestElement(
                            readout.getReadoutType(),
                            (IUTCTime) new UTCTime8B(readout.getFirstTime()),
                            (IUTCTime) new UTCTime8B(readout.getLastTime()),
                            domId,
                            sourceId);
            readoutElements.add(readoutElement);

        }
        // now form the readout request
        IReadoutRequest readoutRequest
                = TriggerRequestPayloadFactory.createReadoutRequest(
                        (ISourceID) new SourceID4B(genericTrigger.getSourceId()),
                        genericTrigger.getTriggerUID(),
                        readoutElements);

        // finally form trigger request
        TriggerRequestPayload triggerPayload
                = (TriggerRequestPayload) payloadFactory.createPayload(
                        genericTrigger.getTriggerUID(),
                        genericTrigger.getTriggerType(),
                        genericTrigger.getTriggerConfigId(),
                        (ISourceID) new SourceID4B(genericTrigger.getSourceId()),
                        (IUTCTime) new UTCTime8B(genericTrigger.getFirstTime()),
                        (IUTCTime) new UTCTime8B(genericTrigger.getLastTime()),
                        genericTrigger.getHitList(),
                        readoutRequest);

        return triggerPayload;

    }

    public static void main(String[] args) {

        TriggerDriver driver = new TriggerDriver(args[0]);
        while (null != driver.nextTrigger()) {

        }


    }

}
