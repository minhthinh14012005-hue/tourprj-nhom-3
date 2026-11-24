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

## Bổ sung hỗ trợ II.6 — 18/11/2026

- Người bổ sung: Lê Thuần Trường.
- Menu.readLine(String prompt): hiển thị lời nhắc, đọc một dòng
  và loại bỏ khoảng trắng hai đầu.
- Các phương thức nhập console dùng chung Scanner của Menu.
- Đã tích hợp Menu/TourManager của Nguyễn Hữu Đức từ PR #5 và kiểm thử lựa chọn Destination bằng Menu thực tế (24/11/2026).

## Hoàn tất tích hợp II.6 — 24/11/2026

- Người thực hiện: Lê Thuần Trường.
- Reviewer theo Plan: Đàm Trọng Đạt (chờ review).
- Đã tích hợp Menu/TourManager từ PR #5, giữ thông tin tác giả Nguyễn Hữu Đức.
- Đã kiểm tra thêm, sửa, liệt kê và chọn Destination bằng Menu thực tế.
- Từ chối mã trùng, chuỗi rỗng, dấu phẩy và chi phí không hợp lệ.
- Cho phép giữ mã hoặc đổi sang mã chưa dùng; giữ nguyên đối tượng Destination và liên kết với Tour.
- Đã kiểm tra luồng menu 1, 2, 4 và lưu/nạp lại dữ liệu.
- Biên dịch thành công bằng `javac --release 11`.
- DestinationCrudTestDriver: 10 passed, 0 failed.
- DestinationListTestDriver: 9 passed, 0 failed.
- TourListTestDriver: 14 passed, 0 failed.
- Tổng: 33 passed, 0 failed.
- Log: `docs/test-results/II6_Destination_Test_Log.txt`.
- Đã đồng bộ UML và ảnh PNG/SVG; chờ reviewer duyệt.