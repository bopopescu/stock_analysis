package preti.stock.coremodel;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock implements Serializable {
	private long id;
	private String code;
	private String name;
	private TreeMap<Date, StockHistory> history = new TreeMap<>();

	public Stock() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Stock(String code) {
		super();
		this.code = code;
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

	public Map<Date, StockHistory> getHistory() {
		return history;
	}

	@JsonIgnore
	public void setHistory(Collection<StockHistory> sh) {
		this.history = new TreeMap<>();
		sh.forEach(s -> this.addHistory(s));
	}

	@JsonIgnore
	public Set<Date> getAllHistoryDates() {
		return history.keySet();
	}

	public void addHistory(StockHistory h) {
		history.put(h.getDate(), h);
	}

	public StockHistory getHistory(Date d) {
		return history.get(d);
	}

	public double getCloseValueAtDate(Date d) {
		return history.floorEntry(d).getValue().getClose();
	}

	public double getVolumeAtDate(Date d) {
		return history.floorEntry(d).getValue().getVolume();
	}

	public boolean hasHistoryAtDate(Date d) {
		return history.containsKey(d);
	}

	public int getHistorySizeBeforeDate(Date d) {
		return history.headMap(d).size();
	}

}
