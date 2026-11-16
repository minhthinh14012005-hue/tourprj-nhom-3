# Console menu specification

| Option | Action | Owner module |
|---:|---|---|
| 1 | List all Destinations | `DestinationList.listDestinations` |
| 2 | Add a new Destination | `DestinationList.addDestination` |
| 3 | Search Destination by ID | `DestinationList.searchId` |
| 4 | Update Destination | `DestinationList.updateDestination` |
| 5 | Save Destinations | `DestinationList.saveToFile` |
| 6 | List Tours sorted by destination name then ID | `TourList.listTours`, `Tour.compareTo` |
| 7 | Search Tours by partial destination name | `TourList.printBasedDestinationName` |
| 8 | Add a new Tour | `TourList.addTour` |
| 9 | Remove Tour by ID | `TourList.removeTour` |
| 10 | Update Tour by ID | `TourList.updateTour` |
| 11 | Save Tours | `TourList.saveToFile` |
| 0 | Exit without implicit save | `TourManager` |

## Interaction rules

- Show the menu again after every action.
- If a numeric input cannot be parsed or is outside the valid range, show a concise error and request it again.
- When selecting a Destination for a Tour, show an indexed list and use `Menu.intGetChoice` / `Menu.refGetChoice`.
- An ID search with no match reports “not found”; it is not an exception.
