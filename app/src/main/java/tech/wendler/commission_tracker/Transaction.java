package tech.wendler.commission_tracker;

import java.io.Serializable;

public class Transaction implements Serializable {

    private final static double REVENUE_ASSUMED_VALUE = .35;
    private final static int CONNECTED_ASSUMED_VALUE = 50;
    private final static int SINGLE_TMP_ASSUMED_VALUE = 70;
    private final static int HUM_ASSUMED_VALUE = 100;
    private final static int MULTI_TMP_ASSUMED_VALUE = 200;
    private final static int TABLET_ASSUMED_VALUE = 200;

    private int totalNewPhones, totalUpgPhones, totalTablets, totalConnected,
            totalHum, totalTMP;
    private int transactionID;
    private double totalSalesDollars, totalRev;
    private boolean newMultiTMP;

    public Transaction(int transactionID, int newPhones, int upgPhones, int tablets, int CD, int hum, int TMP,
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

    public double getTotalSalesDollars() {
        totalSalesDollars = (totalTablets * TABLET_ASSUMED_VALUE) +
                (totalConnected * CONNECTED_ASSUMED_VALUE) + (totalHum * HUM_ASSUMED_VALUE) +
                (totalTMP * SINGLE_TMP_ASSUMED_VALUE) + (totalRev * REVENUE_ASSUMED_VALUE);
        if (newMultiTMP) {
            totalSalesDollars += MULTI_TMP_ASSUMED_VALUE;
        }

        return totalSalesDollars;
    }

    public int getTotalPhones() {
        return totalNewPhones + totalUpgPhones;
    }

    public int getTotalDevices() {
        return totalNewPhones + totalUpgPhones + totalConnected + totalHum + totalTablets;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getTotalNewPhones() {
        return totalNewPhones;
    }

    public void setTotalNewPhones(int totalNewPhones) {
        this.totalNewPhones = totalNewPhones;
    }

    public int getTotalUpgPhones() {
        return totalUpgPhones;
    }

    public void setTotalUpgPhones(int totalUpgPhones) {
        this.totalUpgPhones = totalUpgPhones;
    }

    public int getTotalTablets() {
        return totalTablets;
    }

    public void setTotalTablets(int totalTablets) {
        this.totalTablets = totalTablets;
    }

    public int getTotalConnected() {
        return totalConnected;
    }

    public void setTotalConnected(int totalConnected) {
        this.totalConnected = totalConnected;
    }

    public int getTotalHum() {
        return totalHum;
    }

    public void setTotalHum(int totalHum) {
        this.totalHum = totalHum;
    }

    public int getTotalTMP() {
        return totalTMP;
    }

    public void setTotalTMP(int totalTMP) {
        this.totalTMP = totalTMP;
    }

    public void setTotalSalesDollars(double totalSalesDollars) {
        this.totalSalesDollars = totalSalesDollars;
    }

    public double getTotalRev() {
        return totalRev;
    }

    public void setTotalRev(double totalRev) {
        this.totalRev = totalRev;
    }

    public boolean isNewMultiTMP() {
        return newMultiTMP;
    }

    public void setNewMultiTMP(boolean newMultiTMP) {
        this.newMultiTMP = newMultiTMP;
    }
}
