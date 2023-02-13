/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.util.Scanner;

import com.mycompany.motorph.data.EmployeeDatabase;
import com.mycompany.motorph.helpers.PrintFormatter;
import com.mycompany.motorph.helpers.State;

/**
 *
 * @author reden
 */
public class Motorph {

    static State state = State.Main;
    static boolean isRunning = true;
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        do {
            Start();
        } while (isRunning);
    }

    static void Start() {
        switch(state) {
            case Main:
                Welcome();
                break;
            case Login:
                Login();
                break;
            case Exit: 
                isRunning = false;
                break;
        }
    }

    static void showMainpage() {
        PrintFormatter.Panel("Dashboard", new String[]{"This is your dashboard."});
        scanner.nextLine();
    }

    static boolean ShowLogin(String title) {
        PrintFormatter.Panel("Login for " + title, "Enter your Account Id:");
        if (scanner.hasNextInt()) {
            int id = scanner.nextInt();
            boolean hasAccount = EmployeeDatabase.findOne(id) != null;
            if (!hasAccount) {
                scanner.nextLine();
                PrintFormatter.Panel(
                        "Login for " + title, new String[] { "Account Id doesn't exists!",
                                "Please contact your manager!", "1.) Try again", "2.) Exit" },
                        "Enter your choice:");
                if(scanner.hasNextInt()) {
                    int choice = scanner.nextInt(id);
                    if (choice == 1)
                        return false;
                    else {
                        isLogin = false;
                        isRunning = false;
                        return false;
                    }
                } else {
                    scanner.nextLine();
                    PrintFormatter.Panel("Login for " + title, new String[] { "Invalid input.", "Exiting..." });
                    scanner.nextLine();
                    isLogin = false;
                    isRunning = false;
                    return false;
                }
            } else {
                // check if account has password
                boolean hasPassword = checkAccountIdPassword(id);
                if (hasPassword) {
                    PrintFormatter.Panel("Login for " + title, "Enter your password:");
                    return true;
                } else {
                    scanner.nextLine();
                    PrintFormatter.Panel("Login for " + title,
                            new String[] { "Your account has no password", "Create your new password" },
                            "Enter your password:");
                    String password = scanner.next();
                    System.out.println(password);
                    scanner.nextLine();
                    scanner.nextLine();
                    return true;
                }
            }
        } else {
            PrintFormatter.Panel("Login for " + title, new String[] { "Invalid input.", "Exiting..." });
            scanner.nextLine();
            return false;
        }
    }

    private static boolean checkAccountIdPassword(int id) {
        return false;
    }

    private static boolean checkAccountId(int id) {
        return EmployeeDatabase.findOne(id) != null;
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
                PrintFormatter.Panel("Motor PH", new String[] { "Invalid input.", "Exiting..." });
                scanner.nextLine();
                state = State.Exit;
            }
        } else {
            PrintFormatter.Panel("Motor PH", new String[] { "Invalid input.", "Exiting..." });
            scanner.nextLine();
            state = State.Exit;
        }
    }

    static int Login() {
        try {
            PrintFormatter.Panel("Login Page", new String[] { "1.) Employee", "2.) Payroll", "3.) Back", "4.) Exit" },
                    "Enter your choice:");
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                return input;
            } else {
                return 3;
            }
        } catch (Exception e) {
            return 3;
        }
    }
}
