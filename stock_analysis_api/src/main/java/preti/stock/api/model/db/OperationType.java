package preti.stock.api.model.db;

public enum OperationType {
    BUY("B"), SELL("S");

    public String type;

    private OperationType(String s) {
        this.type = s;
    }
    
    public static OperationType getFromValue(String value){
        if(BUY.type.equals(value))
            return BUY;
        else if(SELL.type.equals(value))
            return OperationType.SELL;
        
        throw new IllegalArgumentException("Invalid order type: " + value);
    }
}
