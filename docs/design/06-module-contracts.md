# Module contracts and review handoff

## Ownership at the design gate

| Deliverable | Owner | Reviewer | Acceptance evidence |
|---|---|---|---|
| UML source and exported image | Nguyễn Hữu Đức | Trần Minh Thịnh | Every class/member/relationship matches UML source. |
| TourPrj skeleton | Trần Minh Thịnh | Vũ Hữu Thịnh | Java 11 compilation and package naming pass. |
| File flow and error policy | Vũ Hữu Thịnh | Lê Thuần Trường | Load order and all error paths documented. |
| Menu specification | Lê Thuần Trường | Bùi Văn Sang | Options 0–11 and recovery rules covered. |
| Test plan | Bùi Văn Sang | Đàm Trọng Đạt | All mandatory tests 01–12 are reproducible. |

## Implementation boundaries

- `Destination` and `Tour` own state and basic serialization only.
- Collection classes own file I/O, searching, CRUD, list rendering, and menu-driven operations.
- `Menu` owns scanner recovery and generic selection; it must not contain business rules.
- `TourManager` creates lists, loads destinations before tours, and routes choices. It must not duplicate collection logic.

## Definition of done for this milestone

1. The six-class skeleton compiles with Java 11.
2. Each design document is reviewed by the named reviewer.
3. Data files have at least three valid records each and use the documented schema.
4. Test plan contains all twelve required scenarios.
5. No implementation branch begins changing public method contracts without updating UML and this document.

## Bổ sung kiểm tra ngày III.6 — 22/11/2026

- Người thực hiện: Lê Thuần Trường.
- `Tour.isValidDepartureDate(String value)` trả `true` khi chuỗi đúng dạng `yyyy-MM-dd` và biểu diễn ngày hợp lệ.
- Khi đọc file Tour, dòng có ngày không hợp lệ được thông báo rõ tên file, số dòng và bỏ qua; các dòng hợp lệ vẫn được nạp.
- `DateValidationTestDriver`: 15 passed, 0 failed trên IntelliJ.
- `TourListTestDriver`: 14 passed, 0 failed trên IntelliJ.
- Đã tích hợp kiểm tra ngày và yêu cầu nhập lại trong `addTour`/`updateTour` của Bùi Văn Sang (23/11/2026).

## Tích hợp nhập ngày III.6 — 23/11/2026

- Người thực hiện: Lê Thuần Trường; giữ mã CRUD Tour của Bùi Văn Sang.
- `TourList.readDepartureDate` yêu cầu nhập lại khi ngày sai định dạng hoặc không tồn tại.
- Hết dữ liệu nhập khi thêm/sửa: hủy thao tác, giữ nguyên dữ liệu hiện tại.
- Kiểm thử trên IntelliJ: DateValidationTestDriver 15/15; DateInputTestDriver 4/4; TourCrudTestDriver 11/11; TourListTestDriver 14/14; 0 failed.
- Đã bổ sung kiểm thử qua Menu và DestinationList thực tế, đạt 4/4 ngày 24/11/2026.
- Đã đồng bộ UML và ảnh PNG/SVG.

## Hoàn tất kiểm thử tích hợp III.6 — 24/11/2026

- Người thực hiện: Lê Thuần Trường.
- Tích hợp Menu/TourManager của Nguyễn Hữu Đức từ PR #5 và DestinationList từ PR #2; giữ thông tin tác giả.
- TourList dùng chung bộ nhập của Menu để tránh nhiều Scanner cùng đọc System.in.
- Biên dịch thành công bằng `javac --release 11`.
- DateValidationTestDriver: 15 passed, 0 failed.
- DateInputTestDriver: 4 passed, 0 failed.
- TourCrudTestDriver: 11 passed, 0 failed.
- TourListTestDriver: 14 passed, 0 failed.
- DateMenuIntegrationTestDriver: 4 passed, 0 failed.
- Tổng: 48 passed, 0 failed.
- Đã kiểm tra thêm/sửa qua menu, nhập lại ngày sai, lưu/nạp lại và hủy an toàn khi hết dữ liệu.
- Chờ reviewer kiểm tra; chưa xác nhận đã được duyệt.