/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.util.Scanner;

import com.mycompany.motorph.data.EmployeeDatabase;
import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.helpers.PrintFormatter;
import com.mycompany.motorph.helpers.State;

/**
 *
 * @author reden
 */
public class Motorph {

    static State state = State.Welcome;
    static boolean isRunning = true;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        do {
            Start();
        } while (isRunning);
    }

    static void Start() {
        switch (state) {
            case Welcome:
                Welcome();
                break;
            case Login:
                Login();
                break;
            case LoginEmployee:
                {
                    boolean isExists = ShowLogin("Employee Login");
                }
                break;
            case LoginPayroll:
                {
                    boolean isExists = ShowLogin("Payroll Login");
                }
                break;
            case Exit:
                isRunning = false;
                break;
            case Error:
                PrintFormatter.Panel("Motor PH", new String[] { "Error occured.", "Exiting..." });
                scanner.nextLine();
                isRunning = false;
                break;
            default:
                break;
        }
    }

    static void Welcome() {
        PrintFormatter.Panel("Motor PH", new String[] { "1.) Login", "2.) Exit" }, "Enter your choice:");
        if (scanner.hasNextInt()) {
            int input = scanner.nextInt();
            if (input == 1) {
                state = State.Login;
            } else if (input == 2) {
                state = State.Exit;
            } else {
                state = State.Error;
            }
        } else {
            state = State.Error;
        }
    }

    static void Login() {
        try {
            PrintFormatter.Panel("Login Page", new String[] { "1.) Employee", "2.) Payroll", "3.) Back", "4.) Exit" },
                    "Enter your choice:");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                switch (input) {
                    case 1:
                        state = State.LoginEmployee;
                        break;
                    case 2:
                        state = State.LoginPayroll;
                        break;
                    case 3:
                        state = State.Welcome;
                        break;
                    case 4:
                        state = State.Exit;
                        break;
                    default:
                        state = State.Error;
                        break;

                }
            } else {
                state = State.Error;
            }
        } catch (Exception e) {
            state = State.Error;
        }
    }

    static boolean ShowLogin(String title) {
        try {
            PrintFormatter.Panel(title, "Enter your Account Id:");
            if(scanner.hasNextInt()) {
                int accountId = scanner.nextInt(); 
                EmployeeDetail employee = EmployeeDatabase.findOne(accountId);
                if(employee != null) {
                    System.out.println("Employee Exists!");
                    return true;
                } else {
                    System.out.println("Employee Doesn't Exists!");
                    return false;
                }
            } else {
                state = State.Error; 
            }
        } catch(Exception ex) {
            state = State.Error;
        }
        return false;
    }
}
