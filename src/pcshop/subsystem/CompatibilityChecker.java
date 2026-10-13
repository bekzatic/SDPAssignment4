package pcshop.subsystem;

import pcshop.model.Part;
import pcshop.model.PartType;

import java.util.ArrayList;
import java.util.List;

// SUBSYSTEM CLASS #3 - checks that a list of parts can actually work
// together: one of each core part, matching CPU/motherboard sockets and a
// power supply with enough headroom.
public class CompatibilityChecker {

    static final double PSU_HEADROOM = 1.3;

    private static final List<PartType> EXACTLY_ONE = List.of(
            PartType.CPU, PartType.MOTHERBOARD, PartType.PSU, PartType.CASE);
    private static final List<PartType> AT_LEAST_ONE = List.of(PartType.RAM, PartType.STORAGE);

    public List<String> findProblems(List<Part> parts) {
        List<String> problems = new ArrayList<>();
        problems.addAll(findMissingOrDuplicateParts(parts));
        if (problems.isEmpty()) {
            problems.addAll(findSocketMismatch(parts));
            problems.addAll(findWeakPowerSupply(parts));
        }
        return problems;
    }

    public boolean isCompatible(List<Part> parts) {
        return findProblems(parts).isEmpty();
    }

    public static int powerDrawWatts(List<Part> parts) {
        return parts.stream().filter(part -> !part.isPowerSupply()).mapToInt(Part::watts).sum();
    }

    private List<String> findMissingOrDuplicateParts(List<Part> parts) {
        List<String> problems = new ArrayList<>();
        for (PartType type : EXACTLY_ONE) {
            long count = countOf(type, parts);
            if (count != 1) {
                problems.add("Build needs exactly one " + type + ", found " + count);
            }
        }
        for (PartType type : AT_LEAST_ONE) {
            if (countOf(type, parts) == 0) {
                problems.add("Build needs at least one " + type);
            }
        }
        return problems;
    }

    private List<String> findSocketMismatch(List<Part> parts) {
        Part cpu = onlyPartOf(PartType.CPU, parts);
        Part motherboard = onlyPartOf(PartType.MOTHERBOARD, parts);
        if (cpu.socket().equals(motherboard.socket())) {
            return List.of();
        }
        return List.of("CPU socket " + cpu.socket() + " does not fit motherboard socket " + motherboard.socket());
    }

    private List<String> findWeakPowerSupply(List<Part> parts) {
        int requiredWatts = (int) Math.ceil(powerDrawWatts(parts) * PSU_HEADROOM);
        int psuWatts = onlyPartOf(PartType.PSU, parts).watts();
        if (psuWatts >= requiredWatts) {
            return List.of();
        }
        return List.of("PSU gives " + psuWatts + "W but the build needs at least " + requiredWatts + "W");
    }

    private static long countOf(PartType type, List<Part> parts) {
        return parts.stream().filter(part -> part.type() == type).count();
    }

    private static Part onlyPartOf(PartType type, List<Part> parts) {
        return parts.stream().filter(part -> part.type() == type).findFirst().orElseThrow();
    }
}
