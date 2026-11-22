// Tên: Nguyễn Hữu Đức
// MSV: B23DCKD017
// Mô tả: Điều phối nạp dữ liệu và menu chính 0-11; chỉ lưu khi người dùng chọn 5 hoặc 11.

package edu.int1332.tourprj;

/** Application entry point and menu loop. */
public final class TourManager {
    private TourManager() { }

    public static void main(String[] args) {
        String destinationFile = args.length > 0 ? args[0] : "data/destinations.txt";
        String tourFile = args.length > 1 ? args[1] : "data/tours.txt";

        DestinationList destinations = new DestinationList();
        TourList tours = new TourList();
        boolean destinationsOk = destinations.loadFromFile(destinationFile);
        boolean toursOk = tours.loadFromFile(tourFile, destinations);
        System.out.printf("Loaded %d destination(s) and %d tour(s).%n", destinations.size(), tours.size());
        if (!destinationsOk || !toursOk) {
            System.out.println("Warning: one or more input lines/files were invalid; valid records remain available.");
        }

        while (true) {
            printMenu();
            final int choice;
            try {
                choice = Menu.readIntInRange("Your choice (0-11): ", 0, 11);
            } catch (IllegalStateException ex) {
                System.out.println("Input ended. Exiting without implicit save.");
                return;
            }

            try {
                switch (choice) {
                    case 1:
                        destinations.listDestinations();
                        break;
                    case 2:
                        destinations.addDestination();
                        break;
                    case 3:
                        showDestinationById(destinations);
                        break;
                    case 4:
                        destinations.updateDestination();
                        break;
                    case 5:
                        printSaveResult("destinations", destinations.saveToFile(destinationFile));
                        break;
                    case 6:
                        tours.listTours();
                        break;
                    case 7:
                        tours.printBasedDestinationName();
                        break;
                    case 8:
                        tours.addTour(destinations);
                        break;
                    case 9:
                        tours.removeTour();
                        break;
                    case 10:
                        tours.updateTour(destinations);
                        break;
                    case 11:
                        printSaveResult("tours", tours.saveToFile(tourFile));
                        break;
                    case 0:
                        System.out.println("Exit selected. No implicit save was performed.");
                        return;
                    default:
                        throw new IllegalStateException("Unreachable menu choice.");
                }
            } catch (IllegalStateException ex) {
                System.out.println("Operation stopped: " + ex.getMessage());
            } catch (RuntimeException ex) {
                System.out.println("Operation failed: " + ex.getMessage());
            }
        }
    }

    private static void showDestinationById(DestinationList destinations) {
        String id = Menu.readRequiredText("Enter destination ID: ");
        int index = destinations.searchId(id);
        if (index < 0) {
            System.out.println("Destination ID '" + id + "' not found.");
        } else {
            Destination item = destinations.get(index);
            System.out.printf("%s | %s | %s | %.2f%n", item.getDestinationId(),
                    item.getDestinationName(), item.getRegion(), item.getBaseCost());
        }
    }

    private static void printSaveResult(String label, boolean success) {
        System.out.println(success ? "Saved " + label + " successfully." : "Could not save " + label + ".");
    }

    private static void printMenu() {
        System.out.println("\n========== TOUR MANAGEMENT ==========");
        System.out.println("1. List destinations");
        System.out.println("2. Add destination");
        System.out.println("3. Search destination by ID");
        System.out.println("4. Update destination");
        System.out.println("5. Save destinations");
        System.out.println("6. List tours sorted");
        System.out.println("7. Search tours by destination name");
        System.out.println("8. Add tour");
        System.out.println("9. Remove tour");
        System.out.println("10. Update tour");
        System.out.println("11. Save tours");
        System.out.println("0. Exit without implicit save");
    }
}
