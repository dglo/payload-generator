/*
 * class: EngFmtGenerator
 *
 * Version $Id: DomHubManifest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: June 10 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

import icecube.daq.payload.IDOMID;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Iterator;

/**
 * This class produces a ByteBuffer representation of a DomHub manifest record.
 *
 * @version $Id: DomHubManifest.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class DomHubManifest
{

    /**
     * Size (in bytes) of fields in record
     */
    private static final int RECORD_LENGTH_SIZE = 4;
    private static final int RECORD_FORMAT_SIZE = 4;
    private static final int NUM_DOMID_SIZE     = 2;
    private static final int DOMID_SIZE         = 8;

    private static final int RECORD_HEADER_SIZE = RECORD_LENGTH_SIZE
                                                  + RECORD_FORMAT_SIZE
                                                  + NUM_DOMID_SIZE;

    /**
     * offset into buffer for above fields
     */
    private static final int RECORD_LENGTH_OFFSET = 0;
    private static final int RECORD_FORMAT_OFFSET = RECORD_LENGTH_OFFSET + RECORD_LENGTH_SIZE;
    private static final int NUM_DOMID_OFFSET     = RECORD_FORMAT_OFFSET + RECORD_FORMAT_SIZE;
    private static final int DOMID_OFFSET         = NUM_DOMID_OFFSET + NUM_DOMID_SIZE;

    private DomHubManifest() {

    }

    public static int getSize(int nDoms) {
        return (RECORD_HEADER_SIZE + nDoms*DOMID_SIZE);
    }

    /**
     * create a manifest
     * @param format record format
     * @param domIds List of DOM ids (IDOMIDs)
     * @return ByteBuffer representation of manifest
     */
    public static ByteBuffer createHeader(int format, List domIds) {
        Integer nDoms = new Integer(domIds.size());
        int size = getSize(nDoms.intValue());

        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        // header
        byteBuffer.putInt(RECORD_LENGTH_OFFSET, size);
        byteBuffer.putInt(RECORD_FORMAT_OFFSET, format);
        byteBuffer.putShort(NUM_DOMID_OFFSET, nDoms.shortValue());

        // loop over domIds
        int offset = DOMID_OFFSET;
        Iterator iter = domIds.iterator();
        while (iter.hasNext()) {
            IDOMID domId = (IDOMID) iter.next();
            byteBuffer.putLong(offset, domId.getDomIDAsLong());
            offset += DOMID_SIZE;
        }

        // flip buffer before returning it
        byteBuffer.flip();
        return byteBuffer;
    }

}
