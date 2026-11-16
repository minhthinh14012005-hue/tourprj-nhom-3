# File flow and error policy

```mermaid
flowchart TD
  A[Start] --> B[Load destinations.txt]
  B --> C{Each line valid?}
  C -- yes --> D[Build DestinationList]
  C -- no --> E[Report filename and line; continue safely]
  D --> F[Load tours.txt]
  F --> G{Five fields and known destination ID?}
  G -- yes --> H[Build Tour linked to Destination]
  G -- no --> I[Report filename and line; skip invalid record]
  H --> J[Display console menu]
  I --> J
  E --> F
  J --> K{Save option 5 or 11?}
  K -- yes --> L[Write records with try-with-resources]
  K -- no --> M[Continue or exit without implicit save]
  L --> J
```

## File schemas

`destinations.txt`: `destinationID,destinationName,region,baseCost`.

`tours.txt`: `tourID,destinationID,tourName,departureDate,price`.

No field may include a comma in this version. Whitespace around values is trimmed on input. A malformed line is not silently accepted.
