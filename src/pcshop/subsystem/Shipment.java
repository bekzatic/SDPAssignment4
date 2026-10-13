package pcshop.subsystem;

import java.time.LocalDate;

// A booked delivery. Plain data.
public record Shipment(String trackingNumber, String city, LocalDate estimatedDelivery) {
}
