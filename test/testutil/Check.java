package testutil;

// Didn't want to set up Maven just for JUnit, so this is a tiny homemade
// assert helper instead. Nothing fancy, just enough to run a few checks
// and print PASS/FAIL.
public final class Check {

    private static int passed = 0;
    private static int failed = 0;

    private Check() {
    }

    public static void that(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }

    public static void equal(String description, Object expected, Object actual) {
        that(description + " (expected \"" + expected + "\", got \"" + actual + "\")",
                expected == null ? actual == null : expected.equals(actual));
    }

    public static void throwsException(String description, Runnable action, Class<? extends Throwable> expectedType) {
        try {
            action.run();
            that(description + " (expected " + expectedType.getSimpleName() + ", nothing was thrown)", false);
        } catch (Throwable t) {
            that(description + " (threw " + t.getClass().getSimpleName() + ")", expectedType.isInstance(t));
        }
    }

    public static void printSummary(String suiteName) {
        System.out.println();
        System.out.println(suiteName + ": " + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }
}
