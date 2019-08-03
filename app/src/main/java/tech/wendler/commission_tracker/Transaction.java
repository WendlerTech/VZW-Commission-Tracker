package tech.wendler.commission_tracker;

import java.io.Serializable;

public class Transaction implements Serializable {

    private final static double REVENUE_ASSUMED_VALUE = SalesDollarValues.getRevenueAssumedValue();
    private final static int CONNECTED_ASSUMED_VALUE = SalesDollarValues.getConnectedAssumedValue();
    private final static int SINGLE_TMP_ASSUMED_VALUE = SalesDollarValues.getSingleTmpAssumedValue();
    private final static int HUM_ASSUMED_VALUE = SalesDollarValues.getHumAssumedValue();
    private final static int MULTI_TMP_ASSUMED_VALUE = SalesDollarValues.getMultiTmpAssumedValue();
    private final static int TABLET_ASSUMED_VALUE = SalesDollarValues.getTabletAssumedValue();

    private int totalNewPhones, totalUpgPhones, totalTablets, totalConnected,
            totalHum, totalTMP;
    private int transactionID;
    private double totalRev, totalSalesDollars;
    private boolean newMultiTMP;
    private String transactionDate;

    Transaction() {

    }

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
        return totalSalesDollars;
    }

    public void setTotalSalesDollars(double totalSalesDollars) {
        this.totalSalesDollars = totalSalesDollars;
    }

    double calculateTotalSalesDollars() {
        double calculatedSalesDollars = (totalTablets * TABLET_ASSUMED_VALUE) +
                (totalConnected * CONNECTED_ASSUMED_VALUE) + (totalHum * HUM_ASSUMED_VALUE) +
                (totalTMP * SINGLE_TMP_ASSUMED_VALUE) + (totalRev * REVENUE_ASSUMED_VALUE);
        if (newMultiTMP) {
            calculatedSalesDollars += MULTI_TMP_ASSUMED_VALUE;
        }

        return calculatedSalesDollars;
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

    public void setTotalNewPhones(int totalNewPhones) {
        this.totalNewPhones = totalNewPhones;
    }

    public void setTotalUpgPhones(int totalUpgPhones) {
        this.totalUpgPhones = totalUpgPhones;
    }

    public void setTotalTablets(int totalTablets) {
        this.totalTablets = totalTablets;
    }

    public void setTotalConnected(int totalConnected) {
        this.totalConnected = totalConnected;
    }

    public void setTotalHum(int totalHum) {
        this.totalHum = totalHum;
    }

    public void setTotalTMP(int totalTMP) {
        this.totalTMP = totalTMP;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public void setTotalRev(double totalRev) {
        this.totalRev = totalRev;
    }

    public void setNewMultiTMP(boolean newMultiTMP) {
        this.newMultiTMP = newMultiTMP;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
