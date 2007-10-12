/*
 * Version $Id: GenericTriggerRequest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * Container for generic trigger request information.
 */
public class GenericTriggerRequest
    extends GenericTrigger
{
    private int readoutReqType;

    public GenericTriggerRequest()
    {
        super();
    }

    public void addReadoutRequestElement(int type, int srcId, long domId,
                                         long firstTime, long lastTime)
    {
        GenericReadoutElement rdOut = new GenericReadoutElement();
        rdOut.setReadoutType(type);
        rdOut.setSourceId(srcId);
        rdOut.setDomId((int) domId);
        rdOut.setFirstTime(firstTime);
        rdOut.setLastTime(lastTime);

        addReadoutElement(rdOut);
    }

    public int getReadoutRequestType()
    {
        return readoutReqType;
    }

    public void setReadoutRequestType(int type)
    {
        readoutReqType = type;
    }
}
