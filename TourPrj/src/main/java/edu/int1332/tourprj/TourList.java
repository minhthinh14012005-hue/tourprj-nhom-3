package edu.int1332.tourprj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Danh sach Tour. Phan do Vu Huu Thinh phu trach:
 * loadFromFile, saveToFile, searchId va loc theo khoang gia.
 * Cac phuong thuc CRUD/hien thi van la skeleton do cac thanh vien khac thuc
 * hien.
 */
public class TourList extends ArrayList<Tour> {
    /**
     * Nap Tour theo schema: tourID,destinationID,tourName,departureDate,price.
     * Tra false neu thieu file / co dong sai / loi I/O; cac dong hop le van duoc
     * nap.
     * Neu file khong the mo, danh sach hien tai duoc giu nguyen.
     * Neu file mo duoc, danh sach duoc thay bang cac dong hop le trong file.
     */
    public boolean loadFromFile(String filename, DestinationList destinations) {
        if (filename == null || filename.trim().isEmpty() || destinations == null) {
            System.err.println("Cannot load tours: filename or destination list is invalid.");
            return false;
        }
        Path path = Paths.get(filename);
        ArrayList<Tour> loaded = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        boolean allValid = true;
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    System.err.printf("%s:%d: empty line; skipped.%n", filename, lineNumber);
                    allValid = false;
                    continue;
                }
                String[] fields = line.split(",", -1);
                if (fields.length != 5) {
                    System.err.printf("%s:%d: expected 5 fields, got %d; skipped.%n",
                            filename, lineNumber, fields.length);
                    allValid = false;
                    continue;
                }
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }
                String id = fields[0];
                String destinationId = fields[1];
                String name = fields[2];
                String departureDate = fields[3];
                String priceText = fields[4];
                if (id.isEmpty() || destinationId.isEmpty() || name.isEmpty()
                        || departureDate.isEmpty() || priceText.isEmpty()) {
                    System.err.printf("%s:%d: required field is blank; skipped.%n", filename, lineNumber);
                    allValid = false;
                    continue;
                }
                String idKey = id.toLowerCase(java.util.Locale.ROOT);
                if (seenIds.contains(idKey)) {
                    System.err.printf("%s:%d: duplicate tour ID '%s'; skipped.%n", filename, lineNumber, id);
                    allValid = false;
                    continue;
                }
                Destination destination = null;
                for (Destination candidate : destinations) {
                    if (candidate != null && candidate.getDestinationId() != null
                            && candidate.getDestinationId().equalsIgnoreCase(destinationId)) {
                        destination = candidate;
                        break;
                    }
                }
                if (destination == null) {
                    System.err.printf("%s:%d: unknown destination ID '%s'; skipped.%n",
                            filename, lineNumber, destinationId);
                    allValid = false;
                    continue;
                }
                double price;
                try {
                    price = Double.parseDouble(priceText);
                } catch (NumberFormatException ex) {
                    System.err.printf("%s:%d: invalid price '%s'; skipped.%n",
                            filename, lineNumber, priceText);
                    allValid = false;
                    continue;
                }
                if (!Double.isFinite(price) || price < 0) {
                    System.err.printf("%s:%d: price must be finite and nonnegative; skipped.%n", filename, lineNumber);
                    allValid = false;
                    continue;
                }
                loaded.add(new Tour(id, destination, name, departureDate, price));
                seenIds.add(idKey);
            }
        } catch (IOException | java.nio.file.InvalidPathException ex) {
            System.err.printf("Cannot read %s: %s%n", filename, ex.getMessage());
            return false;
        }
        clear();
        addAll(loaded);
        return allValid;
    }

    /** Ghi Tour theo thu tu hien tai, khong tu dong ghi khi thoat chuong trinh. */
    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Cannot save tours: filename is blank.");
            return false;
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filename), StandardCharsets.UTF_8)) {
            for (Tour tour : this) {
                if (tour == null || tour.getDestination() == null) {
                    throw new IOException("Tour or linked destination is null");
                }
                writer.write(tour.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException | java.nio.file.InvalidPathException ex) {
            System.err.printf("Cannot save %s: %s%n", filename, ex.getMessage());
            return false;
        }
    }

    /** Tra ve index hoac -1. So sanh gia tri String, khong dung ==. */
    public int searchId(String id) {
        if (id == null || id.trim().isEmpty())
            return -1;
        for (int i = 0; i < size(); i++) {
            Tour tour = get(i);
            if (tour != null && tour.getTourId() != null
                    && tour.getTourId().equalsIgnoreCase(id.trim())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Mo rong III.5: loc Tour theo gia tu minPrice den maxPrice (bao gom hai can).
     * Chi tra danh sach moi, khong thay doi thu tu hay du lieu danh sach goc.
     * Giu menu 0-11; module Menu/TourManager co the them lua chon rieng sau.
     */
    public ArrayList<Tour> findToursByPriceRange(double minPrice, double maxPrice) {
        if (!Double.isFinite(minPrice) || !Double.isFinite(maxPrice)
                || minPrice < 0 || maxPrice < minPrice) {
            throw new IllegalArgumentException("Price range must satisfy 0 <= min <= max, and be finite");
        }
        ArrayList<Tour> matches = new ArrayList<>();
        for (Tour tour : this) {
            if (tour != null && Double.isFinite(tour.getPrice())
                    && tour.getPrice() >= minPrice && tour.getPrice() <= maxPrice) {
                matches.add(tour);
            }
        }
        return matches;
    }

    private static InputStream boundInputStream;
    private static Scanner inputScanner;

    public void addTour(DestinationList destinations) {
        if (destinations == null || destinations.isEmpty()) {
            System.out.println("Cannot add tour: destination list is empty. Please add a destination first.");
            return;
        }

        String id;
        while (true) {
            id = readRequiredText("Enter tour ID: ");
            if (id == null) {
                System.out.println("Input cancelled. Tour was not added.");
                return;
            }
            if (searchId(id) == -1) {
                break;
            }
            System.out.println("Tour ID already exists. Please enter another ID.");
        }

        System.out.println("Select a destination for this tour:");
        Destination selected = resolveExistingDestination(destinations, destinations.getUserChoice());
        if (selected == null) {
            System.out.println("Invalid or missing destination selection. Tour was not added.");
            return;
        }

        String name = readRequiredText("Enter tour name: ");
        if (name == null) {
            System.out.println("Input cancelled. Tour was not added.");
            return;
        }

        String departureDate = readRequiredText("Enter departure date (yyyy-MM-dd): ");
        if (departureDate == null) {
            System.out.println("Input cancelled. Tour was not added.");
            return;
        }

        Double price = readNonNegativePrice("Enter price (>= 0): ");
        if (price == null) {
            System.out.println("Input cancelled. Tour was not added.");
            return;
        }

        Tour created = new Tour(id, selected, name, departureDate, price);
        add(created);
        System.out.println("Added tour successfully: " + created.screenString());
    }

    public boolean removeTour() {
        if (isEmpty()) {
            System.out.println("Tour list is empty.");
            return false;
        }

        String id = readRequiredText("Enter tour ID to remove: ");
        if (id == null) {
            System.out.println("Input cancelled.");
            return false;
        }

        int index = searchId(id);
        if (index == -1) {
            System.out.println("Tour ID '" + id + "' not found.");
            return false;
        }

        Tour removed = remove(index);
        System.out.println("Removed tour successfully: " + removed.getTourId() + " - " + removed.getTourName());
        return true;
    }

    public boolean updateTour(DestinationList destinations) {
        if (isEmpty()) {
            System.out.println("Tour list is empty.");
            return false;
        }
        if (destinations == null || destinations.isEmpty()) {
            System.out.println("Cannot update tour: destination list is empty.");
            return false;
        }

        String targetId = readRequiredText("Enter tour ID to update: ");
        if (targetId == null) {
            System.out.println("Input cancelled.");
            return false;
        }

        int index = searchId(targetId);
        if (index == -1) {
            System.out.println("Tour ID '" + targetId + "' not found.");
            return false;
        }

        Tour tour = get(index);
        System.out.println("Current tour: " + tour.screenString());

        String newId;
        while (true) {
            newId = readRequiredText("Enter new tour ID: ");
            if (newId == null) {
                System.out.println("Input cancelled. Tour was not updated.");
                return false;
            }
            int existingIndex = searchId(newId);
            if (existingIndex == -1 || existingIndex == index) {
                break;
            }
            System.out.println("Tour ID '" + newId + "' already belongs to another tour. Please re-enter.");
        }

        System.out.println("Select new destination for this tour:");
        Destination newDestination = resolveExistingDestination(destinations, destinations.getUserChoice());
        if (newDestination == null) {
            System.out.println("Invalid or missing destination selection. Tour was not updated.");
            return false;
        }

        String newName = readRequiredText("Enter new tour name: ");
        if (newName == null) {
            System.out.println("Input cancelled. Tour was not updated.");
            return false;
        }

        String newDate = readRequiredText("Enter new departure date (yyyy-MM-dd): ");
        if (newDate == null) {
            System.out.println("Input cancelled. Tour was not updated.");
            return false;
        }

        Double newPrice = readNonNegativePrice("Enter new price (>= 0): ");
        if (newPrice == null) {
            System.out.println("Input cancelled. Tour was not updated.");
            return false;
        }

        tour.setTourId(newId);
        tour.setDestination(newDestination);
        tour.setTourName(newName);
        tour.setDepartureDate(newDate);
        tour.setPrice(newPrice);
        System.out.println("Updated tour successfully: " + tour.screenString());
        return true;
    }

    private static String readLine(String prompt) {
        if (inputScanner == null || boundInputStream != System.in) {
            boundInputStream = System.in;
            inputScanner = new Scanner(System.in);
        }
        System.out.print(prompt);
        if (!inputScanner.hasNextLine()) {
            return null;
        }
        return inputScanner.nextLine().trim();
    }

    private static String readRequiredText(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (value == null) {
                return null;
            }
            if (value.isEmpty()) {
                System.out.println("Value cannot be empty. Please re-enter.");
            } else if (value.contains(",")) {
                System.out.println("Value cannot contain a comma (','). Please re-enter.");
            } else {
                return value;
            }
        }
    }

    private static Double readNonNegativePrice(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            if (raw == null) {
                return null;
            }
            try {
                double value = Double.parseDouble(raw);
                if (Double.isFinite(value) && value >= 0) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
            }
            System.out.println("Price must be a valid finite number >= 0. Please re-enter.");
        }
    }

    private static Destination resolveExistingDestination(DestinationList destinations, Destination candidate) {
        if (destinations == null || candidate == null || candidate.getDestinationId() == null) {
            return null;
        }
        for (Destination item : destinations) {
            if (item == candidate) {
                return item;
            }
            if (item != null && item.getDestinationId() != null
                    && item.getDestinationId().equalsIgnoreCase(candidate.getDestinationId().trim())) {
                return item;
            }
        }
        return null;
    }

    // Cac ham duoi day thuoc pham vi thanh vien khac; GIU NGUYEN skeleton.
    public void listTours() {
    }

    public void printBasedDestinationName() {
    }
}
