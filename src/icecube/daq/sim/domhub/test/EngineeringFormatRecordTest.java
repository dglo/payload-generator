/*
 * class: TCalGeneratorTest
 *
 * Version $Id: EngineeringFormatRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 *
 * Date: August 13 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.TcalRecord;
import icecube.daq.sim.domhub.IGenericRecord;
import icecube.daq.sim.domhub.GenericTcalRecord;
import icecube.daq.sim.domhub.EngineeringFormatRecord;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This class defines the tests that any EngineeringFormatRecord object should pass.
 *
 * @author pat
 * @version $Id: EngineeringFormatRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 */
public class EngineeringFormatRecordTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private EngineeringFormatRecord testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public EngineeringFormatRecordTest(String name)
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
        testObject = new EngineeringFormatRecord();
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
     * Test conversion between time stamp as long and time stamp as 6B array.
     */
    public void testTimeStamp() {
        long longTime = 17592186044416L;
        System.out.println(longTime);
        byte[] byteTime = EngineeringFormatRecord.timeStampToBytes(longTime);
        System.out.println(EngineeringFormatRecord.toHexString(byteTime));
        long longTime2 = EngineeringFormatRecord.timeStampToLong(byteTime);
        System.out.println(longTime2);
        assertEquals(longTime, longTime2);

        System.out.println("-------------------------");

        longTime = 0;
        System.out.println(longTime);
        byteTime = EngineeringFormatRecord.timeStampToBytes(longTime);
        System.out.println(EngineeringFormatRecord.toHexString(byteTime));
        longTime2 = EngineeringFormatRecord.timeStampToLong(byteTime);
        System.out.println(longTime2);
        assertEquals(longTime, longTime2);

        System.out.println("-------------------------");

        longTime = 281474976710655L;
        System.out.println(longTime);
        byteTime = EngineeringFormatRecord.timeStampToBytes(longTime);
        System.out.println(EngineeringFormatRecord.toHexString(byteTime));
        longTime2 = EngineeringFormatRecord.timeStampToLong(byteTime);
        System.out.println(longTime2);
        assertEquals(longTime, longTime2);

        System.out.println("-------------------------");

        longTime = 1;
        System.out.println(longTime);
        byteTime = EngineeringFormatRecord.timeStampToBytes(longTime);
        System.out.println(EngineeringFormatRecord.toHexString(byteTime));
        longTime2 = EngineeringFormatRecord.timeStampToLong(byteTime);
        System.out.println(longTime2);
        assertEquals(longTime, longTime2);

    }


    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(EngineeringFormatRecordTest.class);
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
