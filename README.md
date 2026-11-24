# TourPrj — Nhóm 3

Java console project for Topic 10: **Travel Tour Management System** (INT1332).

## Chức năng

- Quản lý Destination: liệt kê, thêm, tìm theo mã, sửa và lưu file.
- Quản lý Tour: liệt kê/sắp xếp, tìm theo tên Destination, thêm, xóa, sửa và lưu file.
- Đọc/ghi UTF-8, báo từng dòng dữ liệu lỗi; kiểm tra mã trùng, giá không hợp lệ và ngày sai định dạng.
- Menu console từ `0` đến `11`; chọn `0` không tự động ghi đè dữ liệu.

## Cấu trúc

- `TourPrj/src/main/java/edu/int1332/tourprj/` — mã nguồn chương trình.
- `TourPrj/src/test/java/edu/int1332/tourprj/` — các test driver độc lập.
- `TourPrj/data/` — `destinations.txt` và `tours.txt`.
- `docs/design/` — UML, luồng file, menu và kế hoạch kiểm thử.

## Chạy bằng IntelliJ IDEA

1. Chọn **File → Open** và mở thư mục `TourPrj`.
2. Chọn **JDK 11 hoặc mới hơn** khi IntelliJ hỏi Project SDK.
3. Mở `src/main/java/edu/int1332/tourprj/TourManager.java`.
4. Nhấp phải lớp `TourManager`, chọn **Run 'TourManager.main()'**.
5. Đặt **Working directory** là thư mục `TourPrj` để chương trình tìm được thư mục `data`.

Nếu chưa đúng, vào **Run → Edit Configurations…**, chọn cấu hình `TourManager`
và sửa trường **Working directory** thành đường dẫn tới `TourPrj`.

## Chạy test driver

Mở một lớp trong `src/test/java/edu/int1332/tourprj/`, nhấp phải lớp và chọn **Run**.
Nên chạy: `DestinationListTestDriver`, `DestinationCrudTestDriver`,
`DestinationAndSearchTestDriver`, `DucAssignedWorkTestDriver`,
`TourEntityTestDriver`, `TourListTestDriver`, `TourCrudTestDriver`,
`DateValidationTestDriver`, `DateMenuIntegrationTestDriver` và
`IV6SaveRestartTestDriver`.

Chỉ ghi nhận minh chứng khi dòng kết quả báo `0 failed`.

## Chạy dòng lệnh

Từ thư mục `TourPrj`:

```powershell
javac -encoding UTF-8 -d out src/main/java/edu/int1332/tourprj/*.java
java -cp out edu.int1332.tourprj.TourManager
```

`main` là bản tích hợp để kiểm thử và nộp bài. Khi test chức năng lưu, hãy dùng
bản sao file dữ liệu nếu không muốn thay đổi dữ liệu gốc.
