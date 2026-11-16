package edu.int1332.tourprj;

/** Domain entity for one destination. Functional validation is implemented in phase II. */
public class Destination {
    private String destinationId;
    private String destinationName;
    private String region;
    private double baseCost;

    public Destination() { }

    public Destination(String destinationId, String destinationName, String region, double baseCost) {
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.region = region;
        this.baseCost = baseCost;
    }

    public String getDestinationId() { return destinationId; }
    public void setDestinationId(String destinationId) { this.destinationId = destinationId; }
    public String getDestinationName() { return destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public double getBaseCost() { return baseCost; }
    public void setBaseCost(double baseCost) { this.baseCost = baseCost; }

    @Override
    public String toString() {
        return destinationId + "," + destinationName + "," + region + "," + baseCost;
    }
}
