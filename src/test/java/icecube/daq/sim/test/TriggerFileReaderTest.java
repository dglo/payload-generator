/*
 * class: TriggerFileReaderTest
 *
 * Version $Id: TriggerFileReaderTest.java,v 1.2 2005/06/09 21:16:04 toale Exp $
 *
 * Date: June 7 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.test;

import icecube.daq.sim.TriggerFileReader;
import icecube.daq.sim.GenericTrigger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any TriggerFileReader object should pass.
 *
 * @author pat
 * @version $Id: TriggerFileReaderTest.java,v 1.2 2005/06/09 21:16:04 toale Exp $
 */
public class TriggerFileReaderTest
        extends TestCase
{

    /**
     * The object being tested.
     */
    private TriggerFileReader testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public TriggerFileReaderTest(String name)
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
            getClass().getResourceAsStream("/trigger-file.txt");
        testObject = new TriggerFileReader(stream);
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
     * Test reading of file
     */
    public void testFile()
    {
        GenericTrigger trigger1 = (GenericTrigger) testObject.nextPayload();
        GenericTrigger trigger2 = (GenericTrigger) testObject.nextPayload();
        GenericTrigger trigger3 = (GenericTrigger) testObject.nextPayload();
        GenericTrigger trigger4 = (GenericTrigger) testObject.nextPayload();
        GenericTrigger trigger5 = (GenericTrigger) testObject.nextPayload();

        long time1 = trigger1.getFirstTime();
        long time2 = trigger2.getFirstTime();
        long time3 = trigger3.getFirstTime();
        long time4 = trigger4.getFirstTime();

        assertTrue((time1 == 10) && (time2 == 30) && (time3 == 35) && (time4 == 50) && (trigger5 == null));        
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(TriggerFileReaderTest.class);
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
