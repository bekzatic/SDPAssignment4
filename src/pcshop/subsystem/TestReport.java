package pcshop.subsystem;

// Result of a burn-in test. Plain data.
public record TestReport(String serialNumber, int peakLoadWatts, int psuWatts, boolean passed, String summary) {
}
