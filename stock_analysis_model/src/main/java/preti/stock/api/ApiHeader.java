package preti.stock.api;

public enum ApiHeader {
    ERROR_VALIDATION_CODE("Error-Validation-Code");

    public String headerName;

    private ApiHeader(String name) {
        this.headerName = name;
    }
}
