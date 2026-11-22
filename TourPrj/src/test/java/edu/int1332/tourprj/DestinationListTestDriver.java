// Tên: Trần Minh Thịnh
// MSV: B23DCKD065
// Mô tả: Kiểm thử tự động chức năng đọc, ghi file và tìm kiếm ID của DestinationList.

package edu.int1332.tourprj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/** Standalone checks for DestinationList II.4 and its file contract. */
public final class DestinationListTestDriver {
    private static int passed;
    private static int failed;

    private DestinationListTestDriver() {
    }

    private static void check(String label, boolean condition) {
        System.out.printf("[%s] %s%n", condition ? "PASS" : "FAIL", label);
        if (condition) {
            passed++;
        } else {
            failed++;
        }
    }

    public static void main(String[] args) throws IOException {
        Path temp = Files.createTempDirectory("destination-list-test-");
        Path input = temp.resolve("destinations.txt");
        Files.write(input, Arrays.asList(
                "D01,Da Nang,Central,1500000",
                "D02,Ha Long,North,2000000",
                "D03,Da Lat,South,1800000"), StandardCharsets.UTF_8);

        DestinationList destinations = new DestinationList();
        check("load valid file",
                destinations.loadFromFile(input.toString()) && destinations.size() == 3);
        check("search existing ID", destinations.searchId("d02") == 1);
        check("search missing/null ID",
                destinations.searchId("D99") == -1 && destinations.searchId(null) == -1);
        int sizeBeforeInvalidPath = destinations.size();
        check("blank and invalid paths are rejected without clearing data",
                !destinations.loadFromFile(" ")
                        && !destinations.loadFromFile(String.valueOf('\u0000'))
                        && destinations.size() == sizeBeforeInvalidPath);

        Path output = temp.resolve("saved-destinations.txt");
        check("save file", destinations.saveToFile(output.toString()));
        DestinationList reloaded = new DestinationList();
        check("reload saved file",
                reloaded.loadFromFile(output.toString())
                        && reloaded.size() == 3
                        && reloaded.get(2).getDestinationId().equals("D03"));

        Path broken = temp.resolve("broken.txt");
        Files.write(broken, Arrays.asList(
                "D01,Valid,Central,100",
                "bad-line",
                "d01,Duplicate,North,200",
                "D02,Invalid cost,North,-1",
                "D03,Valid,South,300",
                "D04,Invalid cost,South,not-a-number",
                "D04,Valid after invalid row,South,400"), StandardCharsets.UTF_8);
        check("invalid rows reported while valid rows remain",
                !destinations.loadFromFile(broken.toString())
                        && destinations.size() == 3
                        && destinations.searchId("D03") == 1
                        && destinations.searchId("D04") == 2);

        int originalSize = destinations.size();
        check("missing file keeps current data",
                !destinations.loadFromFile(temp.resolve("missing.txt").toString())
                        && destinations.size() == originalSize);

        DestinationList containsNull = new DestinationList();
        containsNull.add(null);
        check("save rejects a null Destination",
                !containsNull.saveToFile(temp.resolve("invalid-save.txt").toString()));

        System.out.printf("RESULT: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            System.exit(1);
        }
    }
}
