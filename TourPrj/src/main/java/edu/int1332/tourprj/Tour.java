package edu.int1332.tourprj;

/** A tour belongs to exactly one destination and sorts by destination name then tour ID. */
public class Tour implements Comparable<Tour> {
    private String tourId;
    private Destination destination;
    private String tourName;
    private String departureDate;
    private double price;

    public Tour() { }

    public Tour(String tourId, Destination destination, String tourName, String departureDate, double price) {
        this.tourId = tourId;
        this.destination = destination;
        this.tourName = tourName;
        this.departureDate = departureDate;
        this.price = price;
    }

    public String getTourId() { return tourId; }
    public void setTourId(String tourId) { this.tourId = tourId; }
    public Destination getDestination() { return destination; }
    public void setDestination(Destination destination) { this.destination = destination; }
    public String getTourName() { return tourName; }
    public void setTourName(String tourName) { this.tourName = tourName; }
    public String getDepartureDate() { return departureDate; }
    public void setDepartureDate(String departureDate) { this.departureDate = departureDate; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public int compareTo(Tour other) {
        int byDestination = destination.getDestinationName()
                .compareToIgnoreCase(other.destination.getDestinationName());
        return byDestination != 0 ? byDestination : tourId.compareToIgnoreCase(other.tourId);
    }

    @Override
    public String toString() {
        return tourId + "," + destination.getDestinationId() + "," + tourName + "," + departureDate + "," + price;
    }

    public String screenString() {
        return String.format("%-6s %-20s %-24s %-12s %,.0f", tourId,
                destination.getDestinationName(), tourName, departureDate, price);
    }
}
