package edu.int1332.tourprj;

import java.util.ArrayList;

/** Contract-only collection for tours; it receives destinations before loading tours. */
public class TourList extends ArrayList<Tour> {
    public boolean loadFromFile(String filename, DestinationList destinations) { return false; }
    public boolean saveToFile(String filename) { return false; }
    public int searchId(String id) { return -1; }
    public void addTour(DestinationList destinations) { }
    public boolean removeTour() { return false; }
    public boolean updateTour(DestinationList destinations) { return false; }
    public void listTours() { }
    public void printBasedDestinationName() { }
}
