// Tên: Nguyễn Hữu Đức
// MSV: B23DCKD017
// Mô tả: Cung cấp nhập liệu console an toàn, phục hồi khi nhập sai và chọn đối tượng tổng quát.

package edu.int1332.tourprj;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/** Console input boundary shared by all menu operations. */
public final class Menu {
    private static InputStream boundInput;
    private static Scanner scanner;

    private Menu() { }

    public static <E> int intGetChoice(ArrayList<E> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Options cannot be null or empty.");
        }
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, options.get(i));
        }
        return readIntInRange("Choose 1-" + options.size() + ": ", 1, options.size());
    }

    public static <E> E refGetChoice(ArrayList<E> options) {
        return options.get(intGetChoice(options) - 1);
    }

    public static int readIntInRange(String prompt, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Minimum cannot exceed maximum.");
        }
        while (true) {
            String raw = readLine(prompt);
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Print one consistent message below and retry.
            }
            System.out.printf("Invalid choice. Enter an integer from %d to %d.%n", min, max);
        }
    }

    public static double readNonNegativeDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                double value = Double.parseDouble(raw);
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // Print one consistent message below and retry.
            }
            System.out.println("Value must be a finite number greater than or equal to 0.");
        }
    }

    public static String readRequiredText(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value.isEmpty()) {
                System.out.println("Value cannot be empty.");
            } else if (value.contains(",")) {
                System.out.println("Value cannot contain a comma.");
            } else {
                return value;
            }
        }
    }

    public static String readLine(String prompt) {
        Scanner current = input();
        System.out.print(prompt);
        if (!current.hasNextLine()) {
            throw new IllegalStateException("Input stream ended.");
        }
        return current.nextLine().trim();
    }

    private static Scanner input() {
        if (scanner == null || boundInput != System.in) {
            boundInput = System.in;
            scanner = new Scanner(boundInput);
        }
        return scanner;
    }
}
