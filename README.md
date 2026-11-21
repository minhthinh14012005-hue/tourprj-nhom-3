# TourPrj - Nhóm 3

Java Console project for Topic 10: **Travel Tour Management System** (INT1332 - Lập trình hướng đối tượng).

## Design milestone

This repository contains the reviewed design package and a compilable six-class skeleton. Functional implementations begin only after the group approves the design artifacts in [`docs/design`](docs/design).

## Layout

- `TourPrj/src/main/java/edu/int1332/tourprj/` - six Java classes and `main` entry point.
- `TourPrj/data/` - initial text data matching the agreed formats.
- `docs/design/` - requirements traceability, UML source, file-flow, menu specification, test plan, and module contracts.

The rendered UML is available at [`docs/design/uml-class-diagram.png`](docs/design/uml-class-diagram.png); the editable source is [`docs/design/uml-class-diagram.svg`](docs/design/uml-class-diagram.svg).

## Compile the design skeleton

From `TourPrj`:

```powershell
javac -encoding UTF-8 -d out src/main/java/edu/int1332/tourprj/*.java
java -cp out edu.int1332.tourprj.TourManager
```

The skeleton deliberately does not implement the menu actions yet.

## Collaboration rule

Each owner works on their assigned module in a branch, requests the named reviewer, and merges only after the relevant test cases pass. The `main` branch is the shared integration baseline.
