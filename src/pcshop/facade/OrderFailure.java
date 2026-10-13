package pcshop.facade;

// Why an order could not be completed - in the customer's terms, not in the
// subsystem's terms.
public enum OrderFailure {
    UNSUPPORTED_CITY,
    INCOMPATIBLE_PARTS,
    OUT_OF_STOCK,
    QUALITY_CHECK_FAILED,
    INTERNAL_ERROR
}
