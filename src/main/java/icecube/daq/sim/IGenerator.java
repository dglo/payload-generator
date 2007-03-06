/*
 * interface: IGenerator
 *
 * Version $Id: IGenerator.java,v 1.4 2005/07/26 23:00:11 toale Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import java.nio.ByteBuffer;

/**
 * This interface defines a hit generator
 *
 * @version $Id: IGenerator.java,v 1.4 2005/07/26 23:00:11 toale Exp $
 * @author pat
 */
public interface IGenerator
{

    ByteBuffer generatePayload(long timeStamp, long domId, int sourceId, int triggerMode);

    ByteBuffer generatePayload(GenericHit hit);

    ByteBuffer generatePayload(GenericF2kHit hit);

    int getSize();

}
