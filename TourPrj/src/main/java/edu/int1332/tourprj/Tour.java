// Tên: Lê Đình Đông
// MSV: B23DCKD015
// Mô tả: II.2 - Triển khai lớp Tour: kiểm tra ràng buộc dữ liệu, Comparable, toString/screenString.

package edu.int1332.tourprj;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/** A tour belongs to exactly one destination and sorts by destination name then tour ID. */
public class Tour implements Comparable<Tour> {
    private static final DateTimeFormatter DEPARTURE_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private String tourId;
    private Destination destination;
    private String tourName;
    private String departureDate;
    private double price;

    /** Default constructor: all fields stay unset until the setters are called. */
    public Tour() { }

    /** Creates a validated tour record. */
    public Tour(String tourId, Destination destination, String tourName,
                String departureDate, double price) {
        setTourId(tourId);
        setDestination(destination);
        setTourName(tourName);
        setDepartureDate(departureDate);
        setPrice(price);
    }

    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = requireText(tourId, "Tour ID"); }

    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) {
        if (destination == null) {
            throw new IllegalArgumentException("Destination cannot be null.");
        }
        this.destination = destination;
    }

    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = requireText(tourName, "Tour name"); }

    public String getDepartureDate() { return departureDate; }
    public void setDepartureDate(String departureDate) {
        this.departureDate = requireText(departureDate, "Departure date");
    }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        if (!Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException("Price must be a finite number greater than or equal to 0.");
        }
        this.price = price;
    }

    @Override
    public int compareTo(Tour other) {
        if (other == null) {
            return 1;
        }
        int byDestination = destinationNameOrEmpty().compareToIgnoreCase(other.destinationNameOrEmpty());
        return byDestination != 0 ? byDestination
                : nullToEmpty(tourId).compareToIgnoreCase(nullToEmpty(other.tourId));
    }

    /** Serializes one record for tours.txt: tourID,destinationID,tourName,departureDate,price. */
    @Override
    public String toString() {
        String destinationId = destination == null ? "" : nullToEmpty(destination.getDestinationId());
        String priceText = (price == (long) price)
                ? String.format(java.util.Locale.ROOT, "%.0f", price)
                : String.valueOf(price);
        return nullToEmpty(tourId) + "," + destinationId + "," + nullToEmpty(tourName) + ","
                + nullToEmpty(departureDate) + "," + priceText;
    }

    /** Readable one-line form for console output (destination shown by name, not by ID). */
    public String screenString() {
        return String.format(java.util.Locale.ROOT, "%-6s %-20s %-24s %-12s %,.0f",
                nullToEmpty(tourId), destinationNameOrEmpty(), nullToEmpty(tourName),
                nullToEmpty(departureDate), price);
    }

    /** Returns whether a value is a real date in strict yyyy-MM-dd form. */
    public static boolean isValidDepartureDate(String value) {
        if (value == null || !value.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            return false;
        }
        try {
            LocalDate.parse(value, DEPARTURE_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    private String destinationNameOrEmpty() {
        return destination == null ? "" : nullToEmpty(destination.getDestinationName());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
        if (value.indexOf(',') >= 0) {
            throw new IllegalArgumentException(fieldName + " cannot contain a comma.");
        }
        return value.trim();
    }
}
