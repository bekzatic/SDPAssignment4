package pcshop;

import pcshop.model.Part;
import pcshop.subsystem.CompatibilityChecker;
import pcshop.subsystem.Inventory;
import pcshop.subsystem.PartCatalog;

import java.util.List;

// ADVANCED CLIENT. A shop technician who needs something the Facade doesn't
// offer: checking a custom part list. The subsystem classes don't know about
// the Facade, so they can still be used directly - that's part of the
// pattern, the Facade is a shortcut, not a wall.
public class AdvancedClientDemo {

    public static void main(String[] args) {
        PartCatalog catalog = new PartCatalog();
        CompatibilityChecker checker = new CompatibilityChecker();

        section("Custom build: Intel CPU on an AMD motherboard, small PSU, RTX 4080 SUPER");
        List<Part> customBuild = List.of(
                catalog.findBySku("INT-CPU-HIGH"),
                catalog.findBySku("AMD-BOARD"),
                catalog.findBySku("RAM-64"),
                catalog.findBySku("GPU-4080S"),
                catalog.findBySku("SSD-2TB"),
                catalog.findBySku("PSU-450"),
                catalog.findBySku("CASE-TOWER"));
        customBuild.forEach(part -> System.out.println("  " + part));
        System.out.println("  Power draw: " + CompatibilityChecker.powerDrawWatts(customBuild) + "W");
        checker.findProblems(customBuild).forEach(problem -> System.out.println("  PROBLEM: " + problem));

        section("Fixed build: AMD CPU, 1000W PSU");
        List<Part> fixedBuild = List.of(
                catalog.findBySku("AMD-CPU-HIGH"),
                catalog.findBySku("AMD-BOARD"),
                catalog.findBySku("RAM-64"),
                catalog.findBySku("GPU-4080S"),
                catalog.findBySku("SSD-2TB"),
                catalog.findBySku("PSU-1000"),
                catalog.findBySku("CASE-TOWER"));
        System.out.println("  Compatible? " + checker.isCompatible(fixedBuild));

        section("Stock check on a separate warehouse");
        Inventory inventory = new Inventory();
        inventory.restock("GPU-4080S", 2);
        System.out.println("  RTX 4080 SUPER in stock: " + inventory.stockOf("GPU-4080S"));
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
