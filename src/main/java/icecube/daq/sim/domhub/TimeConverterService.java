package icecube.daq.sim.domhub;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: pat
 * Date: Oct 26, 2006
 * Time: 11:53:04 AM
 */
public final class TimeConverterService
{

    private static TimeConverterService service;

    private static Map domMap;

    private static long utcOffset;

    private TimeConverterService() {
        domMap = new Hashtable();

        // create calendar with current time
        Calendar now = new GregorianCalendar();
        // create calendar for Jan 1 00:00:00 of current year
        int year = now.get(Calendar.YEAR);
        Calendar then = new GregorianCalendar(year, Calendar.JANUARY, 1, 0, 0, 0);
        // get utc time (in milliseconds) on Jan 1 of current year
        utcOffset = then.getTimeInMillis();
    }

    public static TimeConverterService getInstance() {
        if (null == service) service = new TimeConverterService();
        return service;
    }

    public static void registerDom(String domId, TimeConverter timeConverter) {
        domMap.put(domId, timeConverter);
    }

    private static TimeConverter getTimeConverter(String domId) {
        if (domMap.containsKey(domId)) {
            return (TimeConverter) domMap.get(domId);
        } else {
            return null;
        }
    }

    public static long getDorClockFromUtcTime(long utcTime, String domId) {
        TimeConverter timeConverter = getTimeConverter(domId);
        if (null == timeConverter) return -1;
        return timeConverter.getDorClockFromUtcTime(utcTime);
    }

    public static long getDorRxFromDorTx(long dorTx, String domId) {
        TimeConverter timeConverter = getTimeConverter(domId);
        if (null == timeConverter) return -1;
        return timeConverter.getDomRxFromDorTx(dorTx);
    }

    public static long getDomRxFromDorTx(long dorTx, String domId) {
        TimeConverter timeConverter = getTimeConverter(domId);
        if (null == timeConverter) return -1;
        return timeConverter.getDomRxFromDorTx(dorTx);
    }

    public static long getDomTxFromDomRx(long domRx, String domId) {
        TimeConverter timeConverter = getTimeConverter(domId);
        if (null == timeConverter) return -1;
        return timeConverter.getDomTxFromDomRx(domRx);
    }

    public static long getUtcTime() {
        return 10000000*(System.currentTimeMillis() - utcOffset);
    }

}
