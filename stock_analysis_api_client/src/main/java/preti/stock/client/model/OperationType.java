package preti.stock.client.model;

public enum OperationType {
    BUY("B"), SELL("S");

    private String code;

    private OperationType(String s) {
        this.code = s;
    }

    public static OperationType getFromValue(String value) {
        if (BUY.code.equals(value))
            return BUY;
        else if (SELL.code.equals(value))
            return OperationType.SELL;

        throw new IllegalArgumentException("Invalid order type: " + value);
    }

    public String getCode() {
        return code;
    }

}
