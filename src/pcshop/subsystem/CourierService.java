package pcshop.subsystem;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

// SUBSYSTEM CLASS #8 - books delivery of a finished PC to one of the cities
// the courier covers.
public class CourierService {

    private static final Map<String, Integer> DELIVERY_DAYS_BY_CITY = Map.of(
            "Astana", 1,
            "Almaty", 2,
            "Karaganda", 2,
            "Shymkent", 3);

    private final Supplier<LocalDate> today;
    private int nextTrackingNumber = 1;

    public CourierService() {
        this(LocalDate::now);
    }

    // The date source can be replaced, so tests get a fixed "today".
    public CourierService(Supplier<LocalDate> today) {
        this.today = today;
    }

    public Set<String> coveredCities() {
        return new TreeSet<>(DELIVERY_DAYS_BY_CITY.keySet());
    }

    public boolean deliversTo(String city) {
        return DELIVERY_DAYS_BY_CITY.containsKey(city);
    }

    public Shipment ship(String serialNumber, String city) {
        if (!deliversTo(city)) {
            throw new IllegalArgumentException("No delivery to " + city);
        }
        String trackingNumber = String.format("KZ-%05d-%s", nextTrackingNumber++, serialNumber);
        LocalDate estimatedDelivery = today.get().plusDays(DELIVERY_DAYS_BY_CITY.get(city));
        return new Shipment(trackingNumber, city, estimatedDelivery);
    }
}
