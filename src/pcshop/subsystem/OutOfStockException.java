package pcshop.subsystem;

// Thrown by Inventory when a part can't be reserved.
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String partModel) {
        super(partModel + " is out of stock");
    }
}
