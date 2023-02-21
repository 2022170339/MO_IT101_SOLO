/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.data;

import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
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
        if(attendance == null) {
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

    public static void updateTimeOut(int accountId) {
        Attendance attendance = checkAttendance(accountId);    
        if(attendance != null) {
            attendance.setTimeOut(getTime());
            List<Attendance> attendances = findAll();
            for (int i = 0; i < attendances.size(); i++) {
                if(attendances.get(i).getId() == attendance.getId() && attendances.get(i).getDate().equals(attendance.getDate())) 
                {
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
            if(attendance.getDate().equals(strDate)) {
                return attendance;
            }
        }
        return null;
    }

    public static Attendance checkAttendance(int accountId) {
        List<Attendance> attendances = findAll();
        for (Attendance attendance : attendances) {
            if(attendance.getId() == accountId && attendance.getDate().equals(getTodayDate())) {
                return attendance;
            }
        }
        return null;
    }

    public static String getTodayDate() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
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

    public static boolean isWeekend(final LocalDate ld)
    {
        DayOfWeek day = DayOfWeek.of(ld.get(ChronoField.DAY_OF_WEEK));
        return day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY;
    }
}
