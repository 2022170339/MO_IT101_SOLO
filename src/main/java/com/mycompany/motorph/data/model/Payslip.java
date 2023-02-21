package com.mycompany.motorph.data.model;

public class Payslip {
    // Employee #,PayBeginDate,PayEndDate,PayDate,RegularHours,OvertimeHours,RegularHoursPay,OvertimeHoursPay,GrossPay,NetPay,SSS,Philhealth,Pagibig,Tax,Other
    int id;
    String payBeginDate;
    String payEndDate;
    String payDate;
    double regularHours;
    double overtimehours;
    double regularHoursPay;
    double overtimeHoursPay;
    double grossPay;
    double netPay;
    double sss;
    double philhealth;
    double pagibig;
    double tax;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPayBeginDate() {
        return payBeginDate;
    }
    public void setPayBeginDate(String payBeginDate) {
        this.payBeginDate = payBeginDate;
    }
    public String getPayEndDate() {
        return payEndDate;
    }
    public void setPayEndDate(String payEndDate) {
        this.payEndDate = payEndDate;
    }
    public String getPayDate() {
        return payDate;
    }
    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }
    public double getRegularHours() {
        return regularHours;
    }
    public void setRegularHours(double regularHours) {
        this.regularHours = regularHours;
    }
    public double getOvertimehours() {
        return overtimehours;
    }
    public void setOvertimehours(double overtimehours) {
        this.overtimehours = overtimehours;
    }
    public double getRegularHoursPay() {
        return regularHoursPay;
    }
    public void setRegularHoursPay(double regularHoursPay) {
        this.regularHoursPay = regularHoursPay;
    }
    public double getOvertimeHoursPay() {
        return overtimeHoursPay;
    }
    public void setOvertimeHoursPay(double overtimeHoursPay) {
        this.overtimeHoursPay = overtimeHoursPay;
    }
    public double getGrossPay() {
        return grossPay;
    }
    public void setGrossPay(double grossPay) {
        this.grossPay = grossPay;
    }
    public double getNetPay() {
        return netPay;
    }
    public void setNetPay(double netPay) {
        this.netPay = netPay;
    }
    public double getSss() {
        return sss;
    }
    public void setSss(double sss) {
        this.sss = sss;
    }
    public double getPhilhealth() {
        return philhealth;
    }
    public void setPhilhealth(double philhealth) {
        this.philhealth = philhealth;
    }
    public double getPagibig() {
        return pagibig;
    }
    public void setPagibig(double pagibig) {
        this.pagibig = pagibig;
    }
    public double getTax() {
        return tax;
    }
    public void setTax(double tax) {
        this.tax = tax;
    }
}
