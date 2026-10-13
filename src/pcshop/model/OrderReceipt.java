package pcshop.model;

import java.time.LocalDate;

// What the customer gets back from the shop. Only customer-level data:
// no Reservation, no AssembledPc, no TestReport - those stay inside the
// subsystem.
public record OrderReceipt(String orderNumber, String customerName, String pcDescription,
                           String serialNumber, int totalPriceKzt,
                           String trackingNumber, LocalDate estimatedDelivery) {

    @Override
    public String toString() {
        return String.join(System.lineSeparator(),
                "  Order      " + orderNumber + " for " + customerName,
                "  Computer   " + pcDescription + ", serial " + serialNumber,
                String.format("  Total      %,d KZT", totalPriceKzt),
                "  Delivery   " + trackingNumber + ", expected " + estimatedDelivery);
    }
}
