package pcshop.model;

// Operating systems the shop can pre-install, with their license price.
public enum OperatingSystem {
    WINDOWS_11_HOME("Windows 11 Home", 75_000),
    WINDOWS_11_PRO("Windows 11 Pro", 110_000),
    UBUNTU_24_04("Ubuntu 24.04 LTS", 0);

    private final String displayName;
    private final int licensePriceKzt;

    OperatingSystem(String displayName, int licensePriceKzt) {
        this.displayName = displayName;
        this.licensePriceKzt = licensePriceKzt;
    }

    public String displayName() {
        return displayName;
    }

    public int licensePriceKzt() {
        return licensePriceKzt;
    }
}
