package pcshop.subsystem;

import pcshop.model.Part;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// SUBSYSTEM CLASS #2 - warehouse stock. Reserving takes parts off the shelf,
// releasing puts them back. Queries (stockOf, isInStock) never change state;
// commands (restock, reserve, release) never answer questions.
public class Inventory {

    private final Map<String, Integer> stockBySku = new HashMap<>();
    private final Set<Integer> activeReservationIds = new HashSet<>();
    private int nextReservationId = 1;

    public void restock(String sku, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive, got " + quantity);
        }
        stockBySku.merge(sku, quantity, Integer::sum);
    }

    public int stockOf(String sku) {
        return stockBySku.getOrDefault(sku, 0);
    }

    public boolean isInStock(List<Part> parts) {
        return findMissingPart(parts).isEmpty();
    }

    // All-or-nothing: either every part is reserved or nothing changes.
    public Reservation reserve(List<Part> parts) {
        findMissingPart(parts).ifPresent(missing -> {
            throw new OutOfStockException(missing.model());
        });
        parts.forEach(part -> stockBySku.merge(part.sku(), -1, Integer::sum));
        Reservation reservation = new Reservation(nextReservationId++, parts);
        activeReservationIds.add(reservation.id());
        return reservation;
    }

    public void release(Reservation reservation) {
        if (!activeReservationIds.remove(reservation.id())) {
            throw new IllegalStateException("Reservation " + reservation.id() + " is not active.");
        }
        reservation.parts().forEach(part -> stockBySku.merge(part.sku(), 1, Integer::sum));
    }

    // The first part there isn't enough of (a build may need the same SKU twice).
    private Optional<Part> findMissingPart(List<Part> parts) {
        Map<String, Long> neededBySku = countBySku(parts);
        return parts.stream()
                .filter(part -> stockOf(part.sku()) < neededBySku.get(part.sku()))
                .findFirst();
    }

    private static Map<String, Long> countBySku(List<Part> parts) {
        return parts.stream().map(Part::sku)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}
