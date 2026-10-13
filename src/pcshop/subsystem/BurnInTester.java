package pcshop.subsystem;

import pcshop.model.Part;

// SUBSYSTEM CLASS #7 - quality control. Simulates a stress test: every part
// at full load plus a 20% spike must stay within the power supply's rating,
// and the PC must boot (an OS must be installed).
public class BurnInTester {

    static final double PEAK_LOAD_FACTOR = 1.2;

    public TestReport run(AssembledPc pc) {
        int peakLoadWatts = (int) Math.ceil(CompatibilityChecker.powerDrawWatts(pc.parts()) * PEAK_LOAD_FACTOR);
        int psuWatts = pc.parts().stream().filter(Part::isPowerSupply).mapToInt(Part::watts).sum();
        if (pc.installedOs().isEmpty()) {
            return new TestReport(pc.serialNumber(), peakLoadWatts, psuWatts, false, "PC does not boot: no OS");
        }
        boolean powerOk = peakLoadWatts <= psuWatts;
        String summary = powerOk
                ? "Stable at " + peakLoadWatts + "W peak on a " + psuWatts + "W PSU"
                : "PSU overloaded: " + peakLoadWatts + "W peak on a " + psuWatts + "W PSU";
        return new TestReport(pc.serialNumber(), peakLoadWatts, psuWatts, powerOk, summary);
    }
}
