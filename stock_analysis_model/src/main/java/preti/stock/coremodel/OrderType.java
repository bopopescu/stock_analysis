package preti.stock.coremodel;

public enum OrderType {
    BUY("B"), SELL("S");

    public String type;

    private OrderType(String s) {
        this.type = s;
    }
    
    public static OrderType getFromValue(String value){
        if(BUY.type.equals(value))
            return BUY;
        else if(SELL.type.equals(value))
            return OrderType.SELL;
        
        throw new IllegalArgumentException("Invalid order type: " + value);
    }
}
