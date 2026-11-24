package edu.int1332.tourprj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Người thực hiện: Lê Thuần Trường.
 * Ngày thực hiện: 24/11/2026.
 * Mô tả: II.6 - Kiểm thử thêm, sửa, liệt kê và chọn Destination
 * với Menu thực tế.
 */
public final class DestinationCrudTestDriver {
    private static int passed;
    private static final List<String> results = new ArrayList<>();

    private DestinationCrudTestDriver() { }

    private static String withInput(String input, Runnable action) {
        InputStream previousIn = System.in;
        PrintStream previousOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream captured =
                     new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setIn(new ByteArrayInputStream(
                    input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(captured);
            action.run();
        } finally {
            System.setIn(previousIn);
            System.setOut(previousOut);
        }
        return output.toString(StandardCharsets.UTF_8);
    }

    private static void check(String label, boolean condition) {
        if (condition) passed++;
        String status = condition ? "PASS" : "FAIL";
        results.add("[" + status + "] " + label);
        System.out.println(results.get(results.size() - 1));
    }

    public static void main(String[] args) throws Exception {
        DestinationList empty = new DestinationList();

        String emptyOutput = withInput("", empty::listDestinations);
        check("List empty DestinationList safely",
                empty.isEmpty() && emptyOutput.contains("rong"));

        Destination[] emptyChoice = {null};
        withInput("", () -> emptyChoice[0] = empty.getUserChoice());
        check("Empty DestinationList selection returns null",
                emptyChoice[0] == null);

        Destination d01 =
                new Destination("D01", "Da Nang", "Central", 1500000);
        Destination d02 =
                new Destination("D02", "Ha Long", "North", 2000000);
        Destination d03 =
                new Destination("D03", "Hue", "Central", 1000000);

        DestinationList destinations = new DestinationList();
        destinations.addAll(Arrays.asList(d01, d02, d03));

        Tour linkedTour =
                new Tour("T01", d01, "Beach", "2027-01-15", 2800000);

        withInput(
                "\nd01\nD04\n\nBad,Name\nHue South\n\nBad,Region\nCentral\n"
                        + "abc\n0\n-1\nNaN\nInfinity\n1000000\n",
                destinations::addDestination
        );

        int addedIndex = destinations.searchId("D04");
        check("Add rejects invalid inputs then adds a valid Destination",
                destinations.size() == 4 && addedIndex >= 0
                        && destinations.get(addedIndex).getDestinationName()
                        .equals("Hue South")
                        && destinations.get(addedIndex).getRegion()
                        .equals("Central")
                        && destinations.get(addedIndex).getBaseCost() == 1000000);

        String listing = withInput("", destinations::listDestinations);
        check("List shows all 4 records without modifying the list",
                listing.contains("D01") && listing.contains("D02")
                        && listing.contains("D03") && listing.contains("D04")
                        && destinations.size() == 4
                        && destinations.get(0) == d01);

        boolean[] changed = {true};
        withInput("D99\n",
                () -> changed[0] = destinations.updateDestination());

        check("Update missing ID returns false and keeps existing data",
                !changed[0] && destinations.size() == 4
                        && d01.getDestinationId().equals("D01")
                        && d01.getDestinationName().equals("Da Nang")
                        && d01.getBaseCost() == 1500000);

        withInput(
                "d01\nD01\nDa Nang City\nCentral\n1600000\n",
                () -> changed[0] = destinations.updateDestination()
        );

        check("Update allows keeping own ID and retains the same object",
                changed[0] && destinations.get(0) == d01
                        && d01.getDestinationId().equals("D01")
                        && d01.getDestinationName().equals("Da Nang City")
                        && d01.getBaseCost() == 1600000
                        && linkedTour.getDestination() == d01);

        withInput(
                "D01\nD02\nD05\nDa Nang Premium\nCentral\n1700000\n",
                () -> changed[0] = destinations.updateDestination()
        );

        check("Update rejects duplicate ID and preserves Tour reference",
                changed[0] && destinations.searchId("D01") == -1
                        && destinations.searchId("D05") == 0
                        && linkedTour.getDestination() == d01
                        && linkedTour.getDestination().getDestinationId()
                        .equals("D05")
                        && d01.getDestinationName().equals("Da Nang Premium")
                        && d01.getBaseCost() == 1700000
                        && destinations.get(1) == d02);

        Destination[] selected = {null};
        String[] confirmation = {null};

        withInput("abc\n0\n5\n2\nOK\n", () -> {
            selected[0] = destinations.getUserChoice();
            confirmation[0] = Menu.readLine("Confirm: ");
        });

        check("Real Menu retries invalid choices and returns selected object",
                selected[0] == d02);

        check("Shared input stays usable and selection keeps list unchanged",
                "OK".equals(confirmation[0]) && destinations.size() == 4
                        && destinations.get(0) == d01
                        && destinations.get(1) == d02
                        && destinations.get(2) == d03);

        Path data = Files.createTempDirectory("truong-ii6-menu-");
        Path destinationFile = data.resolve("destinations.txt");
        Path tourFile = data.resolve("tours.txt");

        Files.write(destinationFile, Arrays.asList(
                "D01,Da Nang,Central,1500000",
                "D02,Ha Long,North,2000000",
                "D03,Hue,Central,1000000"
        ), StandardCharsets.UTF_8);

        Files.write(tourFile, Arrays.asList(
                "T01,D01,Beach,2027-01-15,2800000",
                "T02,D02,Bay,2027-02-12,3500000",
                "T03,D03,City,2027-03-10,3100000"
        ), StandardCharsets.UTF_8);

        String menuOutput = withInput(
                "1\n2\nd01\nD04\nHue South\nCentral\n1000000\n"
                        + "4\nD01\nD02\nD05\nDa Nang City\nCentral\n1600000\n"
                        + "5\n11\n0\n",
                () -> TourManager.main(new String[]{
                        destinationFile.toString(), tourFile.toString()
                })
        );

        DestinationList reloaded = new DestinationList();
        TourList reloadedTours = new TourList();
        boolean loaded = reloaded.loadFromFile(destinationFile.toString())
                && reloadedTours.loadFromFile(tourFile.toString(), reloaded);

        int renamedIndex = reloaded.searchId("D05");
        int linkedIndex = reloadedTours.searchId("T01");

        check("Real menus add/update/save correctly and preserve Tour linkage",
                loaded && reloaded.size() == 4 && reloadedTours.size() == 3
                        && reloaded.searchId("D01") == -1
                        && reloaded.searchId("D04") >= 0
                        && renamedIndex >= 0 && linkedIndex >= 0
                        && reloaded.get(renamedIndex).getDestinationName()
                        .equals("Da Nang City")
                        && reloaded.get(renamedIndex).getBaseCost() == 1600000
                        && reloadedTours.get(linkedIndex).getDestination()
                        == reloaded.get(renamedIndex)
                        && menuOutput.contains("Saved destinations successfully.")
                        && menuOutput.contains("Saved tours successfully."));

        int failed = results.size() - passed;
        Path root = Paths.get("").toAbsolutePath();
        while (root != null && !Files.exists(root.resolve(".git"))) {
            root = root.getParent();
        }
        if (root == null) {
            throw new IllegalStateException("Run inside the Git project.");
        }

        Path report = Files.createDirectories(
                root.resolve("docs/test-results")
        ).resolve("II6_Destination_Test_Log.txt");

        List<String> log = new ArrayList<>(Arrays.asList(
                "Người thực hiện: Lê Thuần Trường",
                "Ngày thực hiện: 24/11/2026",
                "Phạm vi: II.6",
                "Reviewer theo Plan: Đàm Trọng Đạt - chờ review",
                "Menu/TourManager: Nguyễn Hữu Đức, PR #5.",
                "Kiểm thử lựa chọn Destination bằng Menu thực tế.",
                "Runtime Java: " + System.getProperty("java.version"),
                ""
        ));

        log.addAll(results);
        log.add("RESULT: " + passed + " passed, " + failed + " failed");
        log.add("Test data: " + data);
        log.add("\nTourManager integration transcript:");
        log.add(menuOutput.stripTrailing());
        Files.write(report, log, StandardCharsets.UTF_8);

        System.out.printf("RESULT: %d passed, %d failed%n", passed, failed);
        System.out.println("Report: " + report);

        if (failed > 0) {
            throw new AssertionError("II.6 tests failed. See report.");
        }
    }
}