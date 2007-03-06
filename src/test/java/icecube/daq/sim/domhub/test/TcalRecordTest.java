/*
 * class: TCalGeneratorTest
 *
 * Version $Id: TcalRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 *
 * Date: August 13 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.TcalRecord;
import icecube.daq.sim.domhub.IGenericRecord;
import icecube.daq.sim.domhub.GenericTcalRecord;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This class defines the tests that any TCalGenerator object should pass.
 *
 * @author pat
 * @version $Id: TcalRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 */
public class TcalRecordTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private TcalRecord testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TcalRecordTest(String name)
    {
        super(name);
    }

    /**
     * Sets up the fixture, for example, open a network connection. This method
     * is called before a test is executed.
     *
     * @throws Exception if super class setUp fails.
     */
    protected void setUp()
            throws Exception
    {
        super.setUp();
        testObject = new TcalRecord(8500590);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     *
     * @throws Exception if super class tearDown fails.
     */
    protected void tearDown()
            throws Exception
    {
        testObject = null;
        super.tearDown();
    }

    /**
     * Explanation of test.
     */
    public void testGenerator() {
        IGenericRecord record = new GenericTcalRecord(85015730000000000L);
        ByteBuffer buffer = testObject.generateRecord(record);
        dumpBuffer(buffer);
    }

    private void dumpBuffer(ByteBuffer buffer)
    {
        int length = buffer.getInt();
        int format = buffer.getInt();
        long domId = buffer.getLong();

        short len = buffer.getShort();
        byte stat = buffer.get();
        byte blank = buffer.get();

        long dorTx = buffer.getLong();
        long dorRx = buffer.getLong();
        short dorWf[] = new short[64];
        for (int i=0; i<64; i++) {
            dorWf[i] = buffer.getShort();
        }

        long domRx = buffer.getLong();
        long domTx = buffer.getLong();
        short domWf[] = new short[64];
        for (int i=0; i<64; i++) {
            domWf[i] = buffer.getShort();
        }

        Charset charset = Charset.forName("US-ASCII");
        CharBuffer charBuffer = charset.decode(buffer);

        String gpsString;
        byte bs[];

        if (charBuffer.length() < 15) {
            gpsString = null;
            bs = new byte[0];
        } else {
            gpsString = charBuffer.subSequence(0, 14).toString();
            bs = gpsString.getBytes();
        }

        int pos = buffer.position();
        buffer.position(pos - 8);
        long dorCnt = buffer.getLong();


        System.out.println("\nDump of wrapped tcal record with GPS");

        System.out.println("\nWrapper header:");
        System.out.println("    Length = " + length);
        System.out.println("    Format = " + format);
        System.out.println("    DomId  = " + domId);

        System.out.println("\nRecord header:");
        System.out.println("    Length = " + len);
        System.out.println("    Format = " + stat);
        System.out.println("    Blank  = " + blank);

        System.out.println("\nTCal record:");
        System.out.println("    DOR Tx = " + dorTx);
        System.out.println("    DOR Rx = " + dorRx);
        System.out.print("    DOR Wf = ");
        for (int i=0; i<64; i++) {
            System.out.print(dorWf[i] + " ");
        }
        System.out.println("\n    DOM Rx = " + domRx);
        System.out.println("    DOM Tx = " + domTx);
        System.out.print("    DOM Wf = ");
        for (int i=0; i<64; i++) {
            System.out.print(domWf[i] + " ");
        }


        System.out.println("\n\nGPS record:");
        System.out.println("    GPS string = " + gpsString);
        System.out.print("               = 0x");
        for (int i=0; i<bs.length; i++) {
            System.out.print(Integer.toHexString(bs[i]));
        }
        System.out.println("\n    DOR count  = " + dorCnt);

    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TcalRecordTest.class);
    }

    /**
     * Main routine which runs text test in standalone mode.
     *
     * @param args the arguments with which to execute this method.
     */
    public static void main(String[] args)
    {
        TestRunner.run(suite());
    }

}
