package edu.int1332.tourprj;

import java.util.Locale;

/**
 * Domain entity for one destination.
 * Phu trach boi: Dam Trong Dat (Viec II.1)
 * Dam bao tinh dong goi (Encapsulation), kiem tra rang buoc du lieu tai constructor va setters.
 */
public class Destination {
    private String destinationId;
    private String destinationName;
    private String region;
    private double baseCost;

    /**
     * Default constructor.
     */
    public Destination() {
    }

    /**
     * Parameterized constructor.
     * Su dung cac setter de xac thuc du lieu dau vao.
     *
     * @param destinationId   ma diem den, khong rong
     * @param destinationName ten diem den, khong rong
     * @param region          vung mien, khong rong
     * @param baseCost        chi phi co ban, phai > 0
     * @throws IllegalArgumentException neu bat ky truong nao vi pham rang buoc
     */
    public Destination(String destinationId, String destinationName, String region, double baseCost) {
        setDestinationId(destinationId);
        setDestinationName(destinationName);
        setRegion(region);
        setBaseCost(baseCost);
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        if (destinationId == null || destinationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination ID cannot be null or empty.");
        }
        this.destinationId = destinationId.trim();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public void setDestinationName(String destinationName) {
        if (destinationName == null || destinationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination name cannot be null or empty.");
        }
        this.destinationName = destinationName.trim();
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalArgumentException("Region cannot be null or empty.");
        }
        this.region = region.trim();
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        if (!Double.isFinite(baseCost) || baseCost <= 0) {
            throw new IllegalArgumentException("Base cost must be a finite number greater than 0.");
        }
        this.baseCost = baseCost;
    }

    /**
     * Dinh dang de ghi vao tep destinations.txt:
     * destinationID,destinationName,region,baseCost
     */
    @Override
    public String toString() {
        String costText = (baseCost == (long) baseCost)
                ? String.format(Locale.ROOT, "%.0f", baseCost)
                : String.valueOf(baseCost);
        return destinationId + "," + destinationName + "," + region + "," + costText;
    }
}
