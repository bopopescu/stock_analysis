package preti.stock.api.model.trading;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.HighestValueIndicator;
import eu.verdelhan.ta4j.indicators.helpers.LowestValueIndicator;
import eu.verdelhan.ta4j.indicators.simple.MaxPriceIndicator;
import eu.verdelhan.ta4j.indicators.simple.MinPriceIndicator;
import preti.stock.api.model.db.StockDBEntity;
import preti.stock.api.model.db.StockHistoryDBEntity;
import preti.stock.api.model.db.wrapper.TradeWrapper;
import preti.stock.system.Trade;
import preti.stock.system.TradingStrategy;

public class DonchianStrategyDBImpl implements TradingStrategy<StockDBEntity, TradeWrapper> {
    private static final Log log = LogFactory.getLog(DonchianStrategyDBImpl.class);

    private long id;
    private StockDBImpl stock;
    private final double riskRate;
    private int entryDonchianSize, exitDonchianSize;

    private LowestValueIndicator lowestValueIndicator;
    private HighestValueIndicator highestValueIndicator;

    public DonchianStrategyDBImpl(StockDBImpl stock, long id, int entryDonchianSize, int exitDonchianSize,
            double riskRate) {
        this.stock = stock;
        this.id = id;
        this.entryDonchianSize = entryDonchianSize;
        this.exitDonchianSize = exitDonchianSize;

        this.lowestValueIndicator = createLowestValueIndicator();
        this.highestValueIndicator = createHighestValueIndicator();

        this.riskRate = riskRate;
    }

    private TimeSeries createTimeSeries() {
        TimeSeries stockHistory = new TimeSeries(stock.getCode(), new ArrayList<>());
        for (Date d : stock.getAllHistoryDates()) {
            StockHistoryDBEntity h = stock.getHistory(d);
            stockHistory.addTick(
                    new Tick(new DateTime(d.getTime()), 0, h.getHigh(), h.getLow(), h.getClose(), h.getVolume()));
        }
        return stockHistory;
    }

    private LowestValueIndicator createLowestValueIndicator() {
        MinPriceIndicator minPrice = new MinPriceIndicator(createTimeSeries());
        return new LowestValueIndicator(minPrice, exitDonchianSize);
    }

    private HighestValueIndicator createHighestValueIndicator() {
        MaxPriceIndicator maxPrice = new MaxPriceIndicator(createTimeSeries());
        return new HighestValueIndicator(maxPrice, entryDonchianSize);
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public StockDBImpl getStock() {
        return stock;
    }

    @Override
    public boolean hasReachedMaxGain(Trade<TradeWrapper> trade, Date date) {
        int dataSize = stock.getHistorySizeBeforeDate(date);
        if (dataSize <= exitDonchianSize)
            return false;

        return stock.getCloseValueAtDate(date) <= lowestValueIndicator.getValue(dataSize - 1).toDouble();
    }

    @Override
    public boolean hasReachedStopLoss(Trade<TradeWrapper> trade, Date date) {
        if (!trade.isOpen())
            throw new IllegalArgumentException("Trade is not opened.");

        return stock.getCloseValueAtDate(date) <= trade.getStopLossPosition();
    }

    @Override
    public boolean shouldBuyStock(Date date) {
        if (entryDonchianSize == 0) {
            log.info("Skiping at date " + date);
            return false;
        }

        int dataSize = stock.getHistorySizeBeforeDate(date);
        if (dataSize <= entryDonchianSize) {
            log.info("Skiping at date " + date);
            return false;
        }

        return stock.getVolumeAtDate(date) >= Math.pow(10, 6)
                && stock.getCloseValueAtDate(date) > highestValueIndicator.getValue(dataSize - 1).toDouble();
    }

    @Override
    public double calculatePositionSize(Date date, double accountInitialBalance, double accountCurrentBalance,
            double openPositionsValue) {
        double stopLossPoint = calculateStopLossPoint(date);
        double stockValue = stock.getCloseValueAtDate(date);

        double size = Math
                .floor(((accountCurrentBalance + openPositionsValue) * riskRate) / (stockValue - stopLossPoint));
        while ((size * stockValue) > accountCurrentBalance && size > 1) {
            size--;
        }

        return size;
    }

    public double calculateStopLossPoint(Date d) {
        int dataSize = stock.getHistorySizeBeforeDate(d);
        return lowestValueIndicator.getValue(dataSize - 1).toDouble();
    }

}
