package com.mycompany.motorph.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.mycompany.motorph.data.model.Payslip;
import com.mycompany.motorph.helpers.TextFileParser;

public class PayslipDatabase {

    public static List<Payslip> findAllById(int accountId) { 
        List<Payslip> allPayslips = findAll();
        List<Payslip> myPayslips = new ArrayList<Payslip>();
        for (Payslip payslip : allPayslips) {
            if(payslip.getId() == accountId) myPayslips.add(payslip); 
        }
        return myPayslips;
    }
    
    // Employee #,PayBeginDate,PayEndDate,PayDate,RegularHours,OvertimeHours,RegularHoursPay,OvertimeHoursPay,GrossPay,NetPay,SSS,Philhealth,Pagibig,Tax
    public static List<Payslip> findAll() {
        try {
            FileReader file = new FileReader("src/main/resources/payslips.txt");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            scanner.nextLine();
            List<Payslip> payslips = new ArrayList<Payslip>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                ArrayList<String> payslipArr = TextFileParser.ParseRow(line);
                Payslip payslip = new Payslip();
                payslip.setId(Integer.parseInt(payslipArr.get(0)));
                payslip.setPayBeginDate(payslipArr.get(1));
                payslip.setPayEndDate(payslipArr.get(2));
                payslip.setPayDate(payslipArr.get(3));
                payslip.setRegularHours(Double.parseDouble(payslipArr.get(4)));
                payslip.setOvertimehours(Double.parseDouble(payslipArr.get(5)));
                payslip.setRegularHoursPay(Double.parseDouble(payslipArr.get(6)));
                payslip.setOvertimeHoursPay(Double.parseDouble(payslipArr.get(7)));
                payslip.setGrossPay(Double.parseDouble(payslipArr.get(8)));
                payslip.setNetPay(Double.parseDouble(payslipArr.get(9)));
                payslip.setSss(Double.parseDouble(payslipArr.get(10)));
                payslip.setPhilhealth(Double.parseDouble(payslipArr.get(11)));
                payslip.setPagibig(Double.parseDouble(payslipArr.get(12)));
                payslip.setTax(Double.parseDouble(payslipArr.get(13)));
                payslips.add(payslip);
            }
            scanner.close();
            return payslips;
        } catch (FileNotFoundException e) {
            return new ArrayList<Payslip>();
        } catch (NoSuchElementException e) {
            return new ArrayList<Payslip>();
        }
    }

    public static void save(List<Payslip> payslips) {
        try {
            PrintWriter file = new PrintWriter("src/main/resources/payslips.txt");
            file.println("Employee #,PayBeginDate,PayEndDate,PayDate,RegularHours,OvertimeHours,RegularHoursPay,OvertimeHoursPay,GrossPay,NetPay,SSS,Philhealth,Pagibig,Tax");
            for (Payslip payslip : payslips) {
                file.println(String.join(",", payslip.getId() + "", payslip.getPayBeginDate(), payslip.getPayEndDate(), payslip.getPayDate(), payslip.getRegularHours() + "", payslip.getOvertimehours() + "", payslip.getRegularHoursPay() + "", payslip.getOvertimeHoursPay() + "", payslip.getGrossPay() + "", payslip.getNetPay() + "", payslip.getSss() + "", payslip.getPhilhealth() + "", payslip.getPagibig() + "", payslip.getTax() + ""));    
            }
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
        }  
    }
}

