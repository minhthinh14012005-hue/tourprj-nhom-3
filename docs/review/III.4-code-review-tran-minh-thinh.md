# III.4 — Rà soát ràng buộc cập nhật và trùng mã

**Người review:** Trần Minh Thịnh (B23DCKD065)  
**Ngày rà soát:** 21/11/2026  
**Phạm vi:** `DestinationList.updateDestination`, `TourList.updateTour`, `searchId` và các ràng buộc ID.

## Kết quả rà soát

| Hạng mục | Kết quả | Nhận xét |
|---|---|---|
| `DestinationList.searchId` | Đạt | Tìm không phân biệt hoa thường, trả chỉ số hoặc `-1`, xử lý `null`/rỗng an toàn. |
| `DestinationList.updateDestination` | Đạt một phần | Không cho ID trùng với bản ghi khác; cho phép giữ ID hiện tại; cập nhật trực tiếp đối tượng để không làm mất liên kết Tour. |
| Dữ liệu Destination khi cập nhật | Đạt ở luồng menu | ID/tên/region không rỗng, không chứa dấu phẩy; `baseCost` hữu hạn và lớn hơn 0. |
| `TourList.searchId` | Đạt | Tìm không phân biệt hoa thường và trả `-1` khi không có kết quả. |
| `TourList.updateTour` | Chưa thể nghiệm thu | Phương thức vẫn là skeleton trên `main`; cần kiểm tra giá, chuỗi rỗng, ID trùng và giữ liên kết Destination sau khi thành viên II.7 hoàn thiện. |
| Setter trực tiếp của entity | Cần bổ sung | `Destination`/`Tour` setter hiện chưa tự kiểm tra dữ liệu; invariant mới được bảo vệ ở luồng nhập của list. |

## Kết luận

Phần `DestinationList` đã đủ điều kiện để reviewer tiếp tục kiểm thử Test 01, 03, 04 và 05 sau khi merge branch. Chưa đánh dấu III.4 hoàn thành tuyệt đối cho đến khi `TourList.updateTour` được triển khai và có test cập nhật hợp lệ/không hợp lệ.

## Việc cần theo dõi

1. Thành viên phụ trách II.7 triển khai `updateTour` với kiểm tra ID trùng với bản ghi khác.
2. Thêm test cho cập nhật Tour giữ đúng `Destination` object.
3. Cân nhắc đưa kiểm tra cơ bản vào setter hoặc quy định rõ setter chỉ được gọi sau lớp nhập liệu đã validate.
