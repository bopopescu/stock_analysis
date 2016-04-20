package preti.stock.web;

public enum ApiError {
    TRADE_INSUFICIENT_BALANCE("API.TRADE.INSUFICIENT_BALANCE");
    
    public String code;
    private ApiError(String c) {
        this.code = c;
    }
    

}
