package edu.int1332.tourprj;

import java.util.ArrayList;

/** Console input boundary. It owns numeric recovery and generic list selection. */
public final class Menu {
    private Menu() { }

    public static <E> int intGetChoice(ArrayList<E> options) { return -1; }
    public static <E> E refGetChoice(ArrayList<E> options) { return null; }
    /*
     * Người bổ sung: Lê Thuần Trường
     * Ngày bổ sung: 18/11/2026
     * Mô tả: Scanner dùng chung và hàm đọc dữ liệu phục vụ II.6.
     */
    private static final java.util.Scanner INPUT =
            new java.util.Scanner(System.in);

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: Hiển thị lời nhắc và đọc một dòng,
     *        loại bỏ khoảng trắng ở hai đầu.
     */
    public static String readLine(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine().trim();
    }
}
