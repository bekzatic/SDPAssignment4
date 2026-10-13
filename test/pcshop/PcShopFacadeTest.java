package pcshop;

import pcshop.facade.OrderFailedException;
import pcshop.facade.OrderFailure;
import pcshop.facade.PcShopFacade;
import pcshop.facade.ShopSubsystems;
import pcshop.model.OperatingSystem;
import pcshop.model.OrderReceipt;
import pcshop.model.PcOrder;
import pcshop.model.PcType;
import pcshop.model.Platform;
import pcshop.subsystem.AssembledPc;
import pcshop.subsystem.BurnInTester;
import pcshop.subsystem.Inventory;
import pcshop.subsystem.OsInstaller;
import pcshop.subsystem.TestReport;
import testutil.Check;

// Checks the Facade from the outside: one call does the whole job, failures
// come back as OrderFailedException, and stock is put back when a step fails.
public class PcShopFacadeTest {

    public static void main(String[] args) {
        successfulOrderReturnsReceipt();
        orderTakesPartsFromStock();
        quoteDoesNotTouchStock();
        everyOrderGetsItsOwnNumbers();
        outOfStockIsReportedAndNothingIsReserved();
        unsupportedCityFailsBeforeReserving();
        failedBurnInPutsPartsBack();
        subsystemErrorIsTranslatedAndPartsGoBack();
        invalidOrdersAreRejected();
        Check.printSummary("PcShopFacadeTest");
    }

    private static void successfulOrderReturnsReceipt() {
        System.out.println("Successful order");
        PcShopFacade shop = new PcShopFacade();
        int quoted = shop.quote(PcType.GAMING, Platform.AMD, OperatingSystem.WINDOWS_11_HOME);
        OrderReceipt receipt = shop.orderPc(gamingOrder("Almaty"));
        Check.equal("Order number", "ORD-0001", receipt.orderNumber());
        Check.equal("Customer", "Bekzat", receipt.customerName());
        Check.equal("Price on receipt equals the quote", quoted, receipt.totalPriceKzt());
        Check.that("Tracking number contains the serial",
                receipt.trackingNumber().contains(receipt.serialNumber()));
        Check.that("Delivery date is set", receipt.estimatedDelivery() != null);
    }

    private static void orderTakesPartsFromStock() {
        System.out.println("Stock after an order");
        ShopSubsystems subsystems = ShopSubsystems.standard();
        Inventory inventory = subsystems.inventory();
        PcShopFacade shop = new PcShopFacade(subsystems);
        shop.orderPc(gamingOrder("Astana"));
        Check.equal("GPU stock 3 -> 2", 2, inventory.stockOf("GPU-4070S"));
        Check.equal("AMD CPU stock 3 -> 2", 2, inventory.stockOf("AMD-CPU-MID"));
        Check.equal("Intel CPU untouched", 3, inventory.stockOf("INT-CPU-MID"));
    }

    private static void quoteDoesNotTouchStock() {
        System.out.println("Quote is a query");
        ShopSubsystems subsystems = ShopSubsystems.standard();
        PcShopFacade shop = new PcShopFacade(subsystems);
        shop.quote(PcType.WORKSTATION, Platform.INTEL, OperatingSystem.WINDOWS_11_PRO);
        shop.isAvailable(PcType.WORKSTATION, Platform.INTEL);
        Check.equal("Stock unchanged after quote/isAvailable", 1, subsystems.inventory().stockOf("GPU-4080S"));
    }

    private static void everyOrderGetsItsOwnNumbers() {
        System.out.println("Unique numbers");
        PcShopFacade shop = new PcShopFacade();
        OrderReceipt first = shop.orderPc(gamingOrder("Astana"));
        OrderReceipt second = shop.orderPc(gamingOrder("Astana"));
        Check.that("Different order numbers", !first.orderNumber().equals(second.orderNumber()));
        Check.that("Different serial numbers", !first.serialNumber().equals(second.serialNumber()));
    }

    private static void outOfStockIsReportedAndNothingIsReserved() {
        System.out.println("Out of stock");
        ShopSubsystems subsystems = ShopSubsystems.standard();
        PcShopFacade shop = new PcShopFacade(subsystems);
        shop.orderPc(workstationOrder());
        Check.that("isAvailable is false once the only RTX 4080 SUPER is sold",
                !shop.isAvailable(PcType.WORKSTATION, Platform.INTEL));
        OrderFailure reason = failureOf(shop, workstationOrder());
        Check.equal("Second workstation fails", OrderFailure.OUT_OF_STOCK, reason);
        Check.equal("CPU not taken by the failed order", 2, subsystems.inventory().stockOf("INT-CPU-HIGH"));
    }

    private static void unsupportedCityFailsBeforeReserving() {
        System.out.println("Unsupported city");
        ShopSubsystems subsystems = ShopSubsystems.standard();
        PcShopFacade shop = new PcShopFacade(subsystems);
        Check.equal("Aktau is rejected", OrderFailure.UNSUPPORTED_CITY, failureOf(shop, gamingOrder("Aktau")));
        Check.equal("No GPU reserved", 3, subsystems.inventory().stockOf("GPU-4070S"));
    }

    private static void failedBurnInPutsPartsBack() {
        System.out.println("Burn-in failure rolls back");
        ShopSubsystems subsystems = ShopSubsystems.standard().withBurnInTester(new AlwaysFailingTester());
        PcShopFacade shop = new PcShopFacade(subsystems);
        Check.equal("Reason", OrderFailure.QUALITY_CHECK_FAILED, failureOf(shop, gamingOrder("Astana")));
        Check.equal("GPU back on the shelf", 3, subsystems.inventory().stockOf("GPU-4070S"));
        Check.equal("RAM back on the shelf", 3, subsystems.inventory().stockOf("RAM-32"));
    }

    private static void subsystemErrorIsTranslatedAndPartsGoBack() {
        System.out.println("Unexpected subsystem error");
        ShopSubsystems subsystems = ShopSubsystems.standard().withOsInstaller(new BrokenOsInstaller());
        PcShopFacade shop = new PcShopFacade(subsystems);
        Check.equal("IllegalStateException becomes INTERNAL_ERROR",
                OrderFailure.INTERNAL_ERROR, failureOf(shop, gamingOrder("Astana")));
        Check.equal("GPU back on the shelf", 3, subsystems.inventory().stockOf("GPU-4070S"));
    }

    private static void invalidOrdersAreRejected() {
        System.out.println("Invalid input");
        PcShopFacade shop = new PcShopFacade();
        Check.throwsException("Null order", () -> shop.orderPc(null), NullPointerException.class);
        Check.throwsException("Blank customer name", () -> new PcOrder(" ", PcType.OFFICE, Platform.AMD,
                OperatingSystem.UBUNTU_24_04, "Astana"), IllegalArgumentException.class);
        Check.throwsException("Missing platform", () -> new PcOrder("Bekzat", PcType.OFFICE, null,
                OperatingSystem.UBUNTU_24_04, "Astana"), NullPointerException.class);
    }

    private static OrderFailure failureOf(PcShopFacade shop, PcOrder order) {
        try {
            shop.orderPc(order);
            return null;
        } catch (OrderFailedException failure) {
            return failure.reason();
        }
    }

    private static PcOrder gamingOrder(String city) {
        return new PcOrder("Bekzat", PcType.GAMING, Platform.AMD, OperatingSystem.WINDOWS_11_HOME, city);
    }

    private static PcOrder workstationOrder() {
        return new PcOrder("Daniyar", PcType.WORKSTATION, Platform.INTEL, OperatingSystem.WINDOWS_11_PRO, "Astana");
    }

    // Test doubles: subsystem classes can be replaced because the Facade only
    // depends on them through ShopSubsystems.
    private static final class AlwaysFailingTester extends BurnInTester {
        @Override
        public TestReport run(AssembledPc pc) {
            return new TestReport(pc.serialNumber(), 0, 0, false, "fan failure (simulated)");
        }
    }

    private static final class BrokenOsInstaller extends OsInstaller {
        @Override
        public void install(OperatingSystem operatingSystem, AssembledPc pc) {
            throw new IllegalStateException("installer crashed (simulated)");
        }
    }
}
