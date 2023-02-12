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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author reden
 */
public class AttendanceDatabase {

    public static Date getTimeIn(int id) {
        return null;
    }

    public Date getTimeOut(int id) {
        return null;
    }

    public int insertTimeIn(EmployeeDetail user, Date timeIn) {
        return 0;
    }

    public int updateTimeOut(EmployeeDetail user, Date timeOut) {
        return 0;
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
            e.printStackTrace();
        }
        return null;
    }

    public Attendance[] findAllAttendanceByDate(Date date) {
        return null;
    }
}
