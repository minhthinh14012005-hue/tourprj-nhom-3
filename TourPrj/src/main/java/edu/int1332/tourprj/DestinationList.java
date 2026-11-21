// Tên: Trần Minh Thịnh
// MSV: B23DCKD065
// Mô tả: Triển khai đọc file, ghi file và tìm kiếm Destination theo ID.

package edu.int1332.tourprj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/** Collection of destinations with validated UTF-8 file I/O and ID lookup. */
public class DestinationList extends ArrayList<Destination> {
    public boolean loadFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Cannot load destinations: filename is blank.");
            return false;
        }

        final Path path;
        try {
            path = Paths.get(filename);
        } catch (InvalidPathException ex) {
            System.err.printf("Cannot read %s: invalid path.%n", filename);
            return false;
        }

        ArrayList<Destination> loaded = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        boolean allValid = true;
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    reportInvalidLine(filename, lineNumber, "empty line");
                    allValid = false;
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length != 4) {
                    reportInvalidLine(filename, lineNumber,
                            "expected 4 fields, got " + fields.length);
                    allValid = false;
                    continue;
                }
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].trim();
                }

                String id = fields[0];
                String name = fields[1];
                String region = fields[2];
                String baseCostText = fields[3];
                if (id.isEmpty() || name.isEmpty() || region.isEmpty() || baseCostText.isEmpty()) {
                    reportInvalidLine(filename, lineNumber, "required field is blank");
                    allValid = false;
                    continue;
                }

                String idKey = id.toLowerCase(java.util.Locale.ROOT);
                if (seenIds.contains(idKey)) {
                    reportInvalidLine(filename, lineNumber,
                            "duplicate destination ID '" + id + "'");
                    allValid = false;
                    continue;
                }

                final double baseCost;
                try {
                    baseCost = Double.parseDouble(baseCostText);
                } catch (NumberFormatException ex) {
                    reportInvalidLine(filename, lineNumber,
                            "invalid base cost '" + baseCostText + "'");
                    allValid = false;
                    continue;
                }
                if (!Double.isFinite(baseCost) || baseCost <= 0) {
                    reportInvalidLine(filename, lineNumber,
                            "base cost must be finite and greater than 0");
                    allValid = false;
                    continue;
                }

                loaded.add(new Destination(id, name, region, baseCost));
                seenIds.add(idKey);
            }
        } catch (IOException ex) {
            System.err.printf("Cannot read %s: %s%n", filename, ex.getMessage());
            return false;
        }

        clear();
        addAll(loaded);
        return allValid;
    }

    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println("Cannot save destinations: filename is blank.");
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(filename), StandardCharsets.UTF_8)) {
            for (Destination destination : this) {
                if (destination == null) {
                    throw new IOException("Destination is null");
                }
                writer.write(destination.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException | InvalidPathException ex) {
            System.err.printf("Cannot save %s: %s%n", filename, ex.getMessage());
            return false;
        }
    }

    public int searchId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return -1;
        }

        String normalized = id.trim();
        for (int i = 0; i < size(); i++) {
            Destination destination = get(i);
            if (destination != null && destination.getDestinationId() != null
                    && destination.getDestinationId().equalsIgnoreCase(normalized)) {
                return i;
            }
        }
        return -1;
    }

    private static void reportInvalidLine(String filename, int lineNumber, String reason) {
        System.err.printf("%s:%d: %s; skipped.%n", filename, lineNumber, reason);
    }

    public void addDestination() { }
    public boolean updateDestination() { return false; }
    public void listDestinations() { }
    public Destination getUserChoice() { return null; }
}
