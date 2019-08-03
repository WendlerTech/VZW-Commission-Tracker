package tech.wendler.commission_tracker;

public class SalesDollarValues {

    private final static double REVENUE_ASSUMED_VALUE = .35;
    private final static int CONNECTED_ASSUMED_VALUE = 50;
    private final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    private final static int HUM_ASSUMED_VALUE = 55;
    private final static int MULTI_TMP_ASSUMED_VALUE = 210;
    private final static int TABLET_ASSUMED_VALUE = 200;

    public static void SalesDollarValues() {

    }

    public static double getRevenueAssumedValue() {
        return REVENUE_ASSUMED_VALUE;
    }

    public static int getConnectedAssumedValue() {
        return CONNECTED_ASSUMED_VALUE;
    }

    public static int getSingleTmpAssumedValue() {
        return SINGLE_TMP_ASSUMED_VALUE;
    }

    public static int getHumAssumedValue() {
        return HUM_ASSUMED_VALUE;
    }

    public static int getMultiTmpAssumedValue() {
        return MULTI_TMP_ASSUMED_VALUE;
    }

    public static int getTabletAssumedValue() {
        return TABLET_ASSUMED_VALUE;
    }
}
