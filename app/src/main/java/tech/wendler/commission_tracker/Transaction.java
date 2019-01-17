package tech.wendler.commission_tracker;

import java.io.Serializable;

public class Transaction implements Serializable {

    private final static double REVENUE_ASSUMED_VALUE = .35;
    private final static int CONNECTED_ASSUMED_VALUE = 50;
    private final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    private final static int HUM_ASSUMED_VALUE = 50;
    private final static int MULTI_TMP_ASSUMED_VALUE = 200;
    private final static int TABLET_ASSUMED_VALUE = 200;

    private int totalNewPhones, totalUpgPhones, totalTablets, totalConnected,
            totalHum, totalTMP;
    private int transactionID;
    private double totalRev;
    private boolean newMultiTMP;

    Transaction(int transactionID, int newPhones, int upgPhones, int tablets, int CD, int hum, int TMP,
                double revenue, boolean newMultiTMP) {
        this.transactionID = transactionID;
        this.totalNewPhones = newPhones;
        this.totalUpgPhones = upgPhones;
        this.totalTablets = tablets;
        this.totalConnected = CD;
        this.totalHum = hum;
        this.totalTMP = TMP;
        this.totalRev = revenue;
        this.newMultiTMP = newMultiTMP;
    }

    double getTotalSalesDollars() {
        double totalSalesDollars = (totalTablets * TABLET_ASSUMED_VALUE) +
                (totalConnected * CONNECTED_ASSUMED_VALUE) + (totalHum * HUM_ASSUMED_VALUE) +
                (totalTMP * SINGLE_TMP_ASSUMED_VALUE) + (totalRev * REVENUE_ASSUMED_VALUE);
        if (newMultiTMP) {
            totalSalesDollars += MULTI_TMP_ASSUMED_VALUE;
        }

        return totalSalesDollars;
    }

    int getTotalDevices() {
        return totalNewPhones + totalUpgPhones + totalConnected + totalHum + totalTablets;
    }

    int getTransactionID() {
        return transactionID;
    }

    int getTotalNewPhones() {
        return totalNewPhones;
    }

    int getTotalUpgPhones() {
        return totalUpgPhones;
    }

    int getTotalTablets() {
        return totalTablets;
    }

    int getTotalConnected() {
        return totalConnected;
    }

    int getTotalHum() {
        return totalHum;
    }

    int getTotalTMP() {
        return totalTMP;
    }

    double getTotalRev() {
        return totalRev;
    }

    boolean isNewMultiTMP() {
        return newMultiTMP;
    }
}
