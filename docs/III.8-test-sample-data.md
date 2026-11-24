# III.8 — Dữ liệu mẫu bổ sung và file kiểm thử lỗi (Test 12)

**Người thực hiện:** Đàm Trọng Đạt  
**Người Review:** Lê Đình Đông  
**Mục đích:** Chuẩn bị các bộ dữ liệu mẫu hợp lệ bổ sung và các trường hợp lỗi dữ liệu đầu vào để phục vụ kịch bản Test 12 và kiểm thử độ bền (resilience) của hệ thống.

## Danh mục file tạo tại `TourPrj/data/test_samples/`

1. `destinations_valid_extended.txt`:
   - 6 bản ghi Destination chuẩn (D01 -> D06), đầy đủ các vùng North, Central, South.
   - Dùng để kiểm thử mở rộng danh mục điểm đến và tính toán chi phí.

2. `tours_valid_extended.txt`:
   - 7 bản ghi Tour chuẩn liên kết tới các điểm đến D01 -> D06.
   - Có các trường hợp cùng điểm đến (D01) với mã Tour khác nhau (T01, T04) để kiểm thử thuật toán sắp xếp (Test 06) và tìm kiếm theo một phần tên (Test 07).

3. `destinations_corrupted.txt`:
   - Kiểm thử nạp Destination có lỗi:
     - Dòng không có dấu phẩy (`corrupted_line_without_commas`).
     - Chi phí âm (`-500000`).
     - Chi phí không phải số (`not_a_number`).
     - Thiếu ID (bắt đầu bằng dấu phẩy).
     - Thiếu vùng miền (region rỗng).
   - Kiểm tra hệ thống bỏ qua dòng lỗi, in thông báo rõ dòng và nạp tiếp các dòng hợp lệ.

4. `tours_corrupted.txt`:
   - Kiểm thử nạp Tour có lỗi (phục vụ Test 12):
     - Dòng thiếu trường (`invalid_single_field_row`).
     - Tham chiếu đến Destination không tồn tại (`D99`).
     - Trùng mã Tour ID (`T01` xuất hiện 2 lần).
     - Giá tour âm (`-150000`).
     - Giá tour không phải số (`free_of_charge`).
     - Tên tour rỗng.
   - Xác nhận: Hệ thống in thông báo chi tiết mã lỗi/số dòng và vẫn nạp được các dòng hợp lệ (T01, T07).
