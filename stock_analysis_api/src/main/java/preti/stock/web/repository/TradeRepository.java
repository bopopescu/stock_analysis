package preti.stock.web.repository;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import preti.stock.coremodel.Trade;
import preti.stock.web.repository.mappers.TradeRowMapper;

@Repository
public class TradeRepository {
    private JdbcTemplate jdbcTemplate;
    private final String TRADE_SELECT ="select t.trade_id, t.account_id, t.stock_id, t.model_id, t.buy_date, t.sell_date, t.buy_value, t.sell_value, t.size, t.stop_pos from trade t ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Trade> getOpenTradesForAccount(long accountId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append("where t.account_id=? and t.sell_date is null");
        return jdbcTemplate.query(sql.toString(), new Object[] { accountId }, new TradeRowMapper());
    }

    public Date getOldestOpenTradeBuyDateForAccount(long accountId) {
        return jdbcTemplate.queryForObject("select min(buy_date) from trade where sell_date is null and account_id=?",
                new Object[] { accountId }, Date.class);
    }

    public void createTrade(Trade t, long accountId) {
        jdbcTemplate.update(
                "insert into trade (account_id, stock_id, model_id, buy_date, buy_value, size, stop_pos) values (?, ?, ?, ?, ?, ?, ?) ",
                accountId, t.getStockId(), t.getModelId(), t.getBuyDate(), t.getBuyValue(), t.getSize(),
                t.getStopPos());
    }

    public void closeTrade(long tradeId, Date sellDate, double sellValue) {
        jdbcTemplate.update("update trade set sell_date=?, sell_value=? where trade_id=?", sellDate, sellValue,
                tradeId);
    }

    public Trade getTrade(long tradeId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append("where t.trade_id=?");
        List<Trade> result = jdbcTemplate.query(sql.toString(), new Object[] { tradeId }, new TradeRowMapper());
        
        if(result.isEmpty())
            return null;
        
        return result.get(0);
    }
    
    public List<Trade> getOpenTrades(long accountId, long stockId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append("where t.account_id=? and t.stock_id=? and t.sell_date is null");
        return jdbcTemplate.query(sql.toString(), new Object[] { accountId, stockId }, new TradeRowMapper());
    }
}
