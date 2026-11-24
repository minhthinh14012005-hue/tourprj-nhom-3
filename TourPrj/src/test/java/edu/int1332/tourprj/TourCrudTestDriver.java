package edu.int1332.tourprj;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

public final class TourCrudTestDriver {
    private static int pass = 0;
    private static int fail = 0;

    private TourCrudTestDriver() {
    }

    private static void check(String label, boolean condition) {
        System.out.printf("[%s] %s%n", condition ? "PASS" : "FAIL", label);
        if (condition) {
            pass++;
        } else {
            fail++;
        }
    }

    private static final class StubDestinationList extends DestinationList {
        private final Queue<Destination> scriptedChoices = new LinkedList<>();

        void enqueueChoice(Destination destination) {
            scriptedChoices.add(destination);
        }

        @Override
        public Destination getUserChoice() {
            return scriptedChoices.isEmpty() ? null : scriptedChoices.poll();
        }
    }

    private static void feedInput(String... lines) {
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(System.lineSeparator());
        }
        System.setIn(new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8)));
    }

    public static void main(String[] args) {
        InputStream originalIn = System.in;
        try {
            StubDestinationList destinations = new StubDestinationList();
            Destination d01 = new Destination("D01", "Da Nang", "Central", 1500000);
            Destination d02 = new Destination("D02", "Ha Long", "North", 2000000);
            Destination d03 = new Destination("D03", "Da Lat", "South", 1800000);
            destinations.add(d01);
            destinations.add(d02);
            destinations.add(d03);

            TourList tours = new TourList();
            tours.add(new Tour("T01", d01, "Beach Discovery", "2027-01-15", 2800000));
            tours.add(new Tour("T02", d02, "Bay Cruise", "2027-02-12", 3500000));

            TourList emptyDestCheck = new TourList();
            emptyDestCheck.addTour(new DestinationList());
            check("T08.1 addTour rejects empty DestinationList", emptyDestCheck.isEmpty());

            destinations.enqueueChoice(d03);
            feedInput("", "T01", "t02", "T,04", "T04", "", "Bad,Name", "Flower Valley", "2027-04-01", "abc", "-500", "0");
            tours.addTour(destinations);
            int idxT04 = tours.searchId("T04");
            check("T08.2 addTour rejects invalid inputs then adds valid Tour",
                    idxT04 != -1
                            && tours.get(idxT04).getDestination() == d03
                            && tours.get(idxT04).getTourName().equals("Flower Valley")
                            && tours.get(idxT04).getPrice() == 0.0);

            int sizeBeforeBadRef = tours.size();
            destinations.enqueueChoice(new Destination("D99", "Unknown", "Nowhere", 100));
            feedInput("T05");
            tours.addTour(destinations);
            check("T08.3 addTour rejects destination not in DestinationList",
                    tours.size() == sizeBeforeBadRef && tours.searchId("T05") == -1);

            destinations.enqueueChoice(null);
            feedInput("T06");
            tours.addTour(destinations);
            check("T08.4 addTour aborts safely when getUserChoice returns null",
                    tours.size() == sizeBeforeBadRef && tours.searchId("T06") == -1);

            feedInput("T99");
            check("T09.1 removeTour returns false for missing ID",
                    !tours.removeTour() && tours.size() == 3);

            feedInput("t04");
            check("T09.2 removeTour removes existing ID (case-insensitive)",
                    tours.removeTour() && tours.size() == 2 && tours.searchId("T04") == -1);

            TourList emptyTours = new TourList();
            check("T09.3 removeTour returns false on empty list", !emptyTours.removeTour());

            feedInput("NO_SUCH_ID");
            check("T10.1 updateTour returns false for missing ID", !tours.updateTour(destinations));

            destinations.enqueueChoice(d03);
            feedInput("t01", "T02", "T01", "Da Lat Premium", "2027-05-15", "not-a-number", "-1", "4200000");
            boolean updated = tours.updateTour(destinations);
            Tour t01 = tours.get(tours.searchId("T01"));
            check("T10.2 updateTour blocks duplicate ID, allows keeping own ID, updates fields",
                    updated
                            && t01.getTourId().equals("T01")
                            && t01.getDestination() == d03
                            && t01.getTourName().equals("Da Lat Premium")
                            && t01.getDepartureDate().equals("2027-05-15")
                            && t01.getPrice() == 4200000.0);

            destinations.enqueueChoice(d01);
            feedInput("T01", "T10", "Da Nang Classic", "2027-06-01", "2900000");
            boolean renamed = tours.updateTour(destinations);
            check("T10.3 updateTour supports renaming to an unused ID",
                    renamed && tours.searchId("T01") == -1 && tours.searchId("T10") != -1);

            destinations.enqueueChoice(null);
            feedInput("T10", "T88");
            boolean abortedUpdate = tours.updateTour(destinations);
            check("T10.4 updateTour preserves old data if destination choice is invalid",
                    !abortedUpdate && tours.searchId("T10") != -1 && tours.searchId("T88") == -1);

            System.out.printf("RESULT: %d passed, %d failed%n", pass, fail);
            if (fail > 0) {
                System.exit(1);
            }
        } finally {
            System.setIn(originalIn);
        }
    }
}
