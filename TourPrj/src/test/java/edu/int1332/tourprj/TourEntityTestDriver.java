// Tên: Lê Đình Đông
// MSV: B23DCKD015
// Mô tả: Test driver cho việc II.2 - kiểm tra ràng buộc, Comparable và serialization của lớp Tour.

package edu.int1332.tourprj;

import java.util.ArrayList;
import java.util.Collections;

/** Standalone test driver for the Tour entity. */
public final class TourEntityTestDriver {
    private static int pass;
    private static int fail;

    private TourEntityTestDriver() { }

    private static void check(String label, boolean condition) {
        System.out.printf("[%s] %s%n", condition ? "PASS" : "FAIL", label);
        if (condition) { pass++; } else { fail++; }
    }

    private static boolean rejects(Runnable action) {
        try {
            action.run();
            return false;
        } catch (IllegalArgumentException expected) {
            return true;
        }
    }

    public static void main(String[] args) {
        Destination daNang = new Destination("D01", "Da Nang", "Central", 1_500_000);
        Destination haLong = new Destination("D02", "Ha Long", "North", 2_000_000);
        Tour tour = new Tour("T01", daNang, "Beach Discovery", "2027-01-15", 2_800_000);

        check("valid constructor keeps values", "T01".equals(tour.getTourId())
                && tour.getDestination() == daNang && tour.getPrice() == 2_800_000);
        check("setters trim text", "T9".equals(new Tour(" T9 ", daNang, "A", "2027-01-01", 0).getTourId()));
        check("price zero is allowed", new Tour("T0", daNang, "Free", "2027-01-01", 0).getPrice() == 0);
        check("blank ID rejected", rejects(() -> new Tour(" ", daNang, "A", "2027-01-01", 1)));
        check("null destination rejected", rejects(() -> new Tour("T1", null, "A", "2027-01-01", 1)));
        check("blank name rejected", rejects(() -> new Tour("T1", daNang, "", "2027-01-01", 1)));
        check("negative price rejected", rejects(() -> new Tour("T1", daNang, "A", "2027-01-01", -1)));
        check("non-finite price rejected", rejects(() -> new Tour("T1", daNang, "A", "2027-01-01", Double.NaN)));
        check("comma rejected", rejects(() -> new Tour("T1", daNang, "A,B", "2027-01-01", 1)));
        check("date validator accepts leap day", Tour.isValidDepartureDate("2028-02-29"));
        check("date validator rejects invalid day", !Tour.isValidDepartureDate("2026-02-30"));
        check("file serialization", "T01,D01,Beach Discovery,2027-01-15,2800000".equals(tour.toString()));
        check("screen text has destination and grouped price", tour.screenString().contains("Da Nang")
                && tour.screenString().contains("2,800,000"));

        ArrayList<Tour> list = new ArrayList<>();
        Tour a2 = new Tour("T02", daNang, "X", "2027-01-01", 1);
        Tour a1 = new Tour("T01", daNang, "X", "2027-01-01", 1);
        Tour b1 = new Tour("T00", haLong, "X", "2027-01-01", 1);
        list.add(b1);
        list.add(a2);
        list.add(a1);
        Collections.sort(list);
        check("sorts by destination then ID", list.get(0) == a1 && list.get(1) == a2 && list.get(2) == b1);
        check("incomplete Tour is safe", safeOnIncomplete());

        System.out.printf("RESULT: %d passed, %d failed%n", pass, fail);
        if (fail > 0) { System.exit(1); }
    }

    private static boolean safeOnIncomplete() {
        try {
            Tour empty = new Tour();
            empty.compareTo(new Tour());
            empty.toString();
            empty.screenString();
            return true;
        } catch (RuntimeException ex) {
            return false;
        }
    }
}
