/*
 * class: GenericHit
 *
 * Version $Id: GenericHit.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This class is a container for generic hit information
 *
 * @version $Id: GenericHit.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericHit
        extends Generic
{

    private long timeStamp;
    private long domId;
    private int  sourceId;
    private int  triggerMode;

    public GenericHit() {
        this(-1, -1, -1, -1);
    }

    public GenericHit(long timeStamp, long domId, int sourceId, int triggerMode) {
        this.timeStamp   = timeStamp;
        this.domId       = domId;
        this.sourceId    = sourceId;
        this.triggerMode = triggerMode;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getDomId() {
        return domId;
    }

    public void setDomId(long domId) {
        this.domId = domId;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(int triggerMode) {
        this.triggerMode = triggerMode;
    }

    public String toString() {
        return "GenericHit[" + timeStamp + " DOM#" + domId +
            " Src#" + sourceId + " TrigMode#" + triggerMode + "]";
    }

}
