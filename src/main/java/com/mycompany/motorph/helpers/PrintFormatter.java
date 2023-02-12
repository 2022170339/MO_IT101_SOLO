/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.motorph.helpers;

import java.util.Scanner;

/**
 *
 * @author reden
 */
public class PrintFormatter {

    public static void Panel(String title, String[] content) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        int COUNT = 30;
        PrintLine(COUNT);
        System.out.println("|");
        Title(title);
        System.out.println("|");
        PrintLine(COUNT);
        System.out.println("|");
        Content(content);
        System.out.println("|");
        PrintLine(COUNT);
    }

    public static void Panel(String title, String[] content, String prompt) {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        int COUNT = 30;
        PrintLine(COUNT);
        System.out.println("|");
        Title(title);
        System.out.println("|");
        PrintLine(COUNT);
        System.out.println("|");
        Content(content);
        System.out.println("|");
        PrintLine(COUNT);
        Input(prompt);
    }

    public static void Input(String prompt) {
        System.out.print(">" + prompt);
        Scanner scanner = new Scanner(System.in);
        String ans = scanner.nextLine();
        System.out.println(ans);
    }

    public static void Title(String title) {
        System.out.println("|  " + title);
    }

    public static void Content(String[] content) {
        for (String string : content) {
            System.out.println("|  " + string);
        }
    }

    public static void PrintLine(int count) {
        for (int i = 0; i < count; i++) {
            System.out.print("=");
        }
        System.out.println();
    }
}
