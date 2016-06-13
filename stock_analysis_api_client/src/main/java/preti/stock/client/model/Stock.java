package preti.stock.client.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public @SuppressWarnings("serial") class Stock implements Serializable {

    private long id;
    private String code;
    private String name;

    private TreeMap<Date, StockHistory> history = new TreeMap<>();

    public Stock() {

    }

    public Stock(long id, String code, String name) {
        super();
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addHistory(StockHistory h) {
        history.put(h.getDate(), h);
    }

    @JsonIgnore
    public void setHistory(Collection<StockHistory> sh) {
        this.history = new TreeMap<>();
        sh.forEach(s -> this.addHistory(s));
    }
    
    @JsonIgnore
    public double getCloseValueAtDate(Date d) {
        return history.floorEntry(d).getValue().getClose();
    }

}
