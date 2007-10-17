/*
 * interface: IGenerator
 *
 * Version $Id: IGenerator.java 2125 2007-10-12 18:27:05Z ksb $
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
 * @version $Id: IGenerator.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public interface IGenerator
{

    ByteBuffer generatePayload(long timeStamp, long domId, int sourceId, int triggerMode);

    ByteBuffer generatePayload(GenericHit hit);

    ByteBuffer generatePayload(GenericF2kHit hit);

    int getSize();

}
