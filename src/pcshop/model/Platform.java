package pcshop.model;

// Hardware platform the customer picks. Decides the CPU and motherboard.
public enum Platform {
    INTEL("Intel"),
    AMD("AMD");

    private final String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
