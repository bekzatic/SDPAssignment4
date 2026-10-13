package pcshop.facade;

import pcshop.subsystem.AssemblyWorkshop;
import pcshop.subsystem.BurnInTester;
import pcshop.subsystem.CompatibilityChecker;
import pcshop.subsystem.CourierService;
import pcshop.subsystem.Inventory;
import pcshop.subsystem.OsInstaller;
import pcshop.subsystem.PartCatalog;
import pcshop.subsystem.PriceCalculator;

import java.util.Map;
import java.util.Objects;

// The eight subsystem objects the Facade coordinates, grouped into one
// parameter object (so the Facade constructor doesn't take 8 arguments).
// Tests use the with...() methods to swap a single subsystem.
public record ShopSubsystems(PartCatalog catalog, Inventory inventory, CompatibilityChecker compatibilityChecker,
                             PriceCalculator priceCalculator, AssemblyWorkshop workshop, OsInstaller osInstaller,
                             BurnInTester burnInTester, CourierService courier) {

    static final int STANDARD_STOCK_PER_PART = 3;
    // The top GPU is rare: only one in the warehouse.
    static final Map<String, Integer> LIMITED_STOCK = Map.of("GPU-4080S", 1);

    public ShopSubsystems {
        Objects.requireNonNull(catalog, "catalog");
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(compatibilityChecker, "compatibilityChecker");
        Objects.requireNonNull(priceCalculator, "priceCalculator");
        Objects.requireNonNull(workshop, "workshop");
        Objects.requireNonNull(osInstaller, "osInstaller");
        Objects.requireNonNull(burnInTester, "burnInTester");
        Objects.requireNonNull(courier, "courier");
    }

    public static ShopSubsystems standard() {
        PartCatalog catalog = new PartCatalog();
        return new ShopSubsystems(catalog, stockedInventory(catalog), new CompatibilityChecker(),
                new PriceCalculator(), new AssemblyWorkshop(), new OsInstaller(),
                new BurnInTester(), new CourierService());
    }

    public ShopSubsystems withOsInstaller(OsInstaller replacement) {
        return new ShopSubsystems(catalog, inventory, compatibilityChecker, priceCalculator,
                workshop, replacement, burnInTester, courier);
    }

    public ShopSubsystems withBurnInTester(BurnInTester replacement) {
        return new ShopSubsystems(catalog, inventory, compatibilityChecker, priceCalculator,
                workshop, osInstaller, replacement, courier);
    }

    private static Inventory stockedInventory(PartCatalog catalog) {
        Inventory inventory = new Inventory();
        catalog.allParts().forEach(part -> inventory.restock(part.sku(),
                LIMITED_STOCK.getOrDefault(part.sku(), STANDARD_STOCK_PER_PART)));
        return inventory;
    }
}
