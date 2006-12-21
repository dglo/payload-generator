/*
 * interface: IGeneric
 *
 * Version $Id: IGeneric.java,v 1.1 2005/06/08 19:53:53 toale Exp $
 *
 * Date: June 8 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This interface ...does what?
 *
 * @version $Id: IGeneric.java,v 1.1 2005/06/08 19:53:53 toale Exp $
 * @author pat
 */
public interface IGeneric
        extends Comparable
{

    long getTimeStamp();

    void setTimeStamp(long timeStamp);

}
