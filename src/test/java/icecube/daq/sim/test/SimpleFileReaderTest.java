/*
 * class: SimpleFileReaderTest
 *
 * Version $Id: SimpleFileReaderTest.java,v 1.3 2005/06/08 19:53:53 toale Exp $
 *
 * Date: June 3 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.SimpleFileReader;
import icecube.daq.sim.GenericHit;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any SimpleFileReader object should pass.
 *
 * @author pat
 * @version $Id: SimpleFileReaderTest.java,v 1.3 2005/06/08 19:53:53 toale Exp $
 */
public class SimpleFileReaderTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private SimpleFileReader testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public SimpleFileReaderTest(String name)
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
        java.io.InputStream stream =
            getClass().getResourceAsStream("/test.txt");
        testObject = new SimpleFileReader(stream);
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
     * Test on test.txt
     */
    public void testFile()
    {
        GenericHit hit1 = (GenericHit) testObject.nextPayload();
        GenericHit hit2 = (GenericHit) testObject.nextPayload();
        GenericHit hit3 = (GenericHit) testObject.nextPayload();
        GenericHit hit4 = (GenericHit) testObject.nextPayload();
        GenericHit hit5 = (GenericHit) testObject.nextPayload();

        long time1 = hit1.getTimeStamp();
        long time2 = hit2.getTimeStamp();
        long time3 = hit3.getTimeStamp();
        long time4 = hit4.getTimeStamp();

        assertTrue((time1 == 1) && (time2 == 2) && (time3 == 3) && (time4 == 4) && (hit5 == null));
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(SimpleFileReaderTest.class);
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
