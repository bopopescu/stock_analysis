package preti.stock.web;

public enum ApiError {
    TRADE_INSUFICIENT_BALANCE("API.TRADE.INSUFICIENT_BALANCE"), TRADE_ALREADY_OPEN("API.TRADE.ALREADY_OPEN"), TRADE_NOT_FOUND("API.TRADE.NOT_FOUND"), TRADE_ALREADY_CLOSED("API.TRADE.ALREADY_CLOSED");
    
    public String code;
    private ApiError(String c) {
        this.code = c;
    }
    

}
