/*
 * interface: ISource
 *
 * Version $Id: ISource.java,v 1.3 2005/11/07 18:05:07 dglo Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This interface defines a source of GenericHits
 *
 * @version $Id: ISource.java,v 1.3 2005/11/07 18:05:07 dglo Exp $
 * @author pat
 */
public interface ISource
{

    Generic nextPayload();
    Generic nextPayload(long timeStamp);

}
