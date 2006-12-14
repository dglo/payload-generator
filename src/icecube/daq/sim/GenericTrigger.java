/*
 * class: GenericTrigger
 *
 * Version $Id: GenericTrigger.java,v 1.4 2005/12/20 19:44:41 dglo Exp $
 *
 * Date: June 7 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.util.Vector;

/**
 * This class is a containor for generic trigger information
 *
 * @version $Id: GenericTrigger.java,v 1.4 2005/12/20 19:44:41 dglo Exp $
 * @author pat
 */
public class GenericTrigger
        extends Generic
{

    private int triggerUID;
    private int triggerType;
    private int triggerConfigId;
    private int sourceId;
    private long firstTime;
    private long lastTime;
    private Vector hitList;
    private Vector readoutElementList;

    public GenericTrigger() {
        hitList = new Vector();
        readoutElementList = new Vector();
    }

    public long getTimeStamp() {
        return this.getFirstTime();
    }

    public void setTimeStamp(long timeStamp) {
        this.setFirstTime(timeStamp);
    }

    public int getTriggerUID() {
        return triggerUID;
    }

    public void setTriggerUID(int triggerUID) {
        this.triggerUID = triggerUID;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public int getTriggerConfigId() {
        return triggerConfigId;
    }

    public void setTriggerConfigId(int triggerConfigId) {
        this.triggerConfigId = triggerConfigId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public long getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(long firstTime) {
        this.firstTime = firstTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public Vector getHitList() {
        return hitList;
    }

    public void setHitList(Vector hitList) {
        this.hitList.clear();
        this.hitList.addAll(hitList);
    }

    public Vector getReadoutElementList() {
        return readoutElementList;
    }

    public void setReadoutElementList(Vector readoutElementList) {
        this.readoutElementList = readoutElementList;
    }

    /**
     * Add a readout element to this trigger.
     *
     * @param readout new element
     */
    public void addReadoutElement(GenericReadoutElement readout) {
        if (readoutElementList == null) {
            readoutElementList = new Vector();
        }

        readoutElementList.add(readout);
    }

    public String toString() {
        return "GenericTrigger[UID#" + triggerUID + " type#" + triggerType +
            " cfgId#" + triggerConfigId + " Src#" + sourceId +
            " [" + firstTime + "-" + lastTime + "]]";
    }

}
