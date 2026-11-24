package edu.int1332.tourprj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Người thực hiện: Lê Thuần Trường.
 * Ngày thực hiện: 23/11/2026.
 * Mô tả: III.6 - Kiểm thử nhập lại ngày và hủy thêm/sửa khi hết dữ liệu.
 */
public final class DateInputTestDriver {
    private static int passed;

    private DateInputTestDriver() { }

    private static final class FixedDestinationList extends DestinationList {
        @Override
        public Destination getUserChoice() {
            return isEmpty() ? null : get(0);
        }
    }

    private static String withInput(String text, Runnable action) {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream captured =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(
                    text.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            action.run();
        } finally {
            System.setIn(previousIn);
            System.setOut(previousOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void check(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError("[FAIL] " + label);
        }
        passed++;
        System.out.println("[PASS] " + label);
    }

    public static void main(String[] args) {
        FixedDestinationList destinations = new FixedDestinationList();
        Destination destination =
                new Destination("D01", "Da Nang", "Central", 1000);
        destinations.add(destination);
        TourList tours = new TourList();

        withInput(
                "T01\nBeach\n\n23/11/2026\n2026-02-29\n"
                        + "2026-04-31\n2028-02-29\n1500\n",
                () -> tours.addTour(destinations)
        );

        check("Them: nhap lai ngay sai, chap nhan ngay nhuan",
                tours.size() == 1
                        && tours.get(0).getTourId().equals("T01")
                        && tours.get(0).getDestination() == destination
                        && tours.get(0).getTourName().equals("Beach")
                        && tours.get(0).getDepartureDate().equals("2028-02-29")
                        && tours.get(0).getPrice() == 1500);

        Tour original = tours.get(0);
        boolean[] updated = {false};

        withInput(
                "T01\nT01\nNew Beach\n2026-13-01\n"
                        + "2026-2-01\n2026-12-01\n2000\n",
                () -> updated[0] = tours.updateTour(destinations)
        );

        check("Sua: nhap lai ngay sai va giu nguyen doi tuong Tour",
                updated[0] && tours.size() == 1 && tours.get(0) == original
                        && original.getTourId().equals("T01")
                        && original.getDestination() == destination
                        && original.getTourName().equals("New Beach")
                        && original.getDepartureDate().equals("2026-12-01")
                        && original.getPrice() == 2000);

        String addOutput = withInput(
                "T02\nCancelled\n2026-02-30\n",
                () -> tours.addTour(destinations)
        );

        check("Het du lieu sau ngay sai: huy them an toan",
                tours.size() == 1 && tours.get(0) == original
                        && tours.searchId("T02") == -1
                        && addOutput.contains("Invalid departure date"));

        updated[0] = true;
        String updateOutput = withInput(
                "T01\nT99\nCancelled\n2026-02-30\n",
                () -> updated[0] = tours.updateTour(destinations)
        );

        check("Het du lieu sau ngay sai: huy sua, giu du lieu cu",
                !updated[0] && tours.size() == 1 && tours.get(0) == original
                        && original.getTourId().equals("T01")
                        && original.getDestination() == destination
                        && original.getTourName().equals("New Beach")
                        && original.getDepartureDate().equals("2026-12-01")
                        && original.getPrice() == 2000
                        && updateOutput.contains("Invalid departure date"));

        System.out.printf("RESULT: %d passed, 0 failed%n", passed);
    }
}