package preti.stock.api.model.trading;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import preti.stock.api.model.db.StockDBEntity;
import preti.stock.api.model.db.StockHistoryDBEntity;
import preti.stock.system.Stock;

public class StockDBImpl implements Stock<StockDBEntity> {

    private StockDBEntity target;
    private TreeMap<Date, StockHistoryDBEntity> history = new TreeMap<>();

    public StockDBImpl(StockDBEntity stock) {
        this.target = stock;
    }

    public StockDBImpl(long id, String code, String name) {
        this.target = new StockDBEntity(id, code, name);

    }

    public Map<Date, StockHistoryDBEntity> getHistory() {
        return history;
    }

    public void addHistory(StockHistoryDBEntity h) {
        history.put(h.getDate(), h);
    }

    public void setHistory(Collection<StockHistoryDBEntity> sh) {
        this.history = new TreeMap<>();
        sh.forEach(s -> this.addHistory(s));
    }

    public StockDBEntity getTarget() {
        return this.target;
    }

    @Override
    public String getCode() {
        return target.getCode();
    }

    @Override
    public boolean hasHistoryAtDate(Date date) {
        return history.containsKey(date);
    }

    @Override
    public Set<Date> getAllHistoryDates() {
        return history.keySet();
    }

    public StockHistoryDBEntity getHistory(Date d) {
        return history.get(d);
    }

    @Override
    public int getHistorySizeBeforeDate(Date date) {
        return history.headMap(date).size();
    }

    @Override
    public double getCloseValueAtDate(Date date) {
        return history.floorEntry(date).getValue().getClose();
    }

    @Override
    public double getVolumeAtDate(Date date) {
        return history.floorEntry(date).getValue().getVolume();
    }

}
