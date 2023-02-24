/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.data;

import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.data.model.Timesheet;
import com.mycompany.motorph.helpers.TextFileParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 *
 * @author reden
 */
public class AttendanceDatabase {

    public static void insertTimeIn(int accountId) {
        EmployeeDetail employee = EmployeeDatabase.findOne(accountId);
        Attendance attendance = checkAttendance(accountId);
        if (attendance == null) {
            attendance = new Attendance();
            attendance.setId(accountId);
            attendance.setFirstName(employee.getFirstName());
            attendance.setLastName(employee.getLastName());
            attendance.setDate(getTodayDate());
            attendance.setTimeIn(getTime());
            attendance.setTimeOut("");
            List<Attendance> attendances = findAll();
            attendances.add(attendance);
            save(attendances);
        }
    }

    public static List<Attendance> findAllById(int accountId) {
        List<Attendance> attendances = findAll();
        List<Attendance> filtered = new ArrayList<Attendance>();
        for (Attendance attendance : attendances) {
            if (attendance.getId() == accountId)
                filtered.add(attendance);
        }

        return filtered;
    }

    public static void updateTimeOut(int accountId) {
        Attendance attendance = checkAttendance(accountId);
        if (attendance != null) {
            attendance.setTimeOut(getTime());
            List<Attendance> attendances = findAll();
            for (int i = 0; i < attendances.size(); i++) {
                if (attendances.get(i).getId() == attendance.getId()
                        && attendances.get(i).getDate().equals(attendance.getDate())) {
                    attendances.set(i, attendance);
                    break;
                }
            }
            save(attendances);
        }
    }

    public static List<Attendance> findAll() {
        try {
            FileReader file = new FileReader("src/main/resources/attendance.txt");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            scanner.nextLine();
            List<Attendance> attendances = new ArrayList<Attendance>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                ArrayList<String> attArr = TextFileParser.ParseRow(line);
                Attendance attendance = new Attendance();
                attendance.setId(Integer.parseInt(attArr.get(0)));
                attendance.setLastName(attArr.get(1));
                attendance.setFirstName(attArr.get(2));
                attendance.setDate(attArr.get(3));
                attendance.setTimeIn(attArr.get(4));
                attendance.setTimeOut(attArr.get(5));
                attendances.add(attendance);
            }
            scanner.close();
            return attendances;
        } catch (FileNotFoundException e) {
            return new ArrayList<Attendance>();
        } catch (NoSuchElementException e) {
            return new ArrayList<Attendance>();
        }
    }

    public Attendance[] findAllAttendanceByDate(Date date) {
        return null;
    }

    public static Attendance findOneById(int accountId, String strDate) {
        List<Attendance> attendances = findAll();
        for (Attendance attendance : attendances) {
            if (attendance.getDate().equals(strDate)) {
                return attendance;
            }
        }
        return null;
    }

    public static Attendance checkAttendance(int accountId) {
        List<Attendance> attendances = findAll();
        for (Attendance attendance : attendances) {
            if (attendance.getId() == accountId && attendance.getDate().equals(getTodayDate())) {
                return attendance;
            }
        }
        return null;
    }

    public static String getTodayDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String strDate = formatter.format(date);
        return strDate;
    }

    public static String getTime() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String strTime = formatter.format(date);
        return strTime;
    }

    public static void save(List<Attendance> attendances) {
        try {
            PrintWriter file = new PrintWriter("src/main/resources/attendance.txt");
            file.println("Employee #,Last Name,First Name,Date,Time-in,Time-out");
            for (Attendance attendance : attendances) {
                file.println(
                        String.join(",", attendance.getId() + "", attendance.getFirstName(), attendance.getLastName(),
                                attendance.getDate(), attendance.getTimeIn(), attendance.getTimeOut()));
            }
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
        }
    }

    public static boolean isWeekend(final LocalDate ld) {
        DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }

    public static List<Attendance> findAllByIdAndDate(int accountId, LocalDate d1, LocalDate d2) {
        List<Attendance> attendances = findAll();
        List<Attendance> filtered = new ArrayList<Attendance>();
        DateTimeFormatter f = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        for (Attendance attendance : attendances) {
            if (attendance.getId() == accountId) {
                LocalDate date = LocalDate.parse(getDateClean(attendance.getDate()), f);
                if (isWithinRange(date, d1.minusDays(1), d2.plusDays(1))) {
                    filtered.add(attendance);
                }
            }
        }
        return filtered;
    }

    public static String getDateClean(String date) {
        String[] d = date.split("/");
        if (d[0].length() == 1)
            d[0] = "0" + d[0];
        if (d[1].length() == 1)
            d[1] = "0" + d[1];
        if (d[2].length() == 2)
            d[2] = "20" + d[2];

        return String.join("/", d[0], d[1], d[2]);
    }

    public static String getTimeClean(String time) {
        if (time.isEmpty())
            return "";

        String[] t1 = time.split(":");
        if (t1[0].length() == 1)
            t1[0] = "0" + t1[0];
        if (t1[1].length() == 1)
            t1[1] = "0" + t1[1];
        return String.join(":", t1[0], t1[1]);
    }

    public static LocalDate getDateFromString(String date, String format) {
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(date, pattern);
    }

    public static boolean isWithinRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return !(date.isBefore(startDate) || date.isAfter(endDate));
    }

    public static List<Timesheet> getSortedTimesheet(int accountId) {
        DateTimeFormatter pattern1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        DateTimeFormatter pattern2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        List<Timesheet> timesheets = new ArrayList<Timesheet>();
        List<Attendance> attendances = findAllById(accountId);
        for (Attendance attendance : attendances) {
            Timesheet t = new Timesheet();
            t.setDate(LocalDate.parse(getDateClean(attendance.getDate()), pattern2).format(pattern1));
            t.setTimeIn(formatTime(getTimeClean(attendance.getTimeIn())));
            t.setTimeOut(formatTime(getTimeClean(attendance.getTimeOut())));
            timesheets.add(t);
        }

        timesheets.sort((a, b) -> getDateFromString(a.getDate(), "MMMM dd, yyyy")
                .compareTo(getDateFromString(b.getDate(), "MMMM dd, yyyy")));
        return timesheets;
    }

    private static String formatTime(String timeClean) {
        if (timeClean.isEmpty())
            return "";

        String[] s = timeClean.split(":");
        String a = "";
        String s1 = "";
        String s2 = "";

        int ss1 = Integer.parseInt(s[0]);
        int ss2 = Integer.parseInt(s[1]);
        if (ss1 >= 12) {
            a = "PM";
        } else {
            a = "AM";
        }

        s1 = ((ss1 % 12) > 0 ? (ss1 % 12) : 12) + "";
        s2 = (ss2) + "";

        return getTimeClean(s1 + ":" + s2) + " " + a;
    }

}
