/*
 * class: PositionRegistry
 *
 * Version $Id: PositionRegistry.java 2125 2007-10-12 18:27:05Z ksb $
 *
 * Date: February 1 2005
 *
 * (c) 2005 IceCube Collaboration
 */

package icecube.daq.sim;

/**
 * This class provides static methods for accessing dom identities
 *
 * @version $Id: PositionRegistry.java 2125 2007-10-12 18:27:05Z ksb $
 * @author pat
 */
public final class PositionRegistry
{

    /**
     * total number of deployed doms, inice + icetop
     */
    public static final int NUMBER_OF_DOMS = 60 + 16;

    /**
     * array of deployed doms
     */
    private static DomInfo[] doms = {
        new DomInfo(21,  1, "UP4P0264", "Bat",                 "737d355af587"),
        new DomInfo(21,  2, "TP4P0101", "Conchiglie",          "5B9AE7909352"),
        new DomInfo(21,  3, "UP4P0192", "Summer_ale",          "6e6733a279a7"),
        new DomInfo(21,  4, "TP4P0303", "Topi",                "66504b293808"),
        new DomInfo(21,  5, "UP4P0170", "Dubbel",              "abcfd5e5a352"),
        new DomInfo(21,  6, "TP4P0291", "Squirrel",            "fe92d7ff4480"),
        new DomInfo(21,  7, "UP4P0166", "Trappist",            "32bb0201e5a7"),
        new DomInfo(21,  8, "TP4P0271", "Rhinoceros",          "fb0944d283fd"),
        new DomInfo(21,  9, "UP4H0019", "Erik_the_Red",        "38ae7fdfc4c7"),
        new DomInfo(21, 10, "TP4H0024", "Ragnar_Lodbrok",      "20f6cf1215f8"),
        new DomInfo(21, 11, "UP4H0017", "Erik_Segersaell",     "72fecfd94382"),
        new DomInfo(21, 12, "TP4H0021", "Sverker",             "40429f5180f6"),
        new DomInfo(21, 13, "UP4Y0014", "Wickueler",           "85d69873ad04"),
        new DomInfo(21, 14, "TP4Y0059", "Reissdorf_Koelsch",   "ffb9b1b82c88"),
        new DomInfo(21, 15, "UP4Y0056", "Pilsener_Urquell",    "de18daf832b4"),
        new DomInfo(21, 16, "TP4Y0053", "Radeberger_Pilsener", "b804f6f38a45"),
        new DomInfo(21, 17, "UP4Y0026", "Wittinger",           "e53c98680186"),
        new DomInfo(21, 18, "TP4Y0027", "Veltins",             "858a79abc807"),
        new DomInfo(21, 19, "UP4Y0016", "Rhenenia_Alt",        "427ea29c4bb5"),
        new DomInfo(21, 20, "TP4Y0013", "Schneider_Weisse",    "4255e2b1c79f"),
        new DomInfo(21, 21, "UP4P0252", "Antelope",            "16a5b9112b00"),
        new DomInfo(21, 22, "TP4P0325", "Wombat",              "525d954116a7"),
        new DomInfo(21, 23, "UP4P0346", "Lemur",               "916ceadb214d"),
        new DomInfo(21, 24, "TP4P0255", "Porcupine",           "9513f8a612b5"),
        new DomInfo(21, 25, "UP4P0320", "Gnu",                 "f004760c91e6"),
        new DomInfo(21, 26, "TP4P0225", "Mouse",               "d638529ba5ad"),
        new DomInfo(21, 27, "UP4P0302", "Dolphin",             "4424ac8d389a"),
        new DomInfo(21, 28, "TP4P0221", "Monkey",              "8b9f35308e27"),
        new DomInfo(21, 29, "UP4P0254", "Syrah",               "acf3852b67e7"),
        new DomInfo(21, 30, "TP4P0157", "Phenol",              "57bb7c43b042"),
        new DomInfo(21, 31, "UP4P0236", "Dimethylsulfoxide",   "8d0872b30a8a"),
        new DomInfo(21, 32, "TP4P0153", "Cholesterol",         "6c34a4a77c08"),
        new DomInfo(21, 33, "UP4P0230", "Oatmeal_stout",       "50cb49c42356"),
        new DomInfo(21, 34, "TP4P0151", "Lima_bean",           "0df7b060acad"),
        new DomInfo(21, 35, "UP4P0220", "Dimethylamine",       "9a1136171c74"),
        new DomInfo(21, 36, "TP4P0149", "Glycerol",            "a48aaec81ca3"),
        new DomInfo(21, 37, "UP4P0196", "Anthracene",          "fbfd0026868a"),
        new DomInfo(21, 38, "TP4P0189", "Indole",              "c3780e484438"),
        new DomInfo(21, 39, "UP4P0186", "Parsley",             "77d222d84888"),
        new DomInfo(21, 40, "TP4P0185", "Diethyl_ether",       "634e0cd60304"),
        new DomInfo(21, 41, "UP4P0168", "Cayenne_pepper",      "0b3d4beac530"),
        new DomInfo(21, 42, "TP4P0181", "Porter",              "6bda3a3ae651"),
        new DomInfo(21, 43, "UP4P0162", "Chardonnay",          "f23c1d938a5b"),
        new DomInfo(21, 44, "TP4P0145", "Napthalene",          "82b54295e9cb"),
        new DomInfo(21, 45, "UP4P0206", "Hamachi",             "9EAD08E7E9EE"),
        new DomInfo(21, 46, "TP4P0203", "Tetrahydrofuran",     "d337025ec466"),
        new DomInfo(21, 47, "UP4P0204", "Avocado",             "5E17341BB4EB"),
        new DomInfo(21, 48, "TP4P0169", "Riesling",            "a727fddbb7f4"),
        new DomInfo(21, 49, "UP4P0156", "Suzuki",              "B467B12814CB"),
        new DomInfo(21, 50, "TP4P0081", "Loquat",              "7609BEAE9CA6"),
        new DomInfo(21, 51, "UP4P0148", "Argon",               "789CA09A087E"),
        new DomInfo(21, 52, "AP4P0078", "Nitrogen",            "ACD733B2B248"),
        new DomInfo(21, 53, "UP4P0214", "Sulfur",              "423ED83846C3"),
        new DomInfo(21, 54, "TP4P0127", "Fluorine",            "6F242F105485"),
        new DomInfo(21, 55, "UP4P0158", "Vanadium",            "F85C7530C973"),
        new DomInfo(21, 56, "TP4P0121", "Oxygen",              "455C5F0F8A3F"),
        new DomInfo(21, 57, "UP4P0138", "Beryllium",           "A2A379FAA0BA"),
        new DomInfo(21, 58, "TP4P0117", "Sodium",              "EF3E4BA9CBA4"),
        new DomInfo(21, 59, "UP4P0108", "Ebi",                 "755FC7596D46"),
        new DomInfo(21, 60, "TP4P0103", "Bucatini",            "FE6689460566"),
        new DomInfo(21, 61, "AP4P0076", "Ziti",                "86355672DCEE"),
        new DomInfo(21, 62, "AP4P0061", "Uni",                 "C80B79E1A8B7"),
        new DomInfo(21, 63, "TP4P0179", "Brown_ale",           "1ebec6395f96"),
        new DomInfo(21, 64, "TP4P0141", "Fig",                 "62645A002733"),
        new DomInfo(29, 61, "TP4P0161", "Cerrano_pepper",      "75f1a642df54"),
        new DomInfo(29, 62, "AP4P0077", "Helium",              "173CE1549A47"),
        new DomInfo(29, 63, "AP4P0065", "Tako",                "69A38B5DC5F8"),
        new DomInfo(29, 64, "TP4P0129", "Manicotti",           "308D3F7692FD"),
        new DomInfo(30, 61, "AP4P0063", "Akagai",              "22C1D090B1EB"),
        new DomInfo(30, 62, "AP4P0070", "Lithium",             "06F482883442"),
        new DomInfo(30, 63, "AP4P0066", "Carbon",              "EEB299B6639D"),
        new DomInfo(30, 64, "TP4P0199", "Black_bean",          "d52b66ab6861"),
        new DomInfo(39, 61, "AP4P0073", "Hydrogen",            "9ED5742A784D"),
        new DomInfo(39, 62, "AP4P0075", "Unagi",               "0B5DC6A92170"),
        new DomInfo(39, 63, "AP4P0069", "Tsubugai",            "D4B05C67CE19"),
        new DomInfo(39, 64, "AP4P0068", "Tofe",                "2C5BF45CA479")
    };

    /**
     * get string number given domId
     * @param domID string representation of mainboard id
     * @return string number
     */
    public static int getString(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                return doms[i].getString();
            }
        }
        return -1;
    }

    /**
     * get position on string given domId
     * position 1 is top of string, 60 is bottom
     * @param domID string representation of mainboard id
     * @return position on string
     */
    public static int getPosition(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                return doms[i].getPosition();
            }
        }
        return -1;
    }

    /**
     * get DOM index in range 1-76: inice is 1-60, icetop is 61-76 (ordered by string)
     * this is very dependent on year 2005 geometry
     * @param domID string representation of mainboard id
     * @return index of DOM
     */
    public static int getDomIndex(String domID) {
        int string = getString(domID);
        int position = getPosition(domID);
        if (position < 0) {
            // error, return status
            return position;
        } else {
            if (string == 21) {
                return position;
            } else if (string == 29) {
                return position + 4;
            } else if (string == 30) {
                return position + 8;
            } else if (string == 39) {
                return position + 12;
            } else {
                // error, return status
                return string;
            }
        }
    }

    /**
     * get dom at a specific location
     * @param string string number
     * @param position position on string
     * @return mainboard id as string
     */
    public static String getDOMatPosition(int string, int position) {
        for (int i=0; i<doms.length; i++) {
            if ((string == doms[i].getString()) && (position == doms[i].getPosition())) {
                return doms[i].getMainBoardID().toLowerCase();
            }
        }
        return null;
    }

    /**
     * test if dom is inice
     * @param domID string representation of mainboard id
     * @return true if dom is inice
     */
    public static boolean isInice(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                if (doms[i].getPosition() <= 60) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * get station number given icetop domId
     * @param domID string representation of mainboard id
     * @return station number
     */
    public static int getStation(String domID) {
        return getString(domID);
    }

    /**
     * get tank of icetop dom
     * there are 2 tanks per station
     * @param domID string representation of mainboard id
     * @return tank number
     */
    public static int getTank(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                if ((doms[i].getPosition() == 61) || (doms[i].getPosition() == 62)) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return -1;
    }

    /**
     * test if dom is icetop
     * @param domID string representation of mainboard id
     * @return true if dom is icetop
     */
    public static boolean isIcetop(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                if (doms[i].getPosition() > 60) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getName(String domID) {
        for (int i=0; i<doms.length; i++) {
            if (domID.compareToIgnoreCase(doms[i].getMainBoardID()) == 0) {
                return doms[i].getName();
            }
        }
        return null;
    }

    /**
     * helper class to contain dom-wise information
     */
    private static class DomInfo {

        private int string;
        private int position;
        private String productionID;
        private String name;
        private String mainBoardID;

        private DomInfo(int str, int pos, String prodID, String n, String mbID) {
            string = str;
            position = pos;
            productionID = prodID;
            name = n;
            mainBoardID = mbID;
        }

        private int getString() {
            return string;
        }

        private int getPosition() {
            return position;
        }

        private String getProductionID() {
            return productionID;
        }

        private String getName() {
            return name;
        }

        private String getMainBoardID() {
            return mainBoardID;
        }

    }

}
