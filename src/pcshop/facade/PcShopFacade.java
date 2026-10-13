package pcshop.facade;

import pcshop.model.OperatingSystem;
import pcshop.model.OrderReceipt;
import pcshop.model.Part;
import pcshop.model.PcOrder;
import pcshop.model.PcType;
import pcshop.model.Platform;
import pcshop.subsystem.AssembledPc;
import pcshop.subsystem.OutOfStockException;
import pcshop.subsystem.Reservation;
import pcshop.subsystem.Shipment;
import pcshop.subsystem.TestReport;

import java.util.List;
import java.util.Objects;

// FACADE. One small API for the customer:
//
//   quote(...)        - how much will it cost?
//   isAvailable(...)  - can the shop build it right now?
//   orderPc(order)    - build it, test it and ship it.
//
// Behind orderPc() there are eight subsystem classes that must be called in
// the right order, and the parts must go back on the shelf if any step fails.
// The Facade only COORDINATES: it does not pick parts, count stock or
// calculate prices itself - every rule stays in its subsystem class.
public class PcShopFacade {

    private final ShopSubsystems shop;
    private int nextOrderNumber = 1;

    public PcShopFacade() {
        this(ShopSubsystems.standard());
    }

    public PcShopFacade(ShopSubsystems shop) {
        this.shop = Objects.requireNonNull(shop, "shop");
    }

    public int quote(PcType pcType, Platform platform, OperatingSystem operatingSystem) {
        List<Part> parts = shop.catalog().standardBuild(pcType, platform);
        return shop.priceCalculator().totalPriceKzt(parts, operatingSystem);
    }

    public boolean isAvailable(PcType pcType, Platform platform) {
        return shop.inventory().isInStock(shop.catalog().standardBuild(pcType, platform));
    }

    public OrderReceipt orderPc(PcOrder order) {
        Objects.requireNonNull(order, "order");
        ensureDeliverable(order.deliveryCity());
        List<Part> parts = compatibleBuildFor(order);
        Reservation reservation = reserve(parts);
        try {
            AssembledPc pc = buildAndTest(reservation, order.operatingSystem());
            Shipment shipment = shop.courier().ship(pc.serialNumber(), order.deliveryCity());
            return receipt(order, pc, shipment);
        } catch (RuntimeException failure) {
            shop.inventory().release(reservation);
            throw asOrderFailure(failure);
        }
    }

    private void ensureDeliverable(String city) {
        if (!shop.courier().deliversTo(city)) {
            throw new OrderFailedException(OrderFailure.UNSUPPORTED_CITY,
                    "We don't deliver to " + city + ". Cities: " + shop.courier().coveredCities());
        }
    }

    private List<Part> compatibleBuildFor(PcOrder order) {
        List<Part> parts = shop.catalog().standardBuild(order.pcType(), order.platform());
        List<String> problems = shop.compatibilityChecker().findProblems(parts);
        if (!problems.isEmpty()) {
            throw new OrderFailedException(OrderFailure.INCOMPATIBLE_PARTS, String.join("; ", problems));
        }
        return parts;
    }

    private Reservation reserve(List<Part> parts) {
        try {
            return shop.inventory().reserve(parts);
        } catch (OutOfStockException outOfStock) {
            throw new OrderFailedException(OrderFailure.OUT_OF_STOCK, outOfStock.getMessage(), outOfStock);
        }
    }

    private AssembledPc buildAndTest(Reservation reservation, OperatingSystem operatingSystem) {
        AssembledPc pc = shop.workshop().assemble(reservation);
        shop.osInstaller().install(operatingSystem, pc);
        TestReport report = shop.burnInTester().run(pc);
        if (!report.passed()) {
            throw new OrderFailedException(OrderFailure.QUALITY_CHECK_FAILED,
                    "Burn-in test failed for " + pc.serialNumber() + ": " + report.summary());
        }
        return pc;
    }

    private OrderReceipt receipt(PcOrder order, AssembledPc pc, Shipment shipment) {
        String orderNumber = String.format("ORD-%04d", nextOrderNumber++);
        int totalPriceKzt = shop.priceCalculator().totalPriceKzt(pc.parts(), order.operatingSystem());
        return new OrderReceipt(orderNumber, order.customerName(), order.pcDescription(), pc.serialNumber(),
                totalPriceKzt, shipment.trackingNumber(), shipment.estimatedDelivery());
    }

    private static OrderFailedException asOrderFailure(RuntimeException failure) {
        if (failure instanceof OrderFailedException orderFailed) {
            return orderFailed;
        }
        return new OrderFailedException(OrderFailure.INTERNAL_ERROR,
                "Order could not be completed: " + failure.getMessage(), failure);
    }
}
