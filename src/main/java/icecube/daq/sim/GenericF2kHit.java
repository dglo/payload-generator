/*
 * class: GenericF2kHit
 *
 * Version $Id: GenericF2kHit.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This class is a container for generic f2k hit information
 *
 * @version $Id: GenericF2kHit.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class GenericF2kHit
        extends GenericHit
{

    private int  stringId;
    private int  lcTag;

    public GenericF2kHit() {
        this(-1, -1, -1, -1, -1, -1);
    }

    public GenericF2kHit(long timeStamp, long domId, int stringId, int sourceId, int triggerMode, int lcTag) {
        super(timeStamp, domId, sourceId, triggerMode);
        this.stringId    = stringId;
        this.lcTag       = lcTag;
    }

    public int getStringId() {
        return stringId;
    }

    public void setStringId(int stringId) {
        this.stringId = stringId;
    }

    public int getLcTag() {
        return lcTag;
    }

    public void setLcTag(int lcTag) {
        this.lcTag = lcTag;
    }

}
