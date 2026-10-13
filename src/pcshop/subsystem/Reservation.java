package pcshop.subsystem;

import pcshop.model.Part;

import java.util.List;

// Parts put aside for one build. Returned by Inventory.reserve(...) and
// handed back to Inventory.release(...) if the build is cancelled.
public record Reservation(int id, List<Part> parts) {

    public Reservation {
        parts = List.copyOf(parts);
    }
}
