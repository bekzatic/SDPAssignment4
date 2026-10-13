package pcshop.model;

import java.util.Objects;

// Everything the customer tells the shop, bundled into one object.
// Without it PcShopFacade.orderPc(...) would need five arguments.
public record PcOrder(String customerName, PcType pcType, Platform platform,
                      OperatingSystem operatingSystem, String deliveryCity) {

    public PcOrder {
        requireText(customerName, "customerName");
        Objects.requireNonNull(pcType, "pcType");
        Objects.requireNonNull(platform, "platform");
        Objects.requireNonNull(operatingSystem, "operatingSystem");
        requireText(deliveryCity, "deliveryCity");
    }

    public String pcDescription() {
        return pcType.displayName() + " (" + platform.displayName() + ", " + operatingSystem.displayName() + ")";
    }

    private static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty.");
        }
    }
}
