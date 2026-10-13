package pcshop.model;

// The three kinds of computers the shop builds (same three types as the
// Bridge assignment).
public enum PcType {
    OFFICE("Office PC"),
    GAMING("Gaming PC"),
    WORKSTATION("Workstation");

    private final String displayName;

    PcType(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
