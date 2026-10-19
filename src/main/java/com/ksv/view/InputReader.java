package com.ksv.view;

import java.util.Scanner;

public class InputReader {
    private Scanner keyboard = new Scanner(System.in);

    public int getInt(String prompt){
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(keyboard.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("введите число");
            }
        }
    }

    public String getString(String prompt){
        System.out.print(prompt);
        return keyboard.nextLine();
    }
}
