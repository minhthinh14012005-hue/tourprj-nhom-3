# IV.6 - Test 11: lưu Tour và chạy lại chương trình

- Người thực hiện: Lê Thuần Trường.
- Ngày thực hiện: 24/11/2026.
- Reviewer theo Plan: Nguyễn Hữu Đức (chờ review).
- Runtime Java: 25.0.3
- JVM lưu: PID 27768, exit code 0
- JVM chạy lại: PID 28500, exit code 0
- Dữ liệu thử: C:\Users\ADMIN\AppData\Local\Temp\truong-iv6-test11-11813999262831152397

## Kịch bản

1. Tạo dữ liệu tạm gồm 3 Destination và 3 Tour.
2. JVM thứ nhất: thêm T04 qua menu 8, lưu qua menu 11, thoát qua menu 0.
3. Đối chiếu 3 bản ghi cũ và đầy đủ 5 trường của T04 trong file.
4. JVM mới: nạp lại file, tìm D02, xem T04 rồi hủy sửa bằng EOF.
5. Xác nhận file không thay đổi sau lần chạy lại.

## Kết quả

| Kiểm tra | Kết quả |
|---|---|
| JVM 1: menu 11 saves successfully and exits with code 0 | PASS |
| Saved file contains 4 unique Tour IDs | PASS |
| All 3 original Tours retain all 5 fields | PASS |
| New Tour T04 retains all 5 expected fields | PASS |
| JVM 2: fresh startup loads and displays saved Tour T04 | PASS |
| Restart leaves tours.txt byte-for-byte unchanged | PASS |

Tổng: 6 passed, 0 failed.

Bug quan sát: chưa ghi nhận trong phạm vi Test 11.

## Dữ liệu sau khi lưu

    T01,D01,Beach,2027-01-15,2800000.0
    T02,D02,Bay,2027-02-12,3500000.0
    T03,D03,City,2027-03-10,3100000.0
    T04,D02,IV6 Save Restart,2028-02-29,4500000.0

## Log tiến trình lưu

    Loaded 3 destination(s) and 3 tour(s).

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Enter tour ID: Select a destination for this tour:
    1. D01,Da Nang,Central,1500000.0
    2. D02,Ha Long,North,2000000.0
    3. D03,Hue,Central,1000000.0
    Choose 1-3: Enter tour name: Enter departure date (yyyy-MM-dd): Enter price (>= 0): Added tour successfully: T04    Ha Long              IV6 Save Restart         2028-02-29   4,500,000

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Saved tours successfully.

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Exit selected. No implicit save was performed.

## Log tiến trình chạy lại

    Loaded 3 destination(s) and 4 tour(s).

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Enter destination ID: D02 | Ha Long | North | 2000000.00

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Enter tour ID to update: Current tour: T04    Ha Long              IV6 Save Restart         2028-02-29   4,500,000
    Enter new tour ID: Input cancelled. Tour was not updated.

    ========== TOUR MANAGEMENT ==========
    1. List destinations
    2. Add destination
    3. Search destination by ID
    4. Update destination
    5. Save destinations
    6. List tours sorted
    7. Search tours by destination name
    8. Add tour
    9. Remove tour
    10. Update tour
    11. Save tours
    0. Exit without implicit save
    Your choice (0-11): Input ended. Exiting without implicit save.
