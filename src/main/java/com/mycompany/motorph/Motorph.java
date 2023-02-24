/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

import javax.xml.transform.stax.StAXResult;

import com.mycompany.motorph.data.AccountDatabase;
import com.mycompany.motorph.data.AttendanceDatabase;
import com.mycompany.motorph.data.EmployeeDatabase;
import com.mycompany.motorph.data.PayslipDatabase;
import com.mycompany.motorph.data.model.Account;
import com.mycompany.motorph.data.model.Attendance;
import com.mycompany.motorph.data.model.EmployeeDetail;
import com.mycompany.motorph.data.model.Payslip;
import com.mycompany.motorph.data.model.Timesheet;
import com.mycompany.motorph.helpers.AccountType;
import com.mycompany.motorph.helpers.Deduction;
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
            case ViewManageTimesheet:
                ManageTimesheet();
                break;
            case ListTimeSheet:
                PastTimeSheet();
                break;
            case PendingTimesheet:
                ViewPendingTimesheet();
                break;
            case CalculatePay:
                CalculatePay();
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

    private static void CalculatePay() {
        PrintFormatter.Panel("Manage Timesheet - Calculate Pay",
                "Enter account Id:");
        int input = 0;
        boolean valid = false;
        EmployeeDetail employee = null;
        while (!valid) {
            try {
                while (!scanner.hasNextInt()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
                input = Integer.parseInt(scanner.next());
                employee = EmployeeDatabase.findOne(input);
                if (employee != null)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (employee != null) {
            PrintFormatter.Panel("Manage Timesheet - Calculate Pay",
                    new String[] { "Enter date ranges", "Format:", "Date is limited to 5 days range",
                            "MM/dd/yyyy-MM/dd/yyyy for example 02/24/2023-02/26/2023",
                            "turns to February 24, 2023" },
                    "Enter your date range: ");

            String inp = "";
            boolean isValid = false;
            DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            while (!isValid) {
                try {
                    while (!scanner.hasNext()) {
                        scanner.next();
                        PrintFormatter.InvalidInput("Enter your date range: ");
                    }
                    inp = scanner.next();
                    String[] dates = inp.split("-");
                    LocalDate d1 = LocalDate.parse(dates[0], df);
                    LocalDate d2 = LocalDate.parse(dates[1], df);
                    long days = 0;
                    if (d1 != null && d2 != null) {
                        days = ChronoUnit.DAYS.between(d1, d2);
                    }
                    if ((days + 1) == 5)
                        isValid = true;
                    else {
                        PrintFormatter.InvalidInput("Enter your date range: ");
                        continue;
                    }

                } catch (DateTimeParseException e) {
                    PrintFormatter.InvalidInput("Enter your date range: ");
                    continue;
                } catch (Exception e) {
                    PrintFormatter.InvalidInput("Enter your date range: ");
                    continue;
                }
            }

            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            String[] dates = inp.split("-");

            Payslip p = new Payslip();
            p.setId(employee.getId());
            p.setPayBeginDate(f2.format(LocalDate.parse(dates[0], f1)));
            p.setPayEndDate(f2.format(LocalDate.parse(dates[1], f1)));
            p.setPayDate(f2.format(LocalDate.parse(PayslipDatabase.getTodayDate(), f1)));
            p.setRegularHours(PayslipDatabase.getRegularHours(dates[0], dates[1], employee.getId()));
            p.setOvertimehours(PayslipDatabase.getOvertimeHours(dates[0], dates[1], employee.getId()));
            p.setRegularHoursPay(p.getRegularHours() * employee.getHourlyRate());
            p.setOvertimeHoursPay(p.getOvertimehours() * employee.getHourlyRate() * 1.25);
            p.setGrossPay(p.getOvertimeHoursPay() + p.getRegularHoursPay());

            double totalDeduction = 0.00;
            double sss = Deduction.getSSSContribution(p.getGrossPay() * 4);
            double ph = Deduction.getPhilhealthContribution(p.getGrossPay() * 4);
            double pagibig = Deduction.getPagibigContribution(p.getGrossPay() * 4);
            totalDeduction = sss + ph + pagibig;
            double tax = Deduction.getWithholdingtax((p.getGrossPay() * 4) - totalDeduction);
            totalDeduction += tax;
            double netPay = p.getGrossPay() - (totalDeduction / 4);
            p.setNetPay(netPay);
            p.setSss(sss / 4);
            p.setPhilhealth(ph / 4);
            p.setPagibig(pagibig / 4);
            p.setTax(tax / 4);

            List<Payslip> payslips = PayslipDatabase.findAll();

            int i = 0;
            for (Payslip payslip : payslips) {
                Payslip pp = payslip;
                pp.setPayBeginDate(f2.format(LocalDate.parse(pp.getPayBeginDate(), f1)));
                pp.setPayEndDate(f2.format(LocalDate.parse(pp.getPayEndDate(), f1)));
                pp.setPayDate(f2.format(LocalDate.parse(pp.getPayDate(), f1)));
                payslips.set(i, pp);
                i++;
            }
            payslips.add(p);
            PayslipDatabase.save(payslips);

            PrintFormatter.Panel("Manage Timesheet - Calculate Pay",
                    new String[] {
                            "Payslip has been made for ID#" + employee.getId() + " - " + employee.getLastName() },
                    "Press any key to go back");
            scanner.nextLine();
            scanner.nextLine();
            state = State.PayrollDashboard;
        }
        state = State.PayrollDashboard;
    }

    private static void ViewPendingTimesheet() {
        PrintFormatter.Panel("Manage Timesheet - View Pending Timesheet",
                new String[] { "Enter date ranges", "", "Format:",
                        "MM/dd/yyyy-MM/dd/yyyy for example 02/24/2023-02/26/2023",
                        "turns to February 24, 2023" },
                "Enter your date range: ");

        String input = "";
        boolean isValid = false;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        while (!isValid) {
            try {
                while (!scanner.hasNext()) {
                    scanner.next();
                    PrintFormatter.InvalidInput("Enter your date range: ");
                }
                input = scanner.next();
                String[] dates = input.split("-");
                if (LocalDate.parse(dates[0], df) != null && LocalDate.parse(dates[1], df) != null)
                    isValid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your date range: ");
                    continue;
                }
            } catch (Exception e) {
                PrintFormatter.InvalidInput("Enter your date range: ");
                continue;
            }
        }

        PrintFormatter.Panel("Manage Timesheet - View Pending Timesheet",
                new String[] { "Loading data...." },
                "...");

        String[] dates = input.split("-");

        LocalDate start = LocalDate.parse(dates[0], df);
        LocalDate end = LocalDate.parse(dates[1], df);

        List<String> choices = new ArrayList<String>();

        choices.add(" ---- " + df.format(start) + " - " + df.format(end) + " ---- ");
        choices.add("");
        List<EmployeeDetail> employees = PayslipDatabase.findAllPendingEmployeeByWeek(df.format(start),
                df.format(end));
        for (EmployeeDetail employeeDetail : employees) {
            choices.add(EmployeeFormatter(employeeDetail));
        }
        choices.add("");
        choices.add("");
        PrintFormatter.Panel("View Pending Timesheet",
                choices.toArray(new String[0]),
                "Press any key to go back.");
        scanner.nextLine();
        scanner.nextLine();
        state = State.PayrollDashboard;
    }

    private static String EmployeeFormatter(EmployeeDetail employeeDetail) {
        return String.format("%15s %15s %15s", (employeeDetail.getId() + ""), employeeDetail.getLastName(),
                employeeDetail.getFirstName());
    }

    private static void ManageTimesheet() {
        PrintFormatter.Panel("Manage Timesheet",
                new String[] { "1.) View Pending Timesheet", "2.) Calculate Pay", "3.) Back" },
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
                if (input <= 3 && input >= 1)
                    valid = true;
                else {
                    PrintFormatter.InvalidInput("Enter your choice:");
                }
            } catch (Exception e) {
                continue;
            }
        }
        if (input == 1) {
            state = State.PendingTimesheet;
        } else if (input == 2) {
            state = State.CalculatePay;
        } else if (input == 3) {
            state = State.PayrollDashboard;
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
        List<String> choices = new ArrayList<String>();
        List<Payslip> payslips = PayslipDatabase.findAllById(loggedInUser.getId());
        int index = 1;
        if (payslips.size() > 0) {
            for (Payslip payslip : payslips) {
                choices.add((index++) + ".) " + payslip.getPayBeginDate());
            }
            PrintFormatter.Panel("Payslips - ID#" + loggedInUser.getId(), choices.toArray(new String[0]),
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
                    if (input >= 1 && input <= payslips.size())
                        valid = true;
                    else {
                        PrintFormatter.InvalidInput("Enter your choice:");
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            List<String> showP = new ArrayList<String>();
            Payslip p = payslips.get(input - 1);
            showP.add(" -------- " + "Submitted Pay Date:" + p.getPayDate() + " --------");
            showP.add("");
            showP.add(ItemFormatter("Hourly Rate:", loggedInUser.getHourlyRate()));
            showP.add(ItemFormatter("Pay Start Date:", p.getPayBeginDate()));
            showP.add(ItemFormatter("Pay End Date:", p.getPayEndDate()));
            showP.add(ItemFormatter("Regular Hours Worked:", p.getRegularHours()));
            showP.add(ItemFormatter("Overtime Hours Worked:", p.getOvertimehours()));
            showP.add("");
            showP.add(" ---------------------------------------");
            showP.add("");
            showP.add(ItemFormatter("Regular Hours Pay:", p.getRegularHoursPay()));
            showP.add(ItemFormatter("Overtime Hours Pay:", p.getOvertimeHoursPay()));
            showP.add(ItemFormatter("Gross Pay:", p.getGrossPay()));
            showP.add("");
            showP.add(" ---------------------------------------");
            showP.add("");
            showP.add(ItemFormatter("SSS Contribution:", p.getSss()));
            showP.add(ItemFormatter("PhilHealth Contribution:", p.getPhilhealth()));
            showP.add(ItemFormatter("Pagibig Contribution:", p.getPagibig()));
            showP.add(ItemFormatter("Withholding Tax:", p.getTax()));
            showP.add(ItemFormatter("Net Pay:", p.getNetPay()));

            PrintFormatter.Panel("Payslips - ID#" + loggedInUser.getId(), showP.toArray(new String[0]),
                    "Press any key to go back");

            scanner.nextLine();
            scanner.nextLine();

            if (accountType == AccountType.Employee)
                state = State.EmployeeDashboard;
            else
                state = State.PayrollDashboard;

        } else {
            choices.add("You currently have no payslip.");
            PrintFormatter.Panel("Payslips - ID#" + loggedInUser.getId(), choices.toArray(new String[0]),
                    "Press any key to go back");
            scanner.nextLine();
            scanner.nextLine();
            if (accountType == AccountType.Employee)
                state = State.EmployeeDashboard;
            else
                state = State.PayrollDashboard;
        }
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
                "3.) View Payslip", "4.) Manage Timesheet", "5.) Logout" },
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
            state = State.ViewManageTimesheet;
        } else if (input == 5) {
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

    static String ItemFormatter(String t1, String t2) {
        return String.format("%25s %15s", t1, t2);
    }

    static String ItemFormatter(String t1, double t2) {
        return String.format("%25s %15f", t1, t2);
    }
}
