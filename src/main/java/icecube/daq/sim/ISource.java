/*
 * interface: ISource
 *
 * Version $Id: ISource.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This interface defines a source of GenericHits
 *
 * @version $Id: ISource.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface ISource
{

    Generic nextPayload();
    Generic nextPayload(long timeStamp);

}
