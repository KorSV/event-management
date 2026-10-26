package com.ksv.view;

import java.util.Scanner;
import java.util.function.Predicate;

public class InputReader {
    private Scanner keyboard = new Scanner(System.in);

    public int getInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(keyboard.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("введите число");
            }
        }
    }

    public int getInt(String prompt, Predicate<Integer> validator, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(keyboard.nextLine().trim());
                if (!validator.test(value)) {
                    System.out.println(errorMessage);
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Введите число:");
            }
        }
    }

    public String getString(String prompt) {
        System.out.print(prompt);
        return keyboard.nextLine();
    }
}
