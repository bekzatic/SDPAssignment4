package pcshop;

import pcshop.facade.OrderFailedException;
import pcshop.facade.PcShopFacade;
import pcshop.model.OperatingSystem;
import pcshop.model.OrderReceipt;
import pcshop.model.PcOrder;
import pcshop.model.PcType;
import pcshop.model.Platform;

// CLIENT. Talks only to PcShopFacade - it never creates or calls a subsystem
// class (no imports from pcshop.subsystem in this file).
public class ShopDemo {

    public static void main(String[] args) {
        PcShopFacade shop = new PcShopFacade();

        section("1) Price list (Windows 11 Home)");
        printPriceList(shop);

        section("2) Successful orders - one call each");
        placeOrder(shop, new PcOrder("Bekzat", PcType.GAMING, Platform.AMD,
                OperatingSystem.WINDOWS_11_HOME, "Almaty"));
        placeOrder(shop, new PcOrder("Aruzhan", PcType.OFFICE, Platform.INTEL,
                OperatingSystem.UBUNTU_24_04, "Astana"));
        placeOrder(shop, new PcOrder("Daniyar", PcType.WORKSTATION, Platform.INTEL,
                OperatingSystem.WINDOWS_11_PRO, "Karaganda"));

        section("3) Out of stock - only one RTX 4080 SUPER was in the warehouse");
        System.out.println("  AMD workstation available? " + shop.isAvailable(PcType.WORKSTATION, Platform.AMD));
        placeOrder(shop, new PcOrder("Madina", PcType.WORKSTATION, Platform.AMD,
                OperatingSystem.WINDOWS_11_PRO, "Shymkent"));

        section("4) City the courier doesn't cover");
        placeOrder(shop, new PcOrder("Yerlan", PcType.OFFICE, Platform.AMD,
                OperatingSystem.WINDOWS_11_HOME, "Aktau"));
    }

    private static void printPriceList(PcShopFacade shop) {
        for (PcType pcType : PcType.values()) {
            for (Platform platform : Platform.values()) {
                int price = shop.quote(pcType, platform, OperatingSystem.WINDOWS_11_HOME);
                System.out.printf("  %-12s %-6s %,10d KZT%n", pcType.displayName(), platform.displayName(), price);
            }
        }
    }

    private static void placeOrder(PcShopFacade shop, PcOrder order) {
        System.out.println("> " + order.customerName() + " orders: " + order.pcDescription()
                + " to " + order.deliveryCity());
        try {
            OrderReceipt receipt = shop.orderPc(order);
            System.out.println(receipt);
        } catch (OrderFailedException failure) {
            System.out.println("  FAILED [" + failure.reason() + "] " + failure.getMessage());
        }
    }

    private static void section(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
