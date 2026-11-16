package edu.int1332.tourprj;

import java.util.ArrayList;

/** Contract-only collection for destinations; I/O and menu operations are phase-II work. */
public class DestinationList extends ArrayList<Destination> {
    public boolean loadFromFile(String filename) { return false; }
    public boolean saveToFile(String filename) { return false; }
    public int searchId(String id) { return -1; }
    public void addDestination() { }
    public boolean updateDestination() { return false; }
    public void listDestinations() { }
    public Destination getUserChoice() { return null; }
}
