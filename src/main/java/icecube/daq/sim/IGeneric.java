/*
 * interface: IGeneric
 *
 * Version $Id: IGeneric.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 8 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This interface ...does what?
 *
 * @version $Id: IGeneric.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface IGeneric
        extends Comparable
{

    long getTimeStamp();

    void setTimeStamp(long timeStamp);

}
