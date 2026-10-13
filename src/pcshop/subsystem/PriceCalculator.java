package pcshop.subsystem;

import pcshop.model.OperatingSystem;
import pcshop.model.Part;

import java.util.List;

// SUBSYSTEM CLASS #4 - final price = parts + assembly fee + OS license.
public class PriceCalculator {

    static final int ASSEMBLY_FEE_KZT = 25_000;

    public int totalPriceKzt(List<Part> parts, OperatingSystem operatingSystem) {
        return partsPriceKzt(parts) + ASSEMBLY_FEE_KZT + operatingSystem.licensePriceKzt();
    }

    public int partsPriceKzt(List<Part> parts) {
        return parts.stream().mapToInt(Part::priceKzt).sum();
    }
}
