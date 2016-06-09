package preti.stock.db.model;

public enum OrderDBEntityType {
    BUY("B"), SELL("S");

    public String type;

    private OrderDBEntityType(String s) {
        this.type = s;
    }
    
    public static OrderDBEntityType getFromValue(String value){
        if(BUY.type.equals(value))
            return BUY;
        else if(SELL.type.equals(value))
            return OrderDBEntityType.SELL;
        
        throw new IllegalArgumentException("Invalid order type: " + value);
    }
}
