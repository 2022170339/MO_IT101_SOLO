/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.helpers.TextFileParser;

/**
 *
 * @author reden
 */
public class EmployeeDatabase {

    public static EmployeeDetail findOne(int id) {
        List<EmployeeDetail> employees = findAll();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
                return employees.get(i);
            }
        }
        return null;
    }

    public static List<EmployeeDetail> findAll() {
        try {
            FileReader file = new FileReader("src/main/resources/employee.txt");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            scanner.nextLine();
            List<EmployeeDetail> employees = new ArrayList<EmployeeDetail>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                ArrayList<String> empArr = TextFileParser.ParseRow(line);
                EmployeeDetail employee = new EmployeeDetail();
                employee.setId(Integer.parseInt(empArr.get(0)));
                employee.setLastName(empArr.get(1));
                employee.setFirstName(empArr.get(2));
                employee.setBirthDay(empArr.get(3));
                employee.setAddress(empArr.get(4));
                employee.setPhoneNumber(empArr.get(5));
                employee.setSssNumber(empArr.get(6));
                employee.setPhilhealthNumber(empArr.get(7));
                employee.setTinNumber(empArr.get(8));
                employee.setPagibigNumber(empArr.get(9));
                employee.setStatus(empArr.get(10));
                employee.setPosition(empArr.get(11));
                employee.setImmediateSupervisor(empArr.get(12));
                employee.setBasicSalary(Double.parseDouble(empArr.get(13)));
                employee.setRiceSubsidy(Double.parseDouble(empArr.get(14)));
                employee.setPhoneAllowance(Double.parseDouble(empArr.get(15)));
                employee.setClothingAllowance(Double.parseDouble(empArr.get(16)));
                employee.setGrossSemiMonthlyRate(Double.parseDouble(empArr.get(17)));
                employee.setHourlyRate(Double.parseDouble(empArr.get(18)));
                employees.add(employee);
            }
            scanner.close();
            return employees;
        } catch (FileNotFoundException e) {
            return new ArrayList<EmployeeDetail>();
        } catch (NoSuchElementException e) {
            return new ArrayList<EmployeeDetail>();
        }
    }

    public Boolean insert(EmployeeDetail data) {
        return false;
    }

    public int update(int id, EmployeeDetail data) {
        return 0;
    }

    public int delete(int id) {
        return 0;
    }
}
