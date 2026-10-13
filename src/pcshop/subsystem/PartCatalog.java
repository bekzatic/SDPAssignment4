package pcshop.subsystem;

import pcshop.model.Part;
import pcshop.model.PartType;
import pcshop.model.PcType;
import pcshop.model.Platform;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

// SUBSYSTEM CLASS #1 - knows every part the shop sells and which parts make
// up each standard build. It does not check stock, prices or compatibility.
public class PartCatalog {

    private static final String LGA_1700 = "LGA1700";
    private static final String AM5 = "AM5";

    private final Map<String, Part> partsBySku = new LinkedHashMap<>();

    public PartCatalog() {
        addIntelParts();
        addAmdParts();
        addSharedParts();
    }

    public Collection<Part> allParts() {
        return List.copyOf(partsBySku.values());
    }

    public Part findBySku(String sku) {
        Part part = partsBySku.get(sku);
        if (part == null) {
            throw new NoSuchElementException("Unknown SKU: " + sku);
        }
        return part;
    }

    // The standard build for a PC type on a platform. The CPU tier and the
    // motherboard come from the platform, everything else from the PC type.
    public List<Part> standardBuild(PcType pcType, Platform platform) {
        String prefix = platform == Platform.INTEL ? "INT" : "AMD";
        List<String> skus = switch (pcType) {
            case OFFICE -> List.of(prefix + "-CPU-ENTRY", prefix + "-BOARD",
                    "RAM-16", "SSD-512", "PSU-450", "CASE-MINI");
            case GAMING -> List.of(prefix + "-CPU-MID", prefix + "-BOARD",
                    "RAM-32", "GPU-4070S", "SSD-1TB", "PSU-750", "CASE-MID");
            case WORKSTATION -> List.of(prefix + "-CPU-HIGH", prefix + "-BOARD",
                    "RAM-64", "GPU-4080S", "SSD-2TB", "PSU-1000", "CASE-TOWER");
        };
        return skus.stream().map(this::findBySku).toList();
    }

    private void addIntelParts() {
        add(new Part("INT-CPU-ENTRY", PartType.CPU, "Intel Core i3-14100", LGA_1700, 60, 65_000));
        add(new Part("INT-CPU-MID", PartType.CPU, "Intel Core i5-14600K", LGA_1700, 125, 150_000));
        add(new Part("INT-CPU-HIGH", PartType.CPU, "Intel Core i9-14900K", LGA_1700, 253, 290_000));
        add(new Part("INT-BOARD", PartType.MOTHERBOARD, "ASUS PRIME B760M-A", LGA_1700, 15, 60_000));
    }

    private void addAmdParts() {
        add(new Part("AMD-CPU-ENTRY", PartType.CPU, "AMD Ryzen 5 8500G", AM5, 65, 75_000));
        add(new Part("AMD-CPU-MID", PartType.CPU, "AMD Ryzen 7 9700X", AM5, 65, 160_000));
        add(new Part("AMD-CPU-HIGH", PartType.CPU, "AMD Ryzen 9 9950X", AM5, 170, 300_000));
        add(new Part("AMD-BOARD", PartType.MOTHERBOARD, "MSI MAG B650 Tomahawk", AM5, 15, 95_000));
    }

    private void addSharedParts() {
        add(new Part("RAM-16", PartType.RAM, "16GB DDR5-5600", Part.NO_SOCKET, 6, 25_000));
        add(new Part("RAM-32", PartType.RAM, "32GB DDR5-6000", Part.NO_SOCKET, 10, 50_000));
        add(new Part("RAM-64", PartType.RAM, "64GB DDR5-6000", Part.NO_SOCKET, 20, 100_000));
        add(new Part("GPU-4070S", PartType.GPU, "NVIDIA GeForce RTX 4070 SUPER", Part.NO_SOCKET, 220, 330_000));
        add(new Part("GPU-4080S", PartType.GPU, "NVIDIA GeForce RTX 4080 SUPER", Part.NO_SOCKET, 320, 560_000));
        add(new Part("SSD-512", PartType.STORAGE, "512GB NVMe SSD", Part.NO_SOCKET, 5, 22_000));
        add(new Part("SSD-1TB", PartType.STORAGE, "1TB NVMe SSD", Part.NO_SOCKET, 6, 38_000));
        add(new Part("SSD-2TB", PartType.STORAGE, "2TB NVMe SSD", Part.NO_SOCKET, 8, 70_000));
        add(new Part("PSU-450", PartType.PSU, "450W 80+ Bronze", Part.NO_SOCKET, 450, 20_000));
        add(new Part("PSU-750", PartType.PSU, "750W 80+ Gold", Part.NO_SOCKET, 750, 45_000));
        add(new Part("PSU-1000", PartType.PSU, "1000W 80+ Gold", Part.NO_SOCKET, 1000, 70_000));
        add(new Part("CASE-MINI", PartType.CASE, "Mini Tower Case", Part.NO_SOCKET, 5, 18_000));
        add(new Part("CASE-MID", PartType.CASE, "Mid Tower Case (3 fans)", Part.NO_SOCKET, 8, 35_000));
        add(new Part("CASE-TOWER", PartType.CASE, "Full Tower Case (5 fans)", Part.NO_SOCKET, 12, 55_000));
    }

    private void add(Part part) {
        partsBySku.put(part.sku(), part);
    }
}
