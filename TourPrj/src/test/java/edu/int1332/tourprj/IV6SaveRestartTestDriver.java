package edu.int1332.tourprj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Người thực hiện: Lê Thuần Trường.
 * Ngày thực hiện: 24/11/2026.
 * Mô tả: IV.6 / Test 11 - Lưu Tour và kiểm tra bằng một JVM mới.
 * Dùng dữ liệu tạm; tạo báo cáo và log trong docs/test-results.
 */
public final class IV6SaveRestartTestDriver {
    private static int passed;
    private static final List<String> results = new ArrayList<>();

    private IV6SaveRestartTestDriver() { }

    private static void check(String label, boolean condition) {
        if (condition) passed++;
        String status = condition ? "PASS" : "FAIL";
        results.add("| " + label + " | " + status + " |");
        System.out.println("[" + status + "] " + label);
    }

    private static Path projectRoot() throws IOException {
        Path directory = Paths.get("").toAbsolutePath();
        while (directory != null
                && !Files.exists(directory.resolve(".git"))) {
            directory = directory.getParent();
        }
        if (directory == null) {
            throw new IOException("Run this test inside the Git project.");
        }
        return directory;
    }

    private static Process runApp(Path data, Path input, Path log)
            throws IOException, InterruptedException {
        String executable = System.getProperty("os.name")
                .startsWith("Windows") ? "java.exe" : "java";

        String java = Paths.get(
                System.getProperty("java.home"), "bin", executable
        ).toString();

        Process process = new ProcessBuilder(
                java, "-Dfile.encoding=UTF-8", "-cp",
                System.getProperty("java.class.path"),
                TourManager.class.getName(),
                data.resolve("destinations.txt").toString(),
                data.resolve("tours.txt").toString()
        )
                .redirectInput(input.toFile())
                .redirectErrorStream(true)
                .redirectOutput(log.toFile())
                .start();

        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException(
                    "TourManager exceeded 30 seconds. See: " + log);
        }
        return process;
    }

    private static boolean matchesRecord(
            List<String> rows, String expected) {
        String[] wanted = expected.split(",", -1);

        for (String row : rows) {
            String[] actual = row.split(",", -1);
            if (actual.length == 5 && actual[0].equals(wanted[0])) {
                try {
                    for (int i = 0; i < 4; i++) {
                        if (!actual[i].equals(wanted[i])) return false;
                    }
                    return Double.compare(
                            Double.parseDouble(actual[4]),
                            Double.parseDouble(wanted[4])
                    ) == 0;
                } catch (NumberFormatException ex) {
                    return false;
                }
            }
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Path root = projectRoot();
        Path reports = Files.createDirectories(
                root.resolve("docs/test-results"));
        Path data = Files.createTempDirectory("truong-iv6-test11-");

        List<String> original = Arrays.asList(
                "T01,D01,Beach,2027-01-15,2800000",
                "T02,D02,Bay,2027-02-12,3500000",
                "T03,D03,City,2027-03-10,3100000"
        );
        String newTour =
                "T04,D02,IV6 Save Restart,2028-02-29,4500000";

        Files.write(data.resolve("destinations.txt"), Arrays.asList(
                "D01,Da Nang,Central,1500000",
                "D02,Ha Long,North,2000000",
                "D03,Hue,Central,1000000"
        ), StandardCharsets.UTF_8);

        Path tourFile = data.resolve("tours.txt");
        Files.write(tourFile, original, StandardCharsets.UTF_8);

        // Tiến trình Java thứ nhất: thêm, lưu rồi thoát.
        Path saveInput = data.resolve("save-input.txt");
        Files.write(saveInput, Arrays.asList(
                "8", "T04", "2", "IV6 Save Restart",
                "2028-02-29", "4500000", "11", "0"
        ), StandardCharsets.UTF_8);

        Path saveLog = reports.resolve("IV6_Test11_Run1_Save.txt");
        Process first = runApp(data, saveInput, saveLog);
        String saveOutput = Files.readString(
                saveLog, StandardCharsets.UTF_8);
        byte[] savedBytes = Files.readAllBytes(tourFile);
        List<String> saved = Files.readAllLines(
                tourFile, StandardCharsets.UTF_8);

        Set<String> ids = new HashSet<>();
        for (String row : saved) {
            ids.add(row.split(",", -1)[0]);
        }

        check("JVM 1: menu 11 saves successfully and exits with code 0",
                first.exitValue() == 0
                        && saveOutput.contains("Saved tours successfully."));

        check("Saved file contains 4 unique Tour IDs",
                saved.size() == 4 && ids.size() == 4);

        boolean originalsMatch = true;
        for (String expected : original) {
            originalsMatch &= matchesRecord(saved, expected);
        }
        check("All 3 original Tours retain all 5 fields",
                originalsMatch);

        check("New Tour T04 retains all 5 expected fields",
                matchesRecord(saved, newTour));

        // JVM mới: nạp file, xem Tour đã lưu rồi hủy sửa bằng EOF.
        Path restartInput = data.resolve("restart-input.txt");
        Files.write(restartInput, Arrays.asList(
                "3", "D02", "10", "T04"
        ), StandardCharsets.UTF_8);

        Path restartLog = reports.resolve(
                "IV6_Test11_Run2_Restart.txt");
        Process second = runApp(data, restartInput, restartLog);
        String restartOutput = Files.readString(
                restartLog, StandardCharsets.UTF_8);

        check("JVM 2: fresh startup loads and displays saved Tour T04",
                second.exitValue() == 0
                        && restartOutput.contains(
                        "Loaded 3 destination(s) and 4 tour(s).")
                        && restartOutput.contains("D02 | Ha Long | North")
                        && restartOutput.matches(
                        "(?s).*Current tour:\\s*T04\\s+Ha Long"
                                + "\\s+IV6 Save Restart\\s+2028-02-29\\s+.*")
                        && restartOutput.contains(
                        "Input cancelled. Tour was not updated."));

        check("Restart leaves tours.txt byte-for-byte unchanged",
                Arrays.equals(savedBytes, Files.readAllBytes(tourFile)));

        int failed = results.size() - passed;
        List<String> report = new ArrayList<>(Arrays.asList(
                "# IV.6 - Test 11: lưu Tour và chạy lại chương trình",
                "",
                "- Người thực hiện: Lê Thuần Trường.",
                "- Ngày thực hiện: 24/11/2026.",
                "- Reviewer theo Plan: Nguyễn Hữu Đức (chờ review).",
                "- Runtime Java: " + System.getProperty("java.version"),
                "- JVM lưu: PID " + first.pid()
                        + ", exit code " + first.exitValue(),
                "- JVM chạy lại: PID " + second.pid()
                        + ", exit code " + second.exitValue(),
                "- Dữ liệu thử: " + data,
                "",
                "## Kịch bản",
                "",
                "1. Tạo dữ liệu tạm gồm 3 Destination và 3 Tour.",
                "2. JVM thứ nhất: thêm T04 qua menu 8, lưu qua menu 11, thoát qua menu 0.",
                "3. Đối chiếu 3 bản ghi cũ và đầy đủ 5 trường của T04 trong file.",
                "4. JVM mới: nạp lại file, tìm D02, xem T04 rồi hủy sửa bằng EOF.",
                "5. Xác nhận file không thay đổi sau lần chạy lại.",
                "",
                "## Kết quả",
                "",
                "| Kiểm tra | Kết quả |",
                "|---|---|"
        ));

        report.addAll(results);
        report.add("\nTổng: " + passed + " passed, "
                + failed + " failed.\n");

        report.add(failed == 0
                ? "Bug quan sát: chưa ghi nhận trong phạm vi Test 11."
                : "Có kiểm tra thất bại; cần xem log và xử lý.");

        report.add("\n## Dữ liệu sau khi lưu\n");
        for (String row : saved) {
            report.add("    " + row);
        }

        report.add("\n## Log tiến trình lưu\n");
        saveOutput.lines().forEach(line -> report.add(line.isEmpty() ? "" : "    " + line));

        report.add("\n## Log tiến trình chạy lại\n");
        restartOutput.lines().forEach(line -> report.add(line.isEmpty() ? "" : "    " + line));

        Path reportFile = reports.resolve("IV6_Test11_SaveRestart.md");
        Files.write(reportFile, report, StandardCharsets.UTF_8);

        System.out.println("JVM 1 PID: " + first.pid()
                + "; JVM 2 PID: " + second.pid());

        restartOutput.lines()
                .filter(line -> line.contains("Loaded ")
                        || line.contains("Current tour:"))
                .forEach(System.out::println);

        System.out.printf("RESULT: %d passed, %d failed%n", passed, failed);
        System.out.println("Report: " + reportFile);
        System.out.println("Test data: " + data);

        if (failed > 0) {
            throw new AssertionError("Test 11 failed. See report and logs.");
        }
    }
}