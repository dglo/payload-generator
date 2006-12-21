/*
 * class: DomSimulator
 *
 * Version $Id: DomSimulator.java,v 1.16 2006/11/01 00:58:29 artur Exp $
 *
 * Date: May 26 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * This class simulates a DOM (wow).
 *
 * @version $Id: DomSimulator.java,v 1.16 2006/11/01 00:58:29 artur Exp $
 * @author pat
 */
public class DomSimulator
{

    private static final Log log = LogFactory.getLog(DomSimulator.class);

    private static final int HIT_BUFFER_SIZE = 1;

    private static final int TCAL_BUFFER_SIZE = 1;

    private static final int MONI_BUFFER_SIZE = 1;

    private long domId;

    private int hitStartTime;
    private int tcalStartTime;
    private int moniStartTime;

    private TimeConverter timeConverter;

    private IFactory hitFactory;

    private IFactory tcalFactory;

    private IFactory moniFactory;

    private LinkedList hitBuffer;

    private LinkedList tcalBuffer;

    private LinkedList moniBuffer;

    public DomSimulator(long domId, int startUtcTime,
                        double beaconRate, double speRate, double tcalRate,
                        double moniRate, double supernovaRate, long seed) {

        if (log.isInfoEnabled()) {
            String dump = "Constructing DomSimulator with:\n"
                          + "         domId = " + Long.toHexString(domId) + "\n"
                          + "  startUtcTime = " + startUtcTime + " s\n"
                          + "    beaconRate = " + beaconRate + " Hz\n"
                          + "       speRate = " + speRate + " Hz\n"
                          + "      tcalRate = " + tcalRate + " Hz\n"
                          + "      moniRate = " + moniRate + " Hz\n"
                          + " supernovaRate = " + supernovaRate + " Hz\n"
                          + "          seed = " + seed;
            log.info(dump);
        }

        this.domId = domId;

        // hit + moni stream starts after 10 tcal cycles
        hitStartTime = (int) (startUtcTime + 10.0/tcalRate);
        moniStartTime = (int) (startUtcTime + 10.0/tcalRate);
        tcalStartTime = startUtcTime;

        timeConverter = new TimeConverter(0);
        createHitFactory(beaconRate, speRate, seed);
        createTcalFactory(tcalRate);
        createMoniFactory(moniRate, supernovaRate);

        hitBuffer = new LinkedList();
        tcalBuffer = new LinkedList();
        moniBuffer = new LinkedList();
    }

    public DomSimulator(DomConfiguration domConfiguration) {
        this(domConfiguration.getDomId(), domConfiguration.getStartUtcTime(), domConfiguration.getBeaconRate(),
             domConfiguration.getSpeRate(), domConfiguration.getTcalRate(),
             domConfiguration.getMoniRate(), domConfiguration.getSupernovaRate(),
             domConfiguration.getSeed());
    }

    private void createHitFactory(double beaconRate, double speRate, long seed) {
        IStreamGenerator generator = new HitStreamGenerator(hitStartTime, beaconRate, speRate, seed);
        IRecord record = new EngineeringFormatRecord(timeConverter);
        hitFactory = new HitFactory(domId, generator, record);
    }

    private void createTcalFactory(double tcalRate) {
        IStreamGenerator generator = new TcalStreamGenerator(tcalStartTime, tcalRate);
        IRecord record = new TcalRecord(timeConverter);
        tcalFactory = new TcalFactory(domId, generator, record);
    }

    private void createMoniFactory(double moniRate, double supernovaRate) {
        IStreamGenerator generator = new MoniStreamGenerator(moniStartTime, moniRate, supernovaRate);
        IRecord record = new MoniRecord(timeConverter);
        moniFactory = new MoniFactory(domId, generator, record);
    }

    public void start() {
        fillHitBuffer();
        fillTcalBuffer();
        fillMoniBuffer();
    }

    public ByteBuffer getHitRecord() {
        if (hitBuffer.isEmpty()) {
            fillHitBuffer();
        }
        return (ByteBuffer) hitBuffer.removeLast();
    }

    public List getHitRecords(long target) {
        fillHitBuffer(target);
        return hitBuffer;
    }

    public ByteBuffer getTcalRecord() {
        if (tcalBuffer.isEmpty()) {
            fillTcalBuffer();
        }
        return (ByteBuffer) tcalBuffer.removeLast();
    }

    public List getTcalRecords(long target) {
        fillTcalBuffer(target);
        return tcalBuffer;
    }

    public ByteBuffer getMoniRecord() {
        if (moniBuffer.isEmpty()) {
            fillMoniBuffer();
        }
        return (ByteBuffer) moniBuffer.removeLast();
    }

    public List getMoniRecords(long target) {
        fillMoniBuffer(target);
        return moniBuffer;
    }

    private void fillHitBuffer() {
        while (hitBuffer.size() < HIT_BUFFER_SIZE) {
            ByteBuffer hit = hitFactory.nextBuffer();
            //log.info(EngineeringFormatRecord.dumpRecord(hit));
            hitBuffer.addFirst(addHitHeader(hit, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling hit buffer in dom " + domId + ", hitBuffer = " + hitBuffer.size());
            }
        }
    }

    private void fillHitBuffer(long target) {
        hitBuffer.clear();
        List bufferList = hitFactory.nextBuffers(target);
        Iterator bufferIter = bufferList.iterator();
        while (bufferIter.hasNext()) {
            ByteBuffer hit = (ByteBuffer) bufferIter.next();
            //log.info(EngineeringFormatRecord.dumpRecord(hit));
            hitBuffer.addFirst(addHitHeader(hit, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling hit buffer in dom " + domId + ", hitBuffer = " + hitBuffer.size());
            }
        }
    }

    private void fillTcalBuffer() {
        while (tcalBuffer.size() < TCAL_BUFFER_SIZE) {
            ByteBuffer tcal = tcalFactory.nextBuffer();
            tcalBuffer.addFirst(addTcalHeader(tcal, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling tcal buffer in dom " + domId + ", tcalBuffer = " + tcalBuffer.size());
            }
        }
    }

    private void fillTcalBuffer(long target) {
        tcalBuffer.clear();
        List bufferList = tcalFactory.nextBuffers(target);
        Iterator bufferIter = bufferList.iterator();
        while (bufferIter.hasNext()) {
            ByteBuffer tcal = (ByteBuffer) bufferIter.next();
            tcalBuffer.addFirst(addTcalHeader(tcal, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling tcal buffer in dom " + domId + ", tcalBuffer = " + tcalBuffer.size());
            }
        }
    }

    private void fillMoniBuffer() {
        while (moniBuffer.size() < MONI_BUFFER_SIZE) {
            ByteBuffer moni = moniFactory.nextBuffer();
            moniBuffer.addFirst(addMoniHeader(moni, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling moni buffer in dom " + domId + ", moniBuffer = " + moniBuffer.size());
            }
        }
    }

    private void fillMoniBuffer(long target) {
        moniBuffer.clear();
        List bufferList = moniFactory.nextBuffers(target);
        Iterator bufferIter = bufferList.iterator();
        while (bufferIter.hasNext()) {
            ByteBuffer moni = (ByteBuffer) bufferIter.next();
            moniBuffer.addFirst(addMoniHeader(moni, Long.toHexString(domId)));
            if (log.isDebugEnabled()) {
                log.debug("Filling moni buffer in dom " + domId + ", moniBuffer = " + moniBuffer.size());
            }
        }
    }

    private static ByteBuffer addHitHeader(ByteBuffer hit, String domId) {
        ByteBuffer header = DomHubHeader.createHitHeader(hit.capacity(), Long.parseLong(domId, 16));
        return appendBuffers(header, hit);
    }

    private static ByteBuffer addTcalHeader(ByteBuffer tcal, String domId) {
        ByteBuffer header = DomHubHeader.createTcalHeader(tcal.capacity(), Long.parseLong(domId,16));
        return appendBuffers(header, tcal);
    }

    private static ByteBuffer addMoniHeader(ByteBuffer moni, String domId) {
        ByteBuffer header;
        if (MoniRecord.isSuperNova(moni)) {
            header = DomHubHeader.createSNHeader(moni.capacity(), Long.parseLong(domId,16));
        } else {
            header = DomHubHeader.createMoniHeader(moni.capacity(), Long.parseLong(domId, 16));
        }
        return appendBuffers(header, moni);
    }

    /*
    private ByteBuffer addHitHeader(List hits) {
        ByteBuffer header = DomHubHeader.createHitHeader(domId);
        ByteBuffer buffer = appendBuffers(header, (ByteBuffer) hits.get(0));
        for (int i=1; i<hits.size(); i++) {
            buffer = appendBuffers(buffer, (ByteBuffer) hits.get(i));
        }
        return buffer;
    }
    */

    private static ByteBuffer appendBuffers(ByteBuffer buffer1, ByteBuffer buffer2) {

        ByteBuffer newBuffer = ByteBuffer.allocate(buffer1.limit() + buffer2.limit());
        newBuffer.put(buffer1);
        newBuffer.put(buffer2);
        return newBuffer;

        /*
        byte[] array1 = buffer1.array();
        byte[] array2 = buffer2.array();
        byte[] array12 = new byte[array1.length + array2.length];
        for (int i=0; i<array1.length; i++) {
            array12[i] = array1[i];
        }
        for (int i=0; i<array2.length; i++) {
            array12[array1.length + i] = array2[i];
        }
        return ByteBuffer.wrap(array12);
        */
    }


    public long getID(){
        return domId;
    }

    /**
     * Get the ID for this Simulated DOM
     *
     * @return a String representation of DOM ID
     */
    public String getDomID() {
        return null;
    }

    /**
     * This is the channel that will be used to send data and moni/sn packets to the upper layer classes based on their
     * requests.
     *
     * @return a SocketChannel object
     */
    public SocketChannel getSocketChannel() {
        return null;
    }

    /**
     * Get the port for this socket channel
     *
     * @return a int value
     */
    public int getSocketChannelPort() {
        return 0;
    }

    /**
     * Fill a given ByteBuffer with a tcal record which contains the tcal header + tcal record + gps record.
     *
     * @param tcalRecord
     */
    public void fillTCalRecord(ByteBuffer tcalRecord) {

        // Here we want to create a generic tcal record and write it into the tcalBuffer
        tcalRecord.clear();

        // Write in the header
        DomHubHeader.fillTcalHeader(292, Long.toHexString(domId), tcalRecord);

        // Get the current time in UTC
        long utcTime = TimeConverterService.getUtcTime();
        TcalRecord.fillRecord(utcTime, Long.toHexString(domId), tcalRecord);
        tcalRecord.flip();
    }

}
