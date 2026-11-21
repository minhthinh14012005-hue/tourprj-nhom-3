package edu.int1332.tourprj;

import java.util.ArrayList;

/**
 * Test driver cho cac phan do Dam Trong Dat phu trach:
 * - II.1: Class Destination (Encapsulation, Validation, toString ghi file)
 * - III.1: TourList.listTours (Sap xep theo destinationName roi tourId - Test 06)
 *          va TourList.printBasedDestinationName / findToursByDestinationName (Loc partial name - Test 07)
 */
public final class DestinationAndSearchTestDriver {
    private static int pass = 0;
    private static int fail = 0;

    private DestinationAndSearchTestDriver() {
    }

    private static void check(String label, boolean condition) {
        System.out.printf("[%s] %s%n", condition ? "PASS" : "FAIL", label);
        if (condition) {
            pass++;
        } else {
            fail++;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM THU PHAN CONG VIEC CUA DAM TRONG DAT ===");

        // --- 1. KIEM THU II.1: Destination & Encapsulation ---
        System.out.println("\n--- 1. Testing Destination (II.1) ---");
        Destination d1 = new Destination("D01", "Da Nang", "Central", 1500000);
        check("Destination getters return correct values",
                d1.getDestinationId().equals("D01")
                        && d1.getDestinationName().equals("Da Nang")
                        && d1.getRegion().equals("Central")
                        && d1.getBaseCost() == 1500000.0);

        check("Destination toString formats correctly for file saving",
                d1.toString().equals("D01,Da Nang,Central,1500000"));

        // Test setters
        d1.setDestinationName("Da Nang City");
        d1.setBaseCost(1750000);
        check("Destination setters update properly",
                d1.getDestinationName().equals("Da Nang City") && d1.getBaseCost() == 1750000.0);

        // Test validation: ID blank
        boolean idBlankRejected = false;
        try {
            new Destination("  ", "Valid Name", "Central", 1000);
        } catch (IllegalArgumentException e) {
            idBlankRejected = true;
        }
        check("Destination constructor rejects blank ID", idBlankRejected);

        // Test validation: Name blank
        boolean nameBlankRejected = false;
        try {
            new Destination("D04", "  ", "Central", 1000);
        } catch (IllegalArgumentException e) {
            nameBlankRejected = true;
        }
        check("Destination constructor rejects blank Name", nameBlankRejected);

        // Test validation: Region blank
        boolean regionBlankRejected = false;
        try {
            new Destination("D04", "Hue", "   ", 1000);
        } catch (IllegalArgumentException e) {
            regionBlankRejected = true;
        }
        check("Destination constructor rejects blank Region", regionBlankRejected);

        // Test validation: baseCost <= 0
        boolean zeroCostRejected = false;
        try {
            new Destination("D04", "Hue", "Central", 0);
        } catch (IllegalArgumentException e) {
            zeroCostRejected = true;
        }
        check("Destination constructor rejects baseCost = 0", zeroCostRejected);

        boolean negativeCostRejected = false;
        try {
            new Destination("D04", "Hue", "Central", -50000);
        } catch (IllegalArgumentException e) {
            negativeCostRejected = true;
        }
        check("Destination constructor rejects negative baseCost", negativeCostRejected);

        boolean nanCostRejected = false;
        try {
            d1.setBaseCost(Double.NaN);
        } catch (IllegalArgumentException e) {
            nanCostRejected = true;
        }
        check("Destination setBaseCost rejects NaN", nanCostRejected);


        // --- 2. KIEM THU III.1: TourList.listTours (Test 06) ---
        System.out.println("\n--- 2. Testing TourList.listTours (III.1 - Test 06) ---");
        TourList tours = new TourList();
        System.out.println("Calling listTours on empty list:");
        tours.listTours(); // Safe on empty

        Destination destHue = new Destination("D04", "Hue", "Central", 1200000);
        Destination destHanoi = new Destination("D05", "Ha Noi", "North", 2200000);
        Destination destDaLat = new Destination("D03", "Da Lat", "South", 1800000);

        // Them tours khong theo thu tu
        Tour t1 = new Tour("T05", destHue, "Hue Heritage", "2027-04-10", 2500000);
        Tour t2 = new Tour("T02", destDaLat, "Pine Forest", "2027-02-15", 3000000);
        Tour t3 = new Tour("T01", destDaLat, "Flower Festival", "2027-01-20", 3200000);
        Tour t4 = new Tour("T03", destHanoi, "Old Quarter", "2027-03-01", 2800000);

        tours.add(t1);
        tours.add(t2);
        tours.add(t3);
        tours.add(t4);

        System.out.println("\nTesting sorted listTours display:");
        tours.listTours();

        // Thu tu mong doi sau khi sort:
        // 1. Da Lat (T01 - do T01 < T02)
        // 2. Da Lat (T02)
        // 3. Ha Noi (T03)
        // 4. Hue (T05)
        check("listTours sorts primarily by destination name",
                tours.get(0).getDestination().getDestinationName().equals("Da Lat")
                        && tours.get(1).getDestination().getDestinationName().equals("Da Lat")
                        && tours.get(2).getDestination().getDestinationName().equals("Ha Noi")
                        && tours.get(3).getDestination().getDestinationName().equals("Hue"));

        check("listTours breaks tie by tour ID (T01 before T02)",
                tours.get(0).getTourId().equals("T01") && tours.get(1).getTourId().equals("T02"));


        // --- 3. KIEM THU III.1: Partial Search (Test 07) ---
        System.out.println("\n--- 3. Testing Partial destination search (III.1 - Test 07) ---");
        // Tim "da" -> khop voi "Da Lat"
        ArrayList<Tour> matchedDa = tours.findToursByDestinationName("da");
        check("findToursByDestinationName finds 'da' (case-insensitive)",
                matchedDa.size() == 2
                        && matchedDa.get(0).getTourId().equals("T01")
                        && matchedDa.get(1).getTourId().equals("T02"));

        // Tim "HUE" -> khop voi "Hue"
        ArrayList<Tour> matchedHue = tours.findToursByDestinationName("HUE");
        check("findToursByDestinationName finds 'HUE' (case-insensitive uppercase)",
                matchedHue.size() == 1 && matchedHue.get(0).getTourId().equals("T05"));

        // Tim khong co ket qua
        ArrayList<Tour> noMatch = tours.findToursByDestinationName("Saigon");
        check("findToursByDestinationName returns empty when no match", noMatch.isEmpty());

        // Tim tu khoa blank
        ArrayList<Tour> blankSearch = tours.findToursByDestinationName("   ");
        check("findToursByDestinationName returns empty for blank keyword", blankSearch.isEmpty());

        System.out.println("\nDisplaying search results for keyword 'da':");
        tours.printBasedDestinationName("da");

        System.out.println("Displaying search results for missing keyword 'Saigon':");
        tours.printBasedDestinationName("Saigon");

        // --- KET LUAN ---
        System.out.printf("%n=== KET QUA KIEM THU: %d PASS, %d FAIL ===%n", pass, fail);
        if (fail > 0) {
            System.exit(1);
        }
    }
}
