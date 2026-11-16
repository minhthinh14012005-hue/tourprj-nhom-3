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
