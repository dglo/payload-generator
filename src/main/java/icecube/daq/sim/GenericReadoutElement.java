/*
 * class: GenericReadoutElement
 *
 * Version $Id: GenericReadoutElement.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 7 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This class is a containor for generic readout element information
 *
 * @version $Id: GenericReadoutElement.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericReadoutElement
        extends Generic
{

    private int readoutType;
    private long firstTime;
    private long lastTime;
    private int sourceId;
    private int domId;

    public GenericReadoutElement() {

    }

    public long getTimeStamp() {
        return this.getFirstTime();
    }

    public void setTimeStamp(long timeStamp) {
        this.setFirstTime(timeStamp);
    }

    public int getReadoutType() {
        return readoutType;
    }

    public void setReadoutType(int readoutType) {
        this.readoutType = readoutType;
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

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getDomId() {
        return domId;
    }

    public void setDomId(int domId) {
        this.domId = domId;
    }


}
