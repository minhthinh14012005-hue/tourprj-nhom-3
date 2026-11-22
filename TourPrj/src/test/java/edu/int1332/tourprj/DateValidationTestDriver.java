package edu.int1332.tourprj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Người thực hiện: Lê Thuần Trường
 * Ngày thực hiện: 22/11/2026
 * Mô tả: III.6 - Kiểm thử ngày khởi hành và xử lý
 *        ngày không hợp lệ khi đọc file Tour.
 */
public final class DateValidationTestDriver {
    private static int passed = 0;
    private static int failed = 0;

    private DateValidationTestDriver() {
    }

    private static void check(String label, boolean condition) {
        System.out.printf(
                "[%s] %s%n",
                condition ? "PASS" : "FAIL",
                label
        );

        if (condition) {
            passed++;
        } else {
            failed++;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println("=== KIEM TRA HAM VALIDATE NGAY ===");

        check("Ngay hop le",
                Tour.isValidDepartureDate("2026-11-22"));

        check("Ngay nhuan hop le",
                Tour.isValidDepartureDate("2024-02-29"));

        check("Nam khong nhuan",
                !Tour.isValidDepartureDate("2026-02-29"));

        check("Thang 4 khong co ngay 31",
                !Tour.isValidDepartureDate("2026-04-31"));

        check("Thang 13",
                !Tour.isValidDepartureDate("2026-13-01"));

        check("Thang 0",
                !Tour.isValidDepartureDate("2026-00-10"));

        check("Ngay 0",
                !Tour.isValidDepartureDate("2026-01-00"));

        check("Thieu chu so thang va ngay",
                !Tour.isValidDepartureDate("2026-1-2"));

        check("Sai dinh dang dd/MM/yyyy",
                !Tour.isValidDepartureDate("22/11/2026"));

        check("Chuoi rong",
                !Tour.isValidDepartureDate(""));

        check("Gia tri null",
                !Tour.isValidDepartureDate(null));

        check("Nam 1900 khong phai nam nhuan",
                !Tour.isValidDepartureDate("1900-02-29"));

        System.out.println("\n=== KIEM TRA DOC FILE ===");

        DestinationList destinations = new DestinationList();
        destinations.add(
                new Destination("D01", "Da Nang", "Central", 1500000)
        );

        Path tempDirectory =
                Files.createTempDirectory("truong-date-test-");

        Path input = tempDirectory.resolve("tours-dates.txt");

        Files.write(
                input,
                Arrays.asList(
                        "T01,D01,Valid Tour,2026-11-22,1000000",
                        "T02,D01,Invalid Date,2026-02-30,2000000",
                        "T03,D01,Wrong Format,22/11/2026,3000000",
                        "T04,D01,Leap Tour,2024-02-29,4000000",
                        "T02,D01,Valid Replacement,2026-12-01,2000000"
                ),
                StandardCharsets.UTF_8
        );

        TourList tours = new TourList();
        boolean allValid =
                tours.loadFromFile(input.toString(), destinations);

        check("Ham nap tra false khi co dong sai ngay",
                !allValid);

        check("Bo qua dong sai va giu du 3 Tour hop le",
                tours.size() == 3
                        && tours.searchId("T01") != -1
                        && tours.searchId("T02") != -1
                        && tours.searchId("T03") == -1
                        && tours.searchId("T04") != -1);

        int t02Index = tours.searchId("T02");

        check("Dong sai khong chiem ma cua dong hop le phia sau",
                t02Index != -1
                        && tours.get(t02Index).getDepartureDate()
                        .equals("2026-12-01"));

        System.out.printf(
                "%nRESULT: %d passed, %d failed%n",
                passed, failed
        );

        System.out.println("Test file: " + input);

        if (failed > 0) {
            System.exit(1);
        }
    }
}