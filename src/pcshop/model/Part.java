package pcshop.model;

import java.util.Objects;

// One part from the catalog. A plain data structure (record): it carries
// values and has no hidden behaviour.
//
// "watts" is the power DRAW for normal components and the rated OUTPUT for a
// power supply - isPowerSupply() tells the two apart.
public record Part(String sku, PartType type, String model, String socket, int watts, int priceKzt) {

    public static final String NO_SOCKET = "-";

    public Part {
        Objects.requireNonNull(sku, "sku");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(model, "model");
        Objects.requireNonNull(socket, "socket");
        if (watts < 0 || priceKzt < 0) {
            throw new IllegalArgumentException("Watts and price can't be negative: " + sku);
        }
    }

    public boolean isPowerSupply() {
        return type == PartType.PSU;
    }

    @Override
    public String toString() {
        return String.format("%-11s %-34s %5dW %,9d KZT", type, model, watts, priceKzt);
    }
}
