# Requirements traceability

| Requirement | Design location | Verification |
|---|---|---|
| Manage Destination and Tour; one Tour has one Destination | `Destination`, `Tour`, association in UML | Test 01, 08 |
| Load and save text files | `DestinationList` and `TourList` contracts; file flow | Test 01, 05, 11, 12 |
| Search IDs returns index or `-1` | `searchId` contracts | Test 03, 09 |
| Sort tours by destination name then ID | `Tour.compareTo`, `TourList.listTours` | Test 06 |
| Search partial destination name case-insensitively | `TourList.printBasedDestinationName` | Test 07 |
| Invalid input must not stop the application | `Menu`, validation rules | Test 02, 08, 10, 12 |
| Java OOP and collections | encapsulated fields; list inheritance; `Comparable` | Code review + report |

## Non-negotiable rules

- IDs and required text fields cannot be blank; destination `baseCost` is greater than zero; tour `price` is non-negative.
- Use `equals` or `equalsIgnoreCase` for String comparisons, never `==`.
- `toString()` serializes a record for its data file. `screenString()` is only for readable console output.
- File I/O uses try-with-resources and reports the file name and line number for malformed records.
