/*
 * class: TCalGeneratorTest
 *
 * Version $Id: GenericTcalRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 *
 * Date: August 13 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.GenericTcalRecord;
import icecube.daq.sim.test.LoggingCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericTcalRecord object should pass.
 *
 * @author pat
 * @version $Id: GenericTcalRecordTest.java,v 1.1 2006/05/30 14:31:34 toale Exp $
 */
public class GenericTcalRecordTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private GenericTcalRecord testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public GenericTcalRecordTest(String name)
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
        testObject = new GenericTcalRecord(777);
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
    public void testConstructor() {
        assertEquals(testObject.getUtcTime(), 777);
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(GenericTcalRecordTest.class);
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
