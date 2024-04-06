package domain;

public class MaximumTransferableAmount {
    private static final double MAX_EURO_TRANSFER = 10000.0; // Example limit
    private static final double MAX_RON_TRANSFER = 50000.0; // Example limit

    private MaximumTransferableAmount() {}

    public static double getMaxEuroTransfer() {
        return MAX_EURO_TRANSFER;
    }

    public static double getMaxRonTransfer() {
        return MAX_RON_TRANSFER;
    }
}


