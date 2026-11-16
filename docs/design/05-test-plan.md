# Mandatory test plan

| Test | Scenario | Expected result |
|---:|---|---|
| 01 | Load and list initial destinations | All three valid records are shown. |
| 02 | Add destination; try duplicate ID, empty name, invalid number | Invalid input is rejected and re-requested. |
| 03 | Search existing and missing destination ID | Correct index/record or `-1`/not-found message. |
| 04 | Update destination | Modified values persist in memory and display. |
| 05 | Save destinations and reload | Saved file reloads with the same records. |
| 06 | Sort tours where destination names repeat | Destination name, then tour ID, determines order. |
| 07 | Partial destination-name search | Case-insensitive matches show; no-match is clear. |
| 08 | Add tour via destination selector; duplicate/invalid reference | Only a valid existing Destination can be used. |
| 09 | Remove missing then existing tour ID | Missing item is safe; existing item is removed. |
| 10 | Update tour with valid and invalid values | Only valid changes are retained. |
| 11 | Save tours, restart, and reload | Saved tours remain linked to destinations. |
| 12 | Missing file, malformed row, text for numeric menu input | Error is clear and application continues safely. |

For each executed test, record: tester, date, input data, observed result, pass/fail, bug ID, and screenshot path.
