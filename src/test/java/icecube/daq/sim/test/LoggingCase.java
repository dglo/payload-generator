package icecube.daq.sim.test;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;

public class LoggingCase
    extends TestCase
{
    private MockAppender appender = new MockAppender();

    /**
     * Constructs an instance of this test.
     *
     * @param name the name of the test.
     */
    public LoggingCase(String name)
    {
        super(name);
    }

    public void clearMessages()
    {
        appender.clear();
    }

    public Object getMessage(int idx)
    {
        return appender.getMessage(idx);
    }

    public int getNumberOfMessages()
    {
        return appender.getNumberOfMessages();
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();

        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure(appender);
    }

    protected void tearDown()
        throws Exception
    {
        assertEquals("Bad number of log messages",
                     0, appender.getNumberOfMessages());

        super.tearDown();
    }
}
