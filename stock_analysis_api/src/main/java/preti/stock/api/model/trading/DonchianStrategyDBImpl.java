package preti.stock.api.model.trading;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.client.RestTemplate;

import preti.stock.api.model.db.StockDBEntity;
import preti.stock.api.model.db.wrapper.TradeWrapper;
import preti.stock.system.Trade;
import preti.stock.system.TradingStrategy;

public class DonchianStrategyDBImpl implements TradingStrategy<StockDBEntity, TradeWrapper> {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(DonchianStrategyDBImpl.class);
    private final String analysisServiceLocation = "http://stock_analysis_service:8082";

    private long id;
    private StockDBImpl stock;

    private RestTemplate restTemplate;
    DateFormat dateFormat;

    public DonchianStrategyDBImpl(StockDBImpl stock, long id) {
        this.stock = stock;
        this.id = id;

        restTemplate = new RestTemplate();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stock", stock.getCode());
        parameters.put("account", 1);
        parameters.put("date", dateFormat.format(date));

        StringBuilder url = new StringBuilder();
        url.append(analysisServiceLocation);
        url.append("/shouldSellStock");
        url.append("?stock={stock}&date={date}&account={account}");
        return restTemplate.getForObject(url.toString(), Boolean.class, parameters);
    }

    @Override
    public boolean hasReachedStopLoss(Trade<TradeWrapper> trade, Date date) {
        if (!trade.isOpen())
            throw new IllegalArgumentException("Trade is not opened.");

        return stock.getCloseValueAtDate(date) <= trade.getStopLossPosition();
    }

    @Override
    public boolean shouldBuyStock(Date date) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stock", stock.getCode());
        parameters.put("account", 1);
        parameters.put("date", dateFormat.format(date));

        StringBuilder url = new StringBuilder();
        url.append(analysisServiceLocation);
        url.append("/shouldBuyStock");
        url.append("?stock={stock}&date={date}&account={account}");
        return restTemplate.getForObject(url.toString(), Boolean.class, parameters);
    }

    @Override
    public double calculatePositionSize(Date date, double accountInitialBalance, double accountCurrentBalance,
            double openPositionsValue) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stock", stock.getCode());
        parameters.put("account", 1);
        parameters.put("date", dateFormat.format(date));
        parameters.put("balance", accountCurrentBalance);
        parameters.put("openPosValue", openPositionsValue);

        StringBuilder url = new StringBuilder();
        url.append(analysisServiceLocation);
        url.append("/calculatePositionSize");
        url.append("?stock={stock}&date={date}&account={account}&balance={balance}&openPosValue={openPosValue}");
        return restTemplate.getForObject(url.toString(), Double.class, parameters);
    }

    public double calculateStopLossPoint(Date d) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("stock", stock.getCode());
        parameters.put("account", 1);
        parameters.put("date", dateFormat.format(d));

        StringBuilder url = new StringBuilder();
        url.append(analysisServiceLocation);
        url.append("/calculateStopLossPoint");
        url.append("?stock={stock}&date={date}&account={account}");
        return restTemplate.getForObject(url.toString(), Double.class, parameters);
    }

}
