// Tên: Nguyễn Hữu Đức
// MSV: B23DCKD017
// Mô tả: Kiểm thử phục hồi nhập sai, Test 04 cập nhật Destination và Test 06 sắp xếp Tour.

package edu.int1332.tourprj;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public final class DucAssignedWorkTestDriver {
    private static int passed;
    private static int failed;

    private DucAssignedWorkTestDriver() { }

    public static void main(String[] args) throws Exception {
        testMenuRecoveryAndGenericSelection();
        test04UpdateDestination();
        test06SortTours();
        testManagerInvalidChoiceAndExitWithoutSave();
        testMissingFileDoesNotCrash();
        System.out.printf("Duc assigned tests: %d passed, %d failed.%n", passed, failed);
        if (failed != 0) {
            throw new AssertionError("Some assigned tests failed.");
        }
    }

    private static void testMenuRecoveryAndGenericSelection() {
        ArrayList<String> values = new ArrayList<>();
        values.add("North");
        values.add("Central");
        values.add("South");
        withInput("text\n0\n2\n", () ->
                check("Menu retries text/out-of-range input", Menu.intGetChoice(values) == 2));
        withInput("3\n", () ->
                check("refGetChoice returns selected object", "South".equals(Menu.refGetChoice(values))));
        withInput("bad\n-1\n125.5\n", () ->
                check("Numeric input retries NumberFormatException", Menu.readNonNegativeDouble("Price: ") == 125.5));
    }

    private static void test04UpdateDestination() {
        DestinationList list = new DestinationList();
        Destination original = new Destination("D01", "Da Nang", "Central", 1000);
        list.add(original);
        withInput("D01\nD01\nDa Nang Updated\nCentral Coast\n2500\n", () -> {
            boolean updated = list.updateDestination();
            check("Test 04 update returns true", updated);
            check("Test 04 keeps same object reference", list.get(0) == original);
            check("Test 04 updates all fields",
                    "Da Nang Updated".equals(original.getDestinationName())
                            && "Central Coast".equals(original.getRegion())
                            && original.getBaseCost() == 2500);
        });
    }

    private static void test06SortTours() {
        Destination daNang = new Destination("D01", "Da Nang", "Central", 1000);
        Destination haNoi = new Destination("D02", "Ha Noi", "North", 1200);
        TourList tours = new TourList();
        tours.add(new Tour("T09", haNoi, "Hanoi 2", "2026-12-09", 900));
        tours.add(new Tour("T03", daNang, "Danang 3", "2026-12-03", 700));
        tours.add(new Tour("T01", daNang, "Danang 1", "2026-12-01", 500));
        captureOutput(tours::listTours);
        check("Test 06 sorts destination name first",
                "T01".equals(tours.get(0).getTourId()) && "T03".equals(tours.get(1).getTourId()));
        check("Test 06 tie-breaks repeated destination by tour ID",
                "T09".equals(tours.get(2).getTourId()));
    }

    private static void testManagerInvalidChoiceAndExitWithoutSave() throws Exception {
        Path folder = Files.createTempDirectory("duc-tour-manager-");
        Path destinations = folder.resolve("destinations.txt");
        Path tours = folder.resolve("tours.txt");
        Files.write(destinations, "D01,Da Nang,Central,1000\n".getBytes(StandardCharsets.UTF_8));
        Files.write(tours, "T01,D01,Weekend,2026-12-01,500\n".getBytes(StandardCharsets.UTF_8));
        byte[] beforeDestinations = Files.readAllBytes(destinations);
        byte[] beforeTours = Files.readAllBytes(tours);
        String output = withInputAndCapturedOutput("abc\n99\n0\n",
                () -> TourManager.main(new String[] { destinations.toString(), tours.toString() }));
        check("Manager retries invalid main-menu choices",
                count(output, "Invalid choice.") >= 2);
        check("Option 0 exits without implicit save",
                output.contains("No implicit save")
                        && java.util.Arrays.equals(beforeDestinations, Files.readAllBytes(destinations))
                        && java.util.Arrays.equals(beforeTours, Files.readAllBytes(tours)));
        Files.deleteIfExists(tours);
        Files.deleteIfExists(destinations);
        Files.deleteIfExists(folder);
    }

    private static void testMissingFileDoesNotCrash() {
        DestinationList list = new DestinationList();
        check("Missing destination file returns false", !list.loadFromFile("missing-duc-test-file.txt"));
    }

    private static void withInput(String text, Runnable action) {
        InputStream original = System.in;
        try {
            System.setIn(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
            action.run();
        } finally {
            System.setIn(original);
        }
    }

    private static String withInputAndCapturedOutput(String text, ThrowingRunnable action) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(bytes, true, StandardCharsets.UTF_8.name()));
            action.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return bytes.toString(StandardCharsets.UTF_8.name());
    }

    private static String captureOutput(Runnable action) {
        PrintStream original = System.out;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(bytes));
            action.run();
        } finally {
            System.setOut(original);
        }
        return bytes.toString();
    }

    private static int count(String text, String token) {
        int result = 0;
        int offset = 0;
        while ((offset = text.indexOf(token, offset)) >= 0) {
            result++;
            offset += token.length();
        }
        return result;
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }
}
