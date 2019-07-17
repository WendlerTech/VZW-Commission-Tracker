package tech.wendler.commission_tracker;

import java.io.Serializable;

public class TransactionInfo implements Serializable {

    private String customerName, phoneNumber, orderNumber;
    private int salesForceLeads;
    private boolean repAssistedOrder, dFillOrder, inStorePickupOrder, preOrder;
    private double extraSalesDollars;

    public TransactionInfo() {

    }

    public TransactionInfo(String custName, String phone, String orderNum, int salesForce,
                           double extraSalesDollars, boolean repAssist, boolean dFill,
                           boolean ispu, boolean preOrder) {
        this.customerName = custName;
        this.phoneNumber = phone;
        this.orderNumber = orderNum;
        this.salesForceLeads = salesForce;
        this.extraSalesDollars = extraSalesDollars;
        this.repAssistedOrder = repAssist;
        this.dFillOrder = dFill;
        this.inStorePickupOrder = ispu;
        this.preOrder = preOrder;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getSalesForceLeads() {
        return salesForceLeads;
    }

    public void setSalesForceLeads(int salesForceLeads) {
        this.salesForceLeads = salesForceLeads;
    }

    public double getExtraSalesDollars() {
        return extraSalesDollars;
    }

    public void setExtraSalesDollars(double extraSalesDollars) {
        this.extraSalesDollars = extraSalesDollars;
    }

    public boolean isRepAssistedOrder() {
        return repAssistedOrder;
    }

    public void setRepAssistedOrder(boolean repAssistedOrder) {
        this.repAssistedOrder = repAssistedOrder;
    }

    public boolean isdFillOrder() {
        return dFillOrder;
    }

    public void setdFillOrder(boolean dFillOrder) {
        this.dFillOrder = dFillOrder;
    }

    public boolean isInStorePickupOrder() {
        return inStorePickupOrder;
    }

    public void setInStorePickupOrder(boolean inStorePickupOrder) {
        this.inStorePickupOrder = inStorePickupOrder;
    }

    public boolean isPreOrder() {
        return preOrder;
    }

    public void setPreOrder(boolean preOrder) {
        this.preOrder = preOrder;
    }
}
