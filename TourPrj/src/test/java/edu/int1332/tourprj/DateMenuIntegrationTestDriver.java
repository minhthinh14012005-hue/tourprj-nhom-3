package edu.int1332.tourprj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Người thực hiện: Lê Thuần Trường.
 * Ngày thực hiện: 24/11/2026.
 * Mô tả: III.6 - Kiểm thử qua TourManager, Menu và DestinationList thực tế.
 * Dùng dữ liệu tạm, không sửa file dữ liệu của dự án.
 */
public final class DateMenuIntegrationTestDriver {
    private static int passed;

    private DateMenuIntegrationTestDriver() { }

    private static Path seed(Path root, String name) throws IOException {
        Path directory = Files.createDirectory(root.resolve(name));

        Files.write(directory.resolve("destinations.txt"), Arrays.asList(
                "D01,Da Nang,Central,1500000",
                "D02,Ha Long,North,2000000",
                "D03,Hue,Central,1000000"), StandardCharsets.UTF_8);

        Files.write(directory.resolve("tours.txt"), Arrays.asList(
                "T01,D01,Beach,2027-01-15,2800000",
                "T02,D02,Bay,2027-02-12,3500000",
                "T03,D03,City,2027-03-10,3100000"), StandardCharsets.UTF_8);

        return directory;
    }

    private static String runApp(Path directory, String input) {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream captured =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);

            TourManager.main(new String[]{
                    directory.resolve("destinations.txt").toString(),
                    directory.resolve("tours.txt").toString()
            });
        } finally {
            System.setIn(previousIn);
            System.setOut(previousOut);
        }

        return output.toString(StandardCharsets.UTF_8);
    }

    private static TourList reload(Path directory) {
        DestinationList destinations = new DestinationList();
        TourList tours = new TourList();

        if (!destinations.loadFromFile(
                directory.resolve("destinations.txt").toString())
                || !tours.loadFromFile(
                directory.resolve("tours.txt").toString(), destinations)) {
            throw new AssertionError("Khong nap lai duoc du lieu kiem thu.");
        }
        return tours;
    }

    private static void check(String label, boolean condition) {
        if (!condition) {
            throw new AssertionError("[FAIL] " + label);
        }
        passed++;
        System.out.println("[PASS] " + label);
    }

    public static void main(String[] args) throws IOException {
        Path root = Files.createTempDirectory("truong-date-menu-");

        // Menu 8: nhập sai lựa chọn/ngày, sửa đúng, lưu rồi khởi động lại.
        Path addDirectory = seed(root, "add");
        String addOutput = runApp(addDirectory,
                "abc\n12\n8\nT04\nabc\n0\n4\n2\nNew Tour\n"
                        + "23/11/2026\n2026-02-30\n2028-02-29\n2800\n11\n0\n");

        TourList added = reload(addDirectory);
        int addedIndex = added.searchId("T04");
        String restartOutput = runApp(addDirectory, "0\n");

        check("Menu 8: nhap lai ngay sai, luu va khoi dong lai",
                added.size() == 4 && addedIndex >= 0
                        && added.get(addedIndex).getDestination()
                        .getDestinationId().equals("D02")
                        && added.get(addedIndex).getTourName().equals("New Tour")
                        && added.get(addedIndex).getDepartureDate()
                        .equals("2028-02-29")
                        && added.get(addedIndex).getPrice() == 2800
                        && addOutput.contains("Invalid departure date")
                        && addOutput.contains("Saved tours successfully.")
                        && restartOutput.contains(
                        "Loaded 3 destination(s) and 4 tour(s)."));

        // Menu 10: sửa Tour, đổi Destination và kiểm tra ngày nhuận.
        Path updateDirectory = seed(root, "update");
        String updateOutput = runApp(updateDirectory,
                "10\nT01\nT10\n3\nUpdated Tour\n2026-13-01\n"
                        + "1900-02-29\n2024-02-29\n4200\n11\n0\n");

        TourList updated = reload(updateDirectory);
        int updatedIndex = updated.searchId("T10");

        check("Menu 10: nhap lai ngay sai va luu cap nhat",
                updated.size() == 3
                        && updated.searchId("T01") == -1
                        && updatedIndex >= 0
                        && updated.get(updatedIndex).getDestination()
                        .getDestinationId().equals("D03")
                        && updated.get(updatedIndex).getTourName()
                        .equals("Updated Tour")
                        && updated.get(updatedIndex).getDepartureDate()
                        .equals("2024-02-29")
                        && updated.get(updatedIndex).getPrice() == 4200
                        && updateOutput.contains("Invalid departure date")
                        && updateOutput.contains("Saved tours successfully."));

        // Hết dữ liệu sau ngày sai khi thêm Tour.
        Path cancelAddDirectory = seed(root, "cancel-add");
        String beforeAdd = Files.readString(
                cancelAddDirectory.resolve("tours.txt"));

        String cancelAddOutput = runApp(cancelAddDirectory,
                "8\nT04\n1\nCancelled\n2026-02-30\n");

        check("Menu 8: het du lieu thi huy them an toan",
                cancelAddOutput.contains(
                        "Input cancelled. Tour was not added.")
                        && beforeAdd.equals(Files.readString(
                        cancelAddDirectory.resolve("tours.txt"))));

        // Hết dữ liệu sau ngày sai khi sửa Tour.
        Path cancelUpdateDirectory = seed(root, "cancel-update");
        String beforeUpdate = Files.readString(
                cancelUpdateDirectory.resolve("tours.txt"));

        String cancelUpdateOutput = runApp(cancelUpdateDirectory,
                "10\nT01\nT10\n2\nCancelled\n2026-02-30\n");

        check("Menu 10: het du lieu thi huy sua an toan",
                cancelUpdateOutput.contains(
                        "Input cancelled. Tour was not updated.")
                        && beforeUpdate.equals(Files.readString(
                        cancelUpdateDirectory.resolve("tours.txt"))));

        System.out.printf("RESULT: %d passed, 0 failed%n", passed);
        System.out.println("Test files: " + root);
    }
}