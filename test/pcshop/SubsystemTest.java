package pcshop;

import pcshop.model.Part;
import pcshop.model.PcType;
import pcshop.model.Platform;
import pcshop.subsystem.CompatibilityChecker;
import pcshop.subsystem.CourierService;
import pcshop.subsystem.Inventory;
import pcshop.subsystem.OutOfStockException;
import pcshop.subsystem.PartCatalog;
import pcshop.subsystem.Reservation;
import testutil.Check;

import java.time.LocalDate;
import java.util.List;

// Uses the subsystem classes directly, without the Facade - proves they
// still work on their own (advanced clients can bypass the Facade).
public class SubsystemTest {

    public static void main(String[] args) {
        everyStandardBuildIsCompatible();
        checkerFindsSocketMismatchAndWeakPsu();
        checkerFindsMissingParts();
        inventoryReserveAndRelease();
        courierCoversOnlyKnownCities();
        Check.printSummary("SubsystemTest");
    }

    private static void everyStandardBuildIsCompatible() {
        System.out.println("Catalog + CompatibilityChecker");
        PartCatalog catalog = new PartCatalog();
        CompatibilityChecker checker = new CompatibilityChecker();
        for (PcType pcType : PcType.values()) {
            for (Platform platform : Platform.values()) {
                Check.that(pcType + " on " + platform + " is compatible",
                        checker.isCompatible(catalog.standardBuild(pcType, platform)));
            }
        }
    }

    private static void checkerFindsSocketMismatchAndWeakPsu() {
        System.out.println("Bad custom build");
        PartCatalog catalog = new PartCatalog();
        List<Part> build = List.of(catalog.findBySku("INT-CPU-HIGH"), catalog.findBySku("AMD-BOARD"),
                catalog.findBySku("RAM-64"), catalog.findBySku("GPU-4080S"), catalog.findBySku("SSD-2TB"),
                catalog.findBySku("PSU-450"), catalog.findBySku("CASE-TOWER"));
        List<String> problems = new CompatibilityChecker().findProblems(build);
        Check.equal("Two problems", 2, problems.size());
        Check.that("Socket problem reported", problems.get(0).contains("socket"));
        Check.that("PSU problem reported", problems.get(1).contains("PSU"));
    }

    private static void checkerFindsMissingParts() {
        System.out.println("Incomplete build");
        PartCatalog catalog = new PartCatalog();
        List<Part> build = List.of(catalog.findBySku("AMD-CPU-MID"), catalog.findBySku("AMD-BOARD"));
        Check.that("Build without PSU/case/RAM/storage is not compatible",
                !new CompatibilityChecker().isCompatible(build));
    }

    private static void inventoryReserveAndRelease() {
        System.out.println("Inventory");
        PartCatalog catalog = new PartCatalog();
        Inventory inventory = new Inventory();
        inventory.restock("RAM-16", 2);
        List<Part> twoSticks = List.of(catalog.findBySku("RAM-16"), catalog.findBySku("RAM-16"));
        Reservation reservation = inventory.reserve(twoSticks);
        Check.equal("Both sticks reserved", 0, inventory.stockOf("RAM-16"));
        Check.throwsException("Nothing left to reserve", () -> inventory.reserve(twoSticks),
                OutOfStockException.class);
        inventory.release(reservation);
        Check.equal("Release puts them back", 2, inventory.stockOf("RAM-16"));
        Check.throwsException("Releasing twice fails", () -> inventory.release(reservation),
                IllegalStateException.class);
    }

    private static void courierCoversOnlyKnownCities() {
        System.out.println("Courier");
        CourierService courier = new CourierService(() -> LocalDate.of(2026, 10, 13));
        Check.that("Delivers to Astana", courier.deliversTo("Astana"));
        Check.that("Does not deliver to Aktau", !courier.deliversTo("Aktau"));
        Check.equal("Shymkent takes 3 days", LocalDate.of(2026, 10, 16),
                courier.ship("PC-2026-0001", "Shymkent").estimatedDelivery());
    }
}
