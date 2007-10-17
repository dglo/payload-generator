/*
 * class: DomConfiguration
 *
 * Version $Id: DomConfiguration.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: May 28 2006
 *
 * (c) 2006 IceCube Collaboration
 */

package icecube.daq.sim.domhub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class holds dom simulation configuration data.
 *
 * @version $Id: DomConfiguration.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public class DomConfiguration
{

    private static final Log log = LogFactory.getLog(DomConfiguration.class);

    private long domId;

    private int startUtcTime;

    private double beaconRate;

    private double speRate;

    private double tcalRate;

    private double moniRate;

    private double supernovaRate;

    private long seed;

    public DomConfiguration() {

    }
    
    public long getDomId() {
        return domId;
    }

    public void setDomId(long domId) {
        this.domId = domId;
    }

    public int getStartUtcTime() {
        return startUtcTime;
    }

    public void setStartUtcTime(int startUtcTime) {
        this.startUtcTime = startUtcTime;
    }

    public double getBeaconRate() {
        return beaconRate;
    }

    public void setBeaconRate(double beaconRate) {
        this.beaconRate = beaconRate;
    }

    public double getSpeRate() {
        return speRate;
    }

    public void setSpeRate(double speRate) {
        this.speRate = speRate;
    }

    public double getTcalRate() {
        return tcalRate;
    }

    public void setTcalRate(double tcalRate) {
        this.tcalRate = tcalRate;
    }

    public double getMoniRate() {
        return moniRate;
    }

    public void setMoniRate(double moniRate) {
        this.moniRate = moniRate;
    }

    public double getSupernovaRate() {
        return supernovaRate;
    }

    public void setSupernovaRate(double supernovaRate) {
        this.supernovaRate = supernovaRate;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public String toString(){
        String domConfigInfo = "domID = " + domId +
                "\n" + "startUtcTime = " + startUtcTime +
                "\n" + "beaconRate = " + beaconRate +
                "\n" + "speRate = " + speRate +
                "\n" + "tcalRate = " + tcalRate +
                "\n" + "moniRate = " + moniRate +
                "\n" + "supernovaRate = " + supernovaRate +
                "\n" + "seed = " + seed;

        return domConfigInfo;
    }
}
