package edu.int1332.tourprj;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * TestDriver phan do Vu Huu Thinh phu trach:
 */

/**
 * Standalone test driver: no JUnit, no changes to other owners' Java classes.
 */
public final class TourListTestDriver {
        private static int pass = 0;
        private static int fail = 0;

        private TourListTestDriver() {
        }

        private static void check(String label, boolean condition) {
                System.out.printf("[%s] %s%n", condition ? "PASS" : "FAIL", label);
                if (condition)
                        pass++;
                else
                        fail++;
        }

        public static void main(String[] args) throws IOException {
                Path temp = Files.createTempDirectory("thinh-tour-test-");
                DestinationList destinations = new DestinationList();
                destinations.add(new Destination("D01", "Da Nang", "Central", 1500000));
                destinations.add(new Destination("D02", "Ha Long", "North", 2000000));
                destinations.add(new Destination("D03", "Da Lat", "South", 1800000));

                Path input = temp.resolve("tours.txt");
                Files.write(input, Arrays.asList(
                                "T01,D01,Beach Discovery,2027-01-15,2800000",
                                "T02,D02,Bay Cruise,2027-02-12,3500000",
                                "T03,D03,Flower City,2027-03-10,3100000"), StandardCharsets.UTF_8);

                TourList tours = new TourList();
                check("load 3 records", tours.loadFromFile(input.toString(), destinations) && tours.size() == 3);
                check("loaded association points to Destination object",
                                tours.get(0).getDestination() == destinations.get(0));
                check("searchId finds ID (case-insensitive)", tours.searchId("t02") == 1);
                check("searchId missing ID returns -1", tours.searchId("NO_ID") == -1);
                check("searchId null returns -1", tours.searchId(null) == -1);
                check("price range inclusive", tours.findToursByPriceRange(2800000, 3100000).size() == 2);
                check("price range no match", tours.findToursByPriceRange(0, 100).isEmpty());
                boolean badRangeRejected = false;
                try {
                        tours.findToursByPriceRange(100, 10);
                } catch (IllegalArgumentException expected) {
                        badRangeRejected = true;
                }
                check("invalid price interval rejected", badRangeRejected);

                Path output = temp.resolve("saved-tours.txt");
                check("save succeeds", tours.saveToFile(output.toString()));
                TourList reloaded = new TourList();
                check("reload matches original", reloaded.loadFromFile(output.toString(), destinations)
                                && reloaded.size() == 3 && reloaded.get(1).getTourId().equals("T02"));

                ArrayList<Tour> sorted = new ArrayList<>(tours);
                Collections.sort(sorted);
                check("compareTo sorts by destination name",
                                sorted.get(0).getDestination().getDestinationName().equals("Da Lat")
                                                && sorted.get(1).getDestination().getDestinationName()
                                                                .equals("Da Nang"));
                sorted.add(new Tour("T00", destinations.get(1), "Test", "2027-03-20", 0));
                Collections.sort(sorted);
                check("compareTo tie break by tour ID", sorted.get(2).getTourId().equals("T00")
                                && sorted.get(3).getTourId().equals("T02"));

                Path broken = temp.resolve("broken.txt");
                Files.write(broken, Arrays.asList(
                                "T01,D01,OK,2027-01-15,10",
                                "bad-line",
                                "T02,D99,Wrong reference,2027-02-12,100",
                                "T01,D01,Duplicate,2027-01-16,200",
                                "T04,D02,Bad price,2027-03-10,-1",
                                "T05,D02,OK,2027-03-10,0"), StandardCharsets.UTF_8);
                check("invalid rows reported and valid rows retained",
                                !tours.loadFromFile(broken.toString(), destinations)
                                                && tours.size() == 2 && tours.searchId("T05") == 1);
                int originalSize = tours.size();
                check("missing file does not clear current data",
                                !tours.loadFromFile(temp.resolve("missing.txt").toString(), destinations)
                                                && tours.size() == originalSize);

                System.out.printf("RESULT: %d passed, %d failed%n", pass, fail);
                System.out.println("Temporary test files: " + temp);
                if (fail > 0)
                        System.exit(1);
        }
}
