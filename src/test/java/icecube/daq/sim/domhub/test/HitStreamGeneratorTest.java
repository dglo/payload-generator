/*
 * class: TCalGeneratorTest
 *
 * Version $Id: HitStreamGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: August 13 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub.test;

import icecube.daq.sim.domhub.HitStreamGenerator;
import icecube.daq.sim.domhub.IGenericRecord;
import icecube.daq.sim.test.LoggingCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * This class defines the tests that any GenericHitRecord object should pass.
 *
 * @author pat
 * @version $Id: HitStreamGeneratorTest.java 2125 2007-10-12 18:27:05Z ksb $
 */
public class HitStreamGeneratorTest
        extends LoggingCase
{

    /**
     * The object being tested.
     */
    private HitStreamGenerator testObject;

    /**
     * Constructs and instance of this test.
     *
     * @param name the name of the test.
     */
    public HitStreamGeneratorTest(String name)
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
        testObject = new HitStreamGenerator(1.0, 1.0);
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
    public void testBeacon() {
        IGenericRecord hit = testObject.nextRecord();
    }

    /**
     * Create test suite for this class.
     *
     * @return the suite of tests declared in this class.
     */
    public static Test suite()
    {
        return new TestSuite(HitStreamGeneratorTest.class);
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
