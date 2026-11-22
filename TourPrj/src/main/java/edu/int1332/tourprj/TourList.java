package edu.int1332.tourprj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
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

                /*
                 * Người bổ sung: Lê Thuần Trường
                 * Ngày bổ sung: 22/11/2026
                 * Mô tả: III.6 - Kiểm tra ngày khởi hành khi đọc file Tour.
                 *        Báo rõ file, dòng lỗi và bỏ qua bản ghi không hợp lệ.
                 */
                if (!Tour.isValidDepartureDate(departureDate)) {
                    System.err.printf(
                            "%s:%d: invalid departure date '%s'; "
                                    + "expected a valid date in yyyy-MM-dd format; skipped.%n",
                            filename, lineNumber, departureDate
                    );
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

    // Cac ham duoi day thuoc pham vi thanh vien khac; GIU NGUYEN skeleton.
    public void addTour(DestinationList destinations) {
    }

    public boolean removeTour() {
        return false;
    }

    public boolean updateTour(DestinationList destinations) {
        return false;
    }

    public void listTours() {
    }

    public void printBasedDestinationName() {
    }
}
