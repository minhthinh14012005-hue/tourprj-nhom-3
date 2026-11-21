# III.9 — Rà soát code đợt 1 theo checklist Java

**Người thực hiện:** Đàm Trọng Đạt  
**Người Review:** Lê Thuần Trường  
**Ngày thực hiện:** 2026-11-21  

## Mục tiêu
Rà soát toàn bộ code đợt 1 của các thành viên trong nhóm theo chuẩn Java OOP và các yêu cầu kỹ thuật của đề tài Topic 10:
- Quy ước đặt tên (CamelCase, PascalCase).
- So sánh chuỗi: Dùng `equals()` / `equalsIgnoreCase()`, tuyệt đối không dùng `==`.
- Xử lý file I/O: Sử dụng `try-with-resources`.
- `searchId` / `searchID`: Trả về vị trí (index) tìm thấy hoặc `-1`.
- Getters / Setters: Có kiểm tra dữ liệu đầu vào (tính đóng gói Encapsulation).

---

## Bảng kết quả rà soát chi tiết

| STT | File / Module | Tiêu chí | Kết quả | Ghi chú & Nhận xét cho chủ module |
|:---:|---|---|:---:|---|
| 1 | `Destination.java` (Đàm Trọng Đạt) | Encapsulation, Validation, toString | **ĐẠT** | Thuộc tính private. Getter/setter và constructor kiểm tra ID, name, region không rỗng, baseCost > 0. toString format đúng định dạng ghi file. |
| 2 | `Tour.java` (Lê Đình Đông) | Encapsulation, Comparable, compareTo | **ĐẠT** | Đã implements `Comparable<Tour>`. compareTo so sánh theo `destinationName` rồi tie-break bằng `tourId`. screenString format chuẩn. |
| 3 | `TourList.java` (Vũ Hữu Thịnh) | File I/O, searchId, Price range | **ĐẠT** | `loadFromFile` và `saveToFile` dùng `try-with-resources`. `searchId` so sánh bằng `equalsIgnoreCase`, trả về index hoặc `-1`. Bỏ qua dòng sai có log rõ ràng. |
| 4 | `TourList.java` (Đàm Trọng Đạt) | listTours, printBasedDestinationName | **ĐẠT** | `listTours` dùng `Collections.sort(this)` và format bảng đẹp. `printBasedDestinationName` tìm kiếm không phân biệt hoa thường và thông báo khi không có kết quả. |
| 5 | `DestinationList.java` (Trần Minh Thịnh, Lê Thuần Trường) | CRUD, File I/O | **ĐANG LÀM** | Skeleton đã sẵn sàng theo đúng hợp đồng UML. Cần hoàn thiện nạp/lưu file và CRUD tương tự `TourList`. |
| 6 | `Menu.java` (Nguyễn Hữu Đức) | Generic methods, Numeric recovery | **ĐANG LÀM** | Skeleton đã có `intGetChoice`, `refGetChoice`. Cần hoàn thiện vòng lặp bắt `InputMismatchException` / `NumberFormatException`. |
| 7 | `TourManager.java` (Nguyễn Hữu Đức, Lê Đình Đông) | Main loop, Console menu | **ĐANG LÀM** | Sẵn sàng cho việc nối switch-case menu 1–11 ở đợt 2. |
