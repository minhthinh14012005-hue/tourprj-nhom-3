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

/**
 * Danh sách Destination.
 *
 * II.4 - Trần Minh Thịnh:
 * Đọc file, ghi file, tìm kiếm ID và thông báo dòng lỗi.
 *
 * II.6 - Lê Thuần Trường:
 * Thêm, cập nhật, liệt kê, chọn Destination và kiểm tra đầu vào.
 *
 * Người tích hợp: Lê Thuần Trường
 * Ngày tích hợp: 22/11/2026
 */
public class DestinationList extends ArrayList<Destination> {

    /**
     * Người thực hiện: Trần Minh Thịnh
     * Mô tả: Nạp Destination, bỏ qua dòng sai và thông báo lỗi.
     */
    public boolean loadFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println(
                    "Cannot load destinations: filename is blank."
            );
            return false;
        }

        final Path path;

        try {
            path = Paths.get(filename);
        } catch (InvalidPathException ex) {
            System.err.printf(
                    "Cannot read %s: invalid path.%n", filename
            );
            return false;
        }

        ArrayList<Destination> loaded = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();
        boolean allValid = true;
        int lineNumber = 0;

        try (BufferedReader reader =
                     Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

            String line;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    reportInvalidLine(
                            filename, lineNumber, "empty line"
                    );
                    allValid = false;
                    continue;
                }

                String[] fields = line.split(",", -1);

                if (fields.length != 4) {
                    reportInvalidLine(
                            filename,
                            lineNumber,
                            "expected 4 fields, got " + fields.length
                    );
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

                if (id.isEmpty() || name.isEmpty()
                        || region.isEmpty() || baseCostText.isEmpty()) {
                    reportInvalidLine(
                            filename, lineNumber, "required field is blank"
                    );
                    allValid = false;
                    continue;
                }

                String idKey =
                        id.toLowerCase(java.util.Locale.ROOT);

                if (seenIds.contains(idKey)) {
                    reportInvalidLine(
                            filename,
                            lineNumber,
                            "duplicate destination ID '" + id + "'"
                    );
                    allValid = false;
                    continue;
                }

                final double baseCost;

                try {
                    baseCost = Double.parseDouble(baseCostText);
                } catch (NumberFormatException ex) {
                    reportInvalidLine(
                            filename,
                            lineNumber,
                            "invalid base cost '" + baseCostText + "'"
                    );
                    allValid = false;
                    continue;
                }

                if (!Double.isFinite(baseCost) || baseCost <= 0) {
                    reportInvalidLine(
                            filename,
                            lineNumber,
                            "base cost must be finite and greater than 0"
                    );
                    allValid = false;
                    continue;
                }

                loaded.add(
                        new Destination(id, name, region, baseCost)
                );
                seenIds.add(idKey);
            }
        } catch (IOException ex) {
            System.err.printf(
                    "Cannot read %s: %s%n", filename, ex.getMessage()
            );
            return false;
        }

        clear();
        addAll(loaded);
        return allValid;
    }

    /**
     * Người thực hiện: Trần Minh Thịnh
     * Mô tả: Ghi danh sách Destination ra file UTF-8.
     */
    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            System.err.println(
                    "Cannot save destinations: filename is blank."
            );
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
            System.err.printf(
                    "Cannot save %s: %s%n", filename, ex.getMessage()
            );
            return false;
        }
    }

    /**
     * Người thực hiện: Trần Minh Thịnh
     * Mô tả: Tìm ID không phân biệt hoa thường.
     *
     * @return vị trí tìm thấy hoặc -1
     */
    public int searchId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return -1;
        }

        String normalized = id.trim();

        for (int i = 0; i < size(); i++) {
            Destination destination = get(i);

            if (destination != null
                    && destination.getDestinationId() != null
                    && destination.getDestinationId()
                    .equalsIgnoreCase(normalized)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Người thực hiện: Trần Minh Thịnh
     * Mô tả: Thông báo tên file, số dòng và nguyên nhân lỗi.
     */
    private static void reportInvalidLine(
            String filename, int lineNumber, String reason) {

        System.err.printf(
                "%s:%d: %s; skipped.%n",
                filename, lineNumber, reason
        );
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: II.6 - Thêm Destination, kiểm tra dữ liệu và mã trùng.
     *        Chỉ thêm trong bộ nhớ, không tự lưu xuống file.
     */
    public void addDestination() {
        String id;

        while (true) {
            id = readRequiredText("Nhap Destination ID: ");

            if (searchId(id) == -1) {
                break;
            }

            System.out.println(
                    "ID da ton tai. Vui long nhap lai."
            );
        }

        String name =
                readRequiredText("Nhap ten Destination: ");
        String region =
                readRequiredText("Nhap region: ");
        double baseCost = readBaseCost();

        add(new Destination(id, name, region, baseCost));

        System.out.println("Them Destination thanh cong.");
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: II.6 - Cập nhật Destination theo ID,
     *        kiểm tra dữ liệu và mã trùng với bản ghi khác.
     *
     * @return true nếu cập nhật thành công;
     *         false nếu không tìm thấy Destination
     */
    public boolean updateDestination() {
        String id =
                readRequiredText("Nhap ID Destination can sua: ");
        int index = searchId(id);

        if (index == -1) {
            System.out.println("Khong tim thay Destination.");
            return false;
        }

        Destination destination = get(index);

        System.out.println("Thong tin hien tai:");
        System.out.println("ID: " + destination.getDestinationId());
        System.out.println("Ten: " + destination.getDestinationName());
        System.out.println("Region: " + destination.getRegion());
        System.out.println("Base cost: " + destination.getBaseCost());

        String newId;

        while (true) {
            newId = readRequiredText("Nhap ID moi: ");
            int otherIndex = searchId(newId);

            if (otherIndex == -1 || otherIndex == index) {
                break;
            }

            System.out.println(
                    "ID da thuoc Destination khac."
            );
        }

        String newName =
                readRequiredText("Nhap ten moi: ");
        String newRegion =
                readRequiredText("Nhap region moi: ");
        double newBaseCost = readBaseCost();

        // Sửa đối tượng hiện có để giữ liên kết với các Tour.
        destination.setDestinationId(newId);
        destination.setDestinationName(newName);
        destination.setRegion(newRegion);
        destination.setBaseCost(newBaseCost);

        System.out.println("Cap nhat Destination thanh cong.");
        return true;
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: II.6 - Hiển thị danh sách Destination.
     */
    public void listDestinations() {
        if (isEmpty()) {
            System.out.println(
                    "Danh sach Destination dang rong."
            );
            return;
        }

        System.out.printf(
                "%-12s %-25s %-15s %15s%n",
                "ID", "Destination", "Region", "Base cost"
        );

        for (Destination destination : this) {
            System.out.printf(
                    "%-12s %-25s %-15s %15.2f%n",
                    destination.getDestinationId(),
                    destination.getDestinationName(),
                    destination.getRegion(),
                    destination.getBaseCost()
            );
        }
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: II.6 - Chọn Destination qua Menu.
     *
     * @return Destination được chọn hoặc null
     */
    public Destination getUserChoice() {
        if (isEmpty()) {
            System.out.println(
                    "Chua co Destination. Hay them Destination truoc."
            );
            return null;
        }

        return Menu.refGetChoice(this);
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: Nhập chuỗi bắt buộc, không chứa dấu phẩy.
     */
    private String readRequiredText(String prompt) {
        while (true) {
            String value = Menu.readLine(prompt);

            if (value.isEmpty()) {
                System.out.println("Khong duoc de trong.");
            } else if (value.contains(",")) {
                System.out.println(
                        "Khong duoc chua dau phay."
                );
            } else {
                return value;
            }
        }
    }

    /**
     * Người thực hiện: Lê Thuần Trường
     * Ngày thực hiện: 18/11/2026
     * Mô tả: Nhập chi phí hữu hạn lớn hơn 0.
     */
    private double readBaseCost() {
        while (true) {
            String input =
                    Menu.readLine("Nhap base cost: ");

            try {
                double cost = Double.parseDouble(input);

                if (Double.isFinite(cost) && cost > 0) {
                    return cost;
                }
            } catch (NumberFormatException e) {
                // Thông báo lỗi và yêu cầu nhập lại bên dưới.
            }

            System.out.println(
                    "Base cost phai la so hop le lon hon 0."
            );
        }
    }
}