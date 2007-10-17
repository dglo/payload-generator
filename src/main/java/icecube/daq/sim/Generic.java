/*
 * class: Generic
 *
 * Version $Id: Generic.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 8 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This class ...does what?
 *
 * @version $Id: Generic.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public abstract class Generic
        implements IGeneric
{

    /**
     * define ordering of objects
     * @param o object to compare to
     * @return standard compareTo convention
     */
    public int compareTo(Object o) {

        if (this.getTimeStamp() < ((Generic) o).getTimeStamp()) {
            return -1;
        } else if (this.getTimeStamp() > ((Generic) o).getTimeStamp()) {
            return +1;
        } else {
            return 0;
        }

    }

}
