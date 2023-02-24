package com.mycompany.motorph.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.data.model.Payslip;
import com.mycompany.motorph.helpers.TextFileParser;

public class PayslipDatabase {

    public static List<Payslip> findAllById(int accountId) {
        List<Payslip> allPayslips = findAll();
        List<Payslip> myPayslips = new ArrayList<Payslip>();
        for (Payslip payslip : allPayslips) {
            if (payslip.getId() == accountId)
                myPayslips.add(payslip);
        }
        return myPayslips;
    }

    // Employee
    // #,PayBeginDate,PayEndDate,PayDate,RegularHours,OvertimeHours,RegularHoursPay,OvertimeHoursPay,GrossPay,NetPay,SSS,Philhealth,Pagibig,Tax
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
            file.println(
                    "Employee #,PayBeginDate,PayEndDate,PayDate,RegularHours,OvertimeHours,RegularHoursPay,OvertimeHoursPay,GrossPay,NetPay,SSS,Philhealth,Pagibig,Tax");
            for (Payslip payslip : payslips) {
                file.println(String.join(",", payslip.getId() + "", payslip.getPayBeginDate(), payslip.getPayEndDate(),
                        payslip.getPayDate(), payslip.getRegularHours() + "", payslip.getOvertimehours() + "",
                        payslip.getRegularHoursPay() + "", payslip.getOvertimeHoursPay() + "",
                        payslip.getGrossPay() + "", payslip.getNetPay() + "", payslip.getSss() + "",
                        payslip.getPhilhealth() + "", payslip.getPagibig() + "", payslip.getTax() + ""));
            }
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
        }
    }

    public static List<String> getWeekList() {
        YearMonth yearMonth = YearMonth.of(LocalDate.now().getYear(), LocalDate.now().getMonth());
        int daysInMonth = yearMonth.lengthOfMonth();
        List<String> dates = new ArrayList<String>();
        for (int i = 1; i < daysInMonth; i++) {
            LocalDate day = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), i);
            DayOfWeek d = day.getDayOfWeek();
            DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            if (d.toString() == "MONDAY" && day.isBefore(LocalDate.now())) {
                dates.add((format.format(day) + " - " + format.format(day.plusDays(4))));
            }
        }
        return dates;
    }

    public static String getTodayDate() {
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return f2.format(LocalDateTime.now());
    }

    public static List<EmployeeDetail> findAllPendingEmployeeByWeek(String start, String end) {
        List<EmployeeDetail> filtered = new ArrayList<EmployeeDetail>();
        List<EmployeeDetail> employees = EmployeeDatabase.findAll();
        for (EmployeeDetail employeeDetail : employees) {
            if (checkIfHasPending(start, end, employeeDetail.getId())) {
                filtered.add(employeeDetail);
            }
        }
        return filtered;
    }

    public static boolean checkIfHasPending(String start, String end, int accountId) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate d1 = LocalDate.parse(start, f);
        LocalDate d2 = LocalDate.parse(end, f);
        List<Attendance> attendances = AttendanceDatabase.findAllByIdAndDate(accountId, d1, d2);
        List<Payslip> payslips = PayslipDatabase.findAllById(accountId);
        boolean batt = false;
        boolean bps = false;
        for (Attendance attendance : attendances) {
            LocalDate aDT = LocalDate.parse(AttendanceDatabase.getDateClean(attendance.getDate()), f);
            if (AttendanceDatabase.isWithinRange(aDT, d1, d2))
                batt = true;
        }

        for (Payslip payslip : payslips) {
            LocalDate pbd = LocalDate.parse(payslip.getPayBeginDate(), f);
            LocalDate ped = LocalDate.parse(payslip.getPayEndDate(), f);

            if (AttendanceDatabase.isWithinRange(pbd, d1, d2) || AttendanceDatabase.isWithinRange(ped, d1, d2))
                bps = true;
        }
        return !((batt && bps) || (!batt && !bps));
    }

    public static double getRegularHours(String start, String end, int accountId) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

        LocalDate d1 = LocalDate.parse(start, f);
        LocalDate d2 = LocalDate.parse(end, f);
        List<Attendance> attendances = AttendanceDatabase.findAllByIdAndDate(accountId, d1, d2);
        double totalHours = 0.0;
        for (Attendance attendance : attendances) {
            if (attendance.getTimeOut().isEmpty())
                continue;

            String startT = AttendanceDatabase.getDateClean(attendance.getDate()) + " "
                    + AttendanceDatabase.getTimeClean(attendance.getTimeIn());
            String endT = AttendanceDatabase.getDateClean(attendance.getDate()) + " "
                    + AttendanceDatabase.getTimeClean(attendance.getTimeOut());
            String lateT = AttendanceDatabase.getDateClean(attendance.getDate()) + " 08:10";
            String notLate = AttendanceDatabase.getDateClean(attendance.getDate()) + " 08:00";
            String overT = AttendanceDatabase.getDateClean(attendance.getDate()) + " 17:00";

            LocalDateTime startDT = LocalDateTime.parse(startT, f2);
            LocalDateTime endDT = LocalDateTime.parse(endT, f2);
            LocalDateTime lateDT = LocalDateTime.parse(lateT, f2);
            LocalDateTime overDT = LocalDateTime.parse(overT, f2);
            LocalDateTime notDT = LocalDateTime.parse(notLate, f2);

            boolean late = false;
            boolean ot = false;
            if (startDT.isAfter(lateDT))
                late = true;

            if (endDT.isAfter(overDT))
                ot = true;

            Duration dur = null;
            if (late) {
                if (ot) {
                    dur = Duration.between(overDT, startDT);
                } else {
                    dur = Duration.between(endDT, startDT);
                }
            } else {
                if (endDT.isBefore(notDT)) {
                    dur = Duration.between(endDT, startDT);
                } else {
                    if (ot) {
                        dur = Duration.between(overDT, notDT);
                    } else {
                        dur = Duration.between(endDT, notDT);
                    }
                }
            }
            totalHours = totalHours + Math.abs(dur.toSeconds()) / 3600;
        }
        return totalHours;
    }

    public static double getOvertimeHours(String start, String end, int accountId) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

        LocalDate d1 = LocalDate.parse(start, f);
        LocalDate d2 = LocalDate.parse(end, f);
        List<Attendance> attendances = AttendanceDatabase.findAllByIdAndDate(accountId, d1, d2);
        double totalHours = 0;
        for (Attendance attendance : attendances) {
            if (attendance.getTimeOut().isEmpty())
                continue;

            String startT = AttendanceDatabase.getDateClean(attendance.getDate()) + " " + "17:00";
            String endT = AttendanceDatabase.getDateClean(attendance.getDate()) + " "
                    + AttendanceDatabase.getTimeClean(attendance.getTimeOut());

            LocalDateTime startDT = LocalDateTime.parse(startT, f2);
            LocalDateTime endDT = LocalDateTime.parse(endT, f2);
            boolean ot = false;

            if (endDT.isAfter(startDT)) {
                ot = true;
            }

            if (ot) {
                long minutes = ChronoUnit.MINUTES.between(startDT, endDT);
                totalHours += minutes / 60;
            } else {
                long minutes = 0;
                totalHours += minutes;
            }
        }
        return totalHours <= 0 ? 0 : totalHours;
    }

    public static List<String> findByStartDate(int accountId, String start) {
        return null;
    }
}
