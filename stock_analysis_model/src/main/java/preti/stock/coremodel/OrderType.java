package preti.stock.coremodel;

public enum OrderType {
    BUY("B"), SELL("S");

    public String type;

    private OrderType(String s) {
        this.type = s;
    }
}
