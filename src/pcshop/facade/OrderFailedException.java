package pcshop.facade;

// The only exception the client sees from PcShopFacade. Subsystem exceptions
// (OutOfStockException, IllegalStateException, ...) are translated into this,
// so they never leak through the Facade's interface.
public class OrderFailedException extends RuntimeException {

    private final OrderFailure reason;

    public OrderFailedException(OrderFailure reason, String message) {
        this(reason, message, null);
    }

    public OrderFailedException(OrderFailure reason, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public OrderFailure reason() {
        return reason;
    }
}
