/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import com.mycompany.motorph.data.AccountDatabase;
import com.mycompany.motorph.data.AttendanceDatabase;
import com.mycompany.motorph.data.EmployeeDatabase;
import com.mycompany.motorph.data.model.Account;
import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.data.model.Timesheet;
import com.mycompany.motorph.helpers.AccountType;
import com.mycompany.motorph.helpers.PrintFormatter;
import com.mycompany.motorph.helpers.State;

/**
 *
 * @author reden
 */
public class Motorph {

    static boolean isRunning = true;
    static Scanner scanner = new Scanner(System.in);

    static State state = State.Welcome;
    static AccountType accountType = AccountType.Employee;

    static EmployeeDetail loggedInUser = null;
    static String loggedInDateTime = "";

    public static void main(String[] args) {
        do {
            Start();
        } while (isRunning);
    }

    static void Start() {
        switch (state) {
            case Welcome:
                WelcomePage();
                break;
            case Login:
                LoginPage();
                break;
            case LoginEmployee:
                loggedInUser = null;
                loggedInDateTime = "";
                ShowLogin("Login for Employee", AccountType.Employee);
                break;
            case LoginPayroll:
                loggedInUser = null;
                loggedInDateTime = "";
                ShowLogin("Login for Payroll", AccountType.Payroll);
                break;
            case EmployeeDashboard:
                EmployeeDashboardPage();
                break;
            case PayrollDashboard:
                PayrollDashboard();
                break;
            case ViewProfile:
                ViewProfilePage();
                break;
            case ViewTimeSheet:
                ViewTimeSheetPage();
                break;
            case ViewPayslip:
                ViewPaySlips();
                break;
            case ViewManageEmployee:
                break;
            case ViewManageTimesheet:
                break;
            case ListTimeSheet:
                PastTimeSheet();
                break;
            case Exit:
                PrintFormatter.Panel("Motor PH", new String[] { "Exiting..." });
                scanner.nextLine();
                scanner.nextLine();
                isRunning = false;
                break;
            case Error:
                PrintFormatter.Panel("Motor PH", new String[] { "Error occured.", "Exiting..." });
                scanner.nextLine();
                scanner.nextLine();
                isRunning = false;
                break;
            case NoAccount:
                PrintFormatter.Panel("Motor PH", new String[] { "Account doesn't exists.",
                        "If you need help, contact the managers.", "Going back..." });
                scanner.nextLine();
                scanner.nextLine();
                state = State.Login;
                break;
            case InvalidInput:
                PrintFormatter.Panel("Motor PH", new String[] { "Input is invalid.", "Try again..." });
                scanner.nextLine();
                scanner.nextLine();
                break;
            default:
                break;
        }
    }

    private static void PastTimeSheet() {
        List<Timesheet> timesheets = AttendanceDatabase.getSortedTimesheet(loggedInUser.getId());
        List<String> dates = new ArrayList<String>();
        timesheets.forEach((t) -> dates.add(DateFormatter(t.getDate(), t.getTimeIn(), t.getTimeOut())));
        PrintFormatter.Panel("Past Time Sheet - ID#" + loggedInUser.getId(), dates.toArray(new String[0]),
                "Press any key to go back");
        scanner.nextLine();
        scanner.nextLine();
        if (accountType == AccountType.Employee)
            state = State.EmployeeDashboard;
        else
            state = State.PayrollDashboard;
    }

    private static void ViewPaySlips() {

    }

    static void WelcomePage() {
        PrintFormatter.Panel("Motor PH", new String[] { "1.) Login", "2.) Exit" }, "Enter your choice:");
        int input = 0;
        boolean valid = false;
        while (!valid) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
                input = Integer.parseInt(scanner.next());
                if (input <= 2 && input >= 1)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (input == 1) {
            state = State.Login;
        } else if (input == 2) {
            state = State.Exit;
        }
    }

    static void LoginPage() {
        PrintFormatter.Panel("Login Page", new String[] { "1.) Employee", "2.) Payroll", "3.) Back", "4.) Exit" },
                "Enter your choice:");
        int input = 0;
        boolean valid = false;
        while (!valid) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
                input = Integer.parseInt(scanner.next());
                if (input <= 4 && input >= 1)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
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
    }

    static void ShowLogin(String title, AccountType type) {
        PrintFormatter.Panel(title,
                new String[] { "Account Id and Password", "", "Requirements:", "Account Id must be a number",
                        "Password can be any combination of letters and numbers",
                        "Password length must be between 8 - 16" },
                "Enter your account Id:");
        int accountId = 0;
        boolean validAccountId = false;
        while (!validAccountId) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your account Id:");
                }
                accountId = Integer.parseInt(scanner.next());
                if (accountId > 0)
                    validAccountId = true;
                else {
                    PrintFormatter.InvalidInput("Enter your account Id:");
                }
            } catch (Exception e) {
                continue;
            }
        }

        EmployeeDetail employee = EmployeeDatabase.findOne(accountId);

        if (employee != null) {
            if (CheckAccountType(employee.getPosition()) != type) {
                state = State.NoAccount;
                return;
            }
            Account account = AccountDatabase.findOne(accountId);
            if (account != null) {
                PrintFormatter.Panel(title, new String[] { "Account Id and Password", "", "Requirements:",
                        "Account Id must be a number", "Password can be any combination of letters and numbers",
                        "Password length must be between 8 - 16" }, "Enter your password:");
                String password = "";
                boolean validPassword = false;
                while (!validPassword) {
                    try {
                        while (!scanner.hasNext()) {
                            scanner.next();
                            PrintFormatter.InvalidInput("Enter your password:");
                        }
                        password = scanner.next();
                        if (password.length() >= 8 && password.length() <= 16)
                            validPassword = true;
                        else {
                            PrintFormatter.InvalidInput("Enter your password:");
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }

                boolean correctPassword = false;

                List<Account> accounts = AccountDatabase.findAll();
                for (Account acc : accounts) {
                    if (acc.getEmployeeId() == account.getEmployeeId()) {
                        if (acc.getPassword().trim().toLowerCase().equals(password.trim().toLowerCase()))
                            correctPassword = true;
                        break;
                    }
                }
                if (correctPassword) {
                    PrintFormatter.Panel(title,
                            new String[] { "Account Id and Password", "", "Password is correct! Logging in..." });
                    scanner.nextLine();
                    scanner.nextLine();

                    loggedInUser = employee;
                    loggedInDateTime = new Date().toString();
                    CheckAccountType(employee.getPosition());
                    if (accountType == AccountType.Employee)
                        state = State.EmployeeDashboard;
                    else
                        state = State.PayrollDashboard;
                } else {
                    PrintFormatter.Panel(title,
                            new String[] { "Account Id and Password", "", "Password is incorrect! Try again..." });
                    scanner.nextLine();
                    scanner.nextLine();
                    state = State.Login;
                }
                // continue
            } else {
                PrintFormatter.Panel(title,
                        new String[] { "Account Id and Password", "", "No password has been saved.",
                                "Enter new Password", "", "Requirements",
                                "Password can be any combination of letters and numbers",
                                "Password length must be between 8 - 16" },
                        "Enter your password:");
                String password = "";
                boolean validPassword = false;
                while (!validPassword) {
                    try {
                        while (!scanner.hasNext()) {
                            scanner.next();
                            PrintFormatter.InvalidInput("Enter your password:");
                        }
                        password = scanner.next();
                        if (password.length() >= 8 && password.length() <= 16)
                            validPassword = true;
                        else {
                            PrintFormatter.InvalidInput("Enter your password:");
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                List<Account> accounts = AccountDatabase.findAll();
                account = new Account();
                account.setEmployeeId(accountId);
                account.setPassword(password);
                accounts.add(account);
                AccountDatabase.save(accounts);
                PrintFormatter.Panel(title,
                        new String[] { "Account Id and Password", "", "Password saved! Logging in..." });
                scanner.nextLine();
                scanner.nextLine();

                loggedInUser = employee;
                loggedInDateTime = new Date().toString();

                CheckAccountType(employee.getPosition());
                if (accountType == AccountType.Employee)
                    state = State.EmployeeDashboard;
                else
                    state = State.PayrollDashboard;
            }
        } else {
            state = State.NoAccount;
        }
    }

    static void EmployeeDashboardPage() {
        PrintFormatter.Panel("Employee Dashboard",
                new String[] { "1.) View Profile", "2.) Timesheet", "3.) View Payslip", "4.) Logout" },
                "Enter your choice:");
        int input = 0;
        boolean valid = false;
        while (!valid) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
                input = Integer.parseInt(scanner.next());
                if (input <= 4 && input >= 1)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (input == 1) {
            state = State.ViewProfile;
        } else if (input == 2) {
            state = State.ViewTimeSheet;
        } else if (input == 3) {
            state = State.ViewPayslip;
        } else if (input == 4) {
            PrintFormatter.Panel("Employee Dashboard", new String[] { "Logging out..." });
            scanner.nextLine();
            scanner.nextLine();
            loggedInUser = null;
            state = State.Login;
        }
    }

    static void PayrollDashboard() {
        PrintFormatter.Panel("Payroll Dashboard", new String[] { "1.) View Profile", "2.) Timesheet",
                "3.) View Payslip", "4.) Manage Employees", "5.) Manage Timesheet", "6.) Logout" },
                "Enter your choice:");
        int input = 0;
        boolean valid = false;
        while (!valid) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
                input = Integer.parseInt(scanner.next());
                if (input <= 6 && input >= 1)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (input == 1) {
            state = State.ViewProfile;
        } else if (input == 2) {
            state = State.ViewTimeSheet;
        } else if (input == 3) {
            state = State.ViewPayslip;
        } else if (input == 4) {
            state = State.ViewManageEmployee;
        } else if (input == 5) {
            state = State.ViewManageTimesheet;
        } else if (input == 6) {
            PrintFormatter.Panel("Employee Dashboard", new String[] { "Logging out..." });
            scanner.nextLine();
            scanner.nextLine();
            loggedInUser = null;
            state = State.Login;
        }
    }

    // View Profile
    static void ViewProfilePage() {
        PrintFormatter.Panel("Profile - ID#" + loggedInUser.getId(),
                new String[] {
                        "Last Logged In: " + loggedInDateTime, "",
                        "Name: " + loggedInUser.getFirstName() + " " + loggedInUser.getLastName(),
                        "Birthday: " + loggedInUser.getBirthDay(),
                        "Phone: " + loggedInUser.getPhoneNumber(),
                        "Address: " + loggedInUser.getAddress(),
                        "",
                        "Position: " + loggedInUser.getPosition(),
                        "Status: " + loggedInUser.getStatus(),
                        "Supervisor: " + loggedInUser.getImmediateSupervisor()
                },
                "Enter any key to go back");
        scanner.nextLine();
        scanner.nextLine();
        state = accountType == AccountType.Employee ? State.EmployeeDashboard : State.PayrollDashboard;
    }

    static void ViewTimeSheetPage() {
        List<String> info = new ArrayList<String>();
        List<String> choices = new ArrayList<String>();
        Attendance attendance = AttendanceDatabase.checkAttendance(loggedInUser.getId());
        boolean isWeekend = AttendanceDatabase.isWeekend(LocalDate.now());
        boolean clockIn = false;
        boolean clockOut = false;

        if (isWeekend) {
            info.clear();
            choices.clear();
            info.add("You have no shift today.");
            choices.add("1.) List Past Timesheet");
            choices.add("2.) Back");
        } else {
            info.clear();
            choices.clear();
            info.add("Shift: " + AttendanceDatabase.getTodayDate());

            if (attendance == null) {
                clockIn = false;
                clockOut = false;
                info.add("Clock-In Time: Not set");
                info.add("Clock-Out Time: Not set");
                info.add("");
                choices.add("1.) Clock-in");
                choices.add("2.) List Past Timesheet");
                choices.add("3.) Back");
            } else {
                clockIn = !attendance.getTimeIn().isEmpty();
                clockOut = !attendance.getTimeOut().isEmpty();

                if (clockIn && clockOut) {
                    info.add("Clock-In Time: " + attendance.getTimeIn());
                    info.add("Clock-Out Time: " + attendance.getTimeOut());
                    info.add("");
                    choices.add("1.) List Past Timesheet");
                    choices.add("2.) Back");
                } else if (clockIn && !clockOut) {
                    info.add("Clock-In Time: " + attendance.getTimeIn());
                    info.add("Clock-Out Time: Not set");
                    info.add("");
                    choices.add("1.) Clock-out");
                    choices.add("2.) List Past Timesheet");
                    choices.add("3.) Back");
                } else {
                    info.add("Clock-In Time: Not set");
                    info.add("Clock-Out Time: Not set");
                    info.add("");
                    choices.add("1.) Clock-in");
                    choices.add("2.) List Past Timesheet");
                    choices.add("3.) Back");
                }
            }
        }

        boolean min = choices.size() == 2;
        List<String> all = new ArrayList<String>();
        all.addAll(info);
        all.addAll(choices);
        if (min) {
            PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                    all.toArray(new String[0]), "Enter your choice:");

            int input = 0;
            boolean valid = false;
            while (!valid) {
                try {
                    while (!scanner.hasNextInt()) {
                        scanner.next();
                        PrintFormatter.InvalidInput("Enter your choice:");
                    }
                    input = Integer.parseInt(scanner.next());
                    if (input <= choices.size() && input >= 1)
                        valid = true;
                    else {
                        PrintFormatter.InvalidInput("Enter your choice:");
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (input == 1) {
                state = State.ListTimeSheet;
            } else if (input == 2) {
                PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                        new String[] { "Going back..." });
                scanner.nextLine();
                scanner.nextLine();
                if (CheckAccountType(loggedInUser.getPosition()) == AccountType.Employee)
                    state = State.EmployeeDashboard;
                else
                    state = State.PayrollDashboard;
            }
        } else {
            PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                    all.toArray(new String[0]), "Enter your choice:");

            int input = 0;
            boolean valid = false;
            while (!valid) {
                try {
                    while (!scanner.hasNextInt()) {
                        scanner.next();
                        PrintFormatter.InvalidInput("Enter your choice:");
                    }
                    input = Integer.parseInt(scanner.next());
                    if (input <= choices.size() && input >= 1)
                        valid = true;
                    else {
                        PrintFormatter.InvalidInput("Enter your choice:");
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (input == 1) {
                if (clockIn) {
                    PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                            new String[] { "Clocked-out successfully!" });
                    scanner.nextLine();
                    scanner.nextLine();
                    AttendanceDatabase.updateTimeOut(loggedInUser.getId());
                } else {
                    PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                            new String[] { "Clocked-in successfully!" });
                    scanner.nextLine();
                    scanner.nextLine();
                    AttendanceDatabase.insertTimeIn(loggedInUser.getId());
                }
            } else if (input == 2) {
                state = State.ListTimeSheet;
            } else if (input == 3) {
                PrintFormatter.Panel("Timesheet - ID#" + loggedInUser.getId(),
                        new String[] { "Going back..." });
                scanner.nextLine();
                scanner.nextLine();
                if (CheckAccountType(loggedInUser.getPosition()) == AccountType.Employee)
                    state = State.EmployeeDashboard;
                else
                    state = State.PayrollDashboard;
            }
        }
    }

    static AccountType CheckAccountType(String position) {
        String[] payroll = new String[] { "Account Team Leader", "HR Team Leader", "Payroll Manager" };
        String[] employee = new String[] { "Account Rank and File", "HR Rank and File" };

        if (Arrays.asList(payroll).contains(position)) {
            accountType = AccountType.Payroll;
        }

        if (Arrays.asList(employee).contains(position)) {
            accountType = AccountType.Employee;
        }

        return accountType;
    }

    static String DateFormatter(String date, String t1, String t2) {
        return String.format("%25s %10s %10s", date, t1, t2);
    }
}
