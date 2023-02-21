package com.mycompany.motorph.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import com.mycompany.motorph.data.model.Account;
import com.mycompany.motorph.helpers.TextFileParser;

public class AccountDatabase {

    public static Account findOne(int id) {
        List<Account> account = findAll();
        for (int i = 0; i < account.size(); i++) {
            if (account.get(i).getEmployeeId() == id) {
                return account.get(i);
            }
        }
        return null;
    }

    public static List<Account> findAll() {
        try {
            FileReader file = new FileReader("src/main/resources/account.txt");
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(",");
            scanner.nextLine();
            List<Account> accounts = new ArrayList<Account>();
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                ArrayList<String> accArr = TextFileParser.ParseRow(line);
                Account account = new Account();
                account.setEmployeeId(Integer.parseInt(accArr.get(0)));
                account.setPassword(accArr.get(1));
                accounts.add(account);
            }
            scanner.close();
            return accounts;
        } catch (FileNotFoundException e) {
            return new ArrayList<Account>();
        } catch (NoSuchElementException e) {
            return new ArrayList<Account>();
        }
    } 

    public static void save(List<Account> accounts) {
        try {
            PrintWriter file = new PrintWriter("src/main/resources/account.txt");
            file.println("Employee #,Password");
            for (Account account : accounts) {
                file.println(String.join(",", account.getEmployeeId() + "", account.getPassword()));    
                System.out.println(String.join(",", account.getEmployeeId() + "", account.getPassword()));    
            }
            file.flush();
            file.close();
        } catch (FileNotFoundException e) {
        }  
    }
}
