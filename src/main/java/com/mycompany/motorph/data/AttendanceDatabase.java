/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.data;

import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
import java.util.Date;

/**
 *
 * @author reden
 */
public class AttendanceDatabase {

    public Date getTimeIn(int id) {
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
    
    public Attendance[] findAllAttendanceByDate(Date date) {
        return null;
    }
}
