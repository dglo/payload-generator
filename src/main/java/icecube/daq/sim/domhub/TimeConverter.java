/*
 * class: TimeConverter
 *
 * Version $Id: TimeConverter.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: August 16 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class handles time conversion between IceCube UTC time, DOR clock time,
 * and DOM clock time. It is initialized with a DOR reset time and the DOM-DOR
 * offset time.
 *
 * @version $Id: TimeConverter.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class TimeConverter
{

    /**
     * Log object for this class
     */
    private static final Log log = LogFactory.getLog(TimeConverter.class);

    /**
     * Number of ns per DOR clock tick (20 MHz).
     */
    private static final double NS_PER_DOR_COUNT = 50;

    /**
     * Number of ns per DOM clock tick (40 MHz).
     */
    private static final double NS_PER_DOM_COUNT = 25;

    /**
     * Maximum count on the 56 bit DOR counter.
     */
    private static final long MAX_DOR_COUNT = (long) (Math.pow(2, 56) - 1);

    /**
     * Maximum count on the 48 bit DOM counter.
     */
    private static final long MAX_DOM_COUNT = (long) (Math.pow(2, 48) - 1);

    /**
     * Maximum UTC time (number of 1/10 ns in a non-leap year).
     */
    private static final long MAX_UTC_TIME = 365*24*60*60*10000000000L;

    /**
     * Default value for DOR Tx for computing conversion data.
     */
    private static final long DEFAULT_DOR_TX = 19640336501L;

    /**
     * Default value for DOR Rx for computing conversion data.
     */
    private static final long DEFAULT_DOR_RX = 19640337530L;

    /**
     * Default value for DOM Rx for computing conversion data.
     */
    private static final long DEFAULT_DOM_RX = 14589485741L;

    /**
     * Default value for DOM Tx for computing conversion data.
     */
    private static final long DEFAULT_DOM_TX = 14589486351L;

    /**
     * Time difference between DOR send and DOM recieve, in ns.
     */
    private double tripTime;

    /**
     * Offset between zeroes of DOR and DOM clocks, in ns.
     */
    private double offset;

    /**
     * Time difference between DOR send and DOR recieve, in ns.
     */
    private double deltaDor;

    /**
     * Time difference between DOM recieve and DOM send, in ns.
     */
    private double deltaDom;

    /**
     * GPS time (since Jan 1 of current year, in seconds) when DOR clock was zeroed
     */
    private long dorResetInGpsTime;

    /**
     * Constructor.
     * @param dorResetInGpsTime GPS time when DOR clock was zeroed
     */
    public TimeConverter(long dorResetInGpsTime) {
        this(DEFAULT_DOR_TX, DEFAULT_DOR_RX, DEFAULT_DOM_RX, DEFAULT_DOM_TX, dorResetInGpsTime);
    }

    /**
     * Constructor.
     * @param tripTime time difference between DOR send and DOM recieve, in ns
     * @param offset offset between zeroes of DOR and DOM clocks, in ns
     * @param dorResetInGpsTime GPS time when DOR clock was zeroed
     */
    public TimeConverter(double tripTime, double offset, long dorResetInGpsTime) {
        this.tripTime = tripTime;
        this.offset = offset;
        this.dorResetInGpsTime = dorResetInGpsTime;

        deltaDor = (DEFAULT_DOR_RX - DEFAULT_DOR_TX)*NS_PER_DOR_COUNT;
        deltaDom = (DEFAULT_DOM_TX - DEFAULT_DOM_RX)*NS_PER_DOM_COUNT;
    }

    /**
     * Constructor.
     * @param dorTx DOR send clock count
     * @param dorRx DOR recieve clock count
     * @param domRx DOM recieve clock count
     * @param domTx DOM send clock count
     * @param dorResetInGpsTime GPS time when DOR clock was zeroed
     */
    public TimeConverter(long dorTx, long dorRx, long domRx, long domTx, long dorResetInGpsTime) {
        this.dorResetInGpsTime = dorResetInGpsTime;

        deltaDor = (dorRx - dorTx)*NS_PER_DOR_COUNT;
        deltaDom = (domTx - domRx)*NS_PER_DOM_COUNT;
        tripTime = (deltaDor - deltaDom)/2;
        offset = dorTx*NS_PER_DOR_COUNT - domRx*NS_PER_DOM_COUNT + tripTime;
    }

    /**
     * Convert from UTC time (in 1/10 ns units) to DOR clock counts.
     * @param utcTime UTC time in 1/10 ns
     * @return DOR clock count
     */
    public long getDorClockFromUtcTime(long utcTime) {
        // check if utcTime makes sense
        if (utcTime > MAX_UTC_TIME) {
            log.warn("UTC Time " + utcTime + " is larger than max UTC time " + MAX_UTC_TIME);
            utcTime %= MAX_UTC_TIME;
        }

        long dorClock = (long) ((utcTime/10 - dorResetInGpsTime*1e9)/NS_PER_DOR_COUNT);

        // take into account the rollover of the counter
        long dorClockModMax = Math.abs(dorClock) % MAX_DOR_COUNT;
        if (dorClock < 0) {
            // subtract off negative count
            return (MAX_DOR_COUNT + 1 - dorClockModMax);
        } else {
            return dorClockModMax;
        }
    }

    /**
     * Convert from DOR clock counts to UTC time (in 1/10 ns units).
     * @param dorClock DOR clock count
     * @return UTC time in 1/10 ns
     */
    public long getUtcTimeFromDorClock(long dorClock) {
        // check if dorClock makes sense
        if (dorClock > MAX_DOR_COUNT) {
            log.warn("DOR count " + dorClock + " is larger than max DOR count " + MAX_DOR_COUNT);
            dorClock %= MAX_DOR_COUNT;
        }
        long utcTime = (long) (10*(dorClock*NS_PER_DOR_COUNT + dorResetInGpsTime*1e9));
        // do rollover
        if (utcTime > MAX_UTC_TIME) {
            utcTime %= MAX_UTC_TIME;
        }
        return utcTime;
    }

    /**
     * Convert from DOR clock counts to DOM clock counts.
     * NOTE: This DOES NOT take into account the trip time of the tcal signal.
     * @param dorClock DOR clock count
     * @return DOM clock count
     */
    public long getDomClockFromDorClock(long dorClock) {
        // check if dorClock makes sense
        if (dorClock > MAX_DOR_COUNT) {
            log.warn("DOR count " + dorClock + " is larger than max DOR count " + MAX_DOR_COUNT);
            dorClock %= MAX_DOR_COUNT;
        }

        long domClock = (long) ((dorClock*NS_PER_DOR_COUNT - offset)/NS_PER_DOM_COUNT);

        // take into account the rollover of the counter
        long domClockModMax = Math.abs(domClock) % MAX_DOM_COUNT;
        if (domClock < 0) {
            // subtract off negative count
            return (MAX_DOM_COUNT + 1 - domClockModMax);
        } else {
            return domClockModMax;
        }
    }

    /**
     * Convert from DOM clock counts to DOR clock counts.
     * NOTE: This DOES NOT take into account the trip time of the tcal signal.
     * @param domClock DOM clock count
     * @return DOR clock count
     */
    public long getDorClockFromDomClock(long domClock) {
        // check if domClock makes sense
        if (domClock > MAX_DOM_COUNT) {
            log.warn("DOM count " + domClock + " is larger than max DOM count " + MAX_DOM_COUNT);
            domClock %= MAX_DOM_COUNT;
        }

        long dorClock = (long) ((domClock*NS_PER_DOM_COUNT + offset)/NS_PER_DOR_COUNT);

        // take into account the rollover of the counter
        long dorClockModMax = Math.abs(dorClock) % MAX_DOR_COUNT;
        if (dorClock < 0) {
            // subtract off negative count
            return (MAX_DOR_COUNT + 1 - dorClockModMax);
        } else {
            return dorClockModMax;
        }
    }

    /**
     * Convert between UTC time (in 1/10 ns units) to DOM clock counts.
     * @param utcTime UTC time in 1/10 ns
     * @return DOM clock count
     */
    public long getDomClockFromUtcTime(long utcTime) {
        long dorClock = getDorClockFromUtcTime(utcTime);
        return getDomClockFromDorClock(dorClock);
    }

    /**
     * Convert between DOM clock counts to UTC time (in 1/10 ns units).
     * @param domClock DOM clock count
     * @return UTC time in 1/10 ns
     */
    public long getUtcTimeFromDomClock(long domClock) {
        long dorClock = getDorClockFromDomClock(domClock);
        return getUtcTimeFromDorClock(dorClock);
    }

    /**
     * Get DOR Rx from DOR Tx.
     * @param dorTx clock count of DOR send
     * @return clock count of DOR recieve
     */
    public long getDorRxFromDorTx(long dorTx) {
        long dorRx = dorTx + (long) (deltaDor/NS_PER_DOR_COUNT);
        // take into account the rollover of the counter
        if (dorRx > MAX_DOR_COUNT) {
            dorRx %= MAX_DOR_COUNT;
        }
        return dorRx;
    }

    /**
     * Get DOM Rx from DOR Tx.
     * @param dorTx clock count of DOR send
     * @return clock count of DOM recieve
     */
    public long getDomRxFromDorTx(long dorTx) {
        // first convert to DOM time
        long dorTxInDomCnts = getDomClockFromDorClock(dorTx);
        // then add the tripTime
        long domRx = dorTxInDomCnts + (long) (tripTime/NS_PER_DOM_COUNT);
        // take into account the rollover of the counter
        if (domRx > MAX_DOM_COUNT) {
            domRx %= MAX_DOM_COUNT;
        }
        return domRx;
    }

    /**
     * Get DOM Tx from DOM Rx.
     * @param domRx clock count of DOM recieve
     * @return clock count of DOM send
     */
    public long getDomTxFromDomRx(long domRx) {
        long domTx = domRx + (long) (deltaDom/NS_PER_DOM_COUNT);
        // take into account the rollover of the counter
        if (domTx > MAX_DOM_COUNT) {
            domTx %= MAX_DOM_COUNT;
        }
        return domTx;
    }

    /**
     * Get DOM Tx from DOR Tx.
     * @param dorTx clock count of DOR send
     * @return clock count of DOM send
     */
    public long getDomTxFromDorTx(long dorTx) {
        long domRx = getDomRxFromDorTx(dorTx);
        return getDomTxFromDomRx(domRx);
    }

    public double getTripTime() {
        return tripTime;
    }

    public void setTripTime(double tripTime) {
        this.tripTime = tripTime;
    }

    public double getOffset() {
        return offset;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public long getDorResetInGpsTime() {
        return dorResetInGpsTime;
    }

    public void setDorResetInGpsTime(long dorResetInGpsTime) {
        this.dorResetInGpsTime = dorResetInGpsTime;
    }

    public double getDeltaDor() {
        return deltaDor;
    }

    public void setDeltaDor(double deltaDor) {
        this.deltaDor = deltaDor;
    }

    public double getDeltaDom() {
        return deltaDom;
    }

    public void setDeltaDom(double deltaDom) {
        this.deltaDom = deltaDom;
    }

}
