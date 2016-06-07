package preti.stock.web.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import preti.stock.coremodel.Trade;
import preti.stock.web.repository.mappers.TradeRowMapper;

@Repository
public class TradeRepository {
    private JdbcTemplate jdbcTemplate;
    private final String TRADE_SELECT = "select t.trade_id, t.account_id, t.stock_id, t.buy_date, t.sell_date, t.buy_value, t.sell_value, t.size, t.stop_pos from trade t ";

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

    public long createTrade(Trade t) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "insert into trade (account_id, stock_id, buy_order_id, buy_date, buy_value, size, stop_pos) values (?, ?, ?, ?, ?, ?, ?) ",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, t.getAccountId());
                ps.setLong(2, t.getStockId());
                ps.setLong(3, t.getBuyOrderId());
                ps.setDate(4, new java.sql.Date(t.getBuyDate().getTime()));
                ps.setDouble(5, t.getBuyValue());
                ps.setDouble(6, t.getSize());
                ps.setDouble(7, t.getStopPos());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void closeTrade(long tradeId, Date sellDate, double sellValue, long sellOrderId) {
        jdbcTemplate.update("update trade set sell_date=?, sell_value=?, sell_order_id=? where trade_id=?", sellDate,
                sellValue, sellOrderId, tradeId);
    }

    public Trade getTrade(long tradeId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append("where t.trade_id=?");
        List<Trade> result = jdbcTemplate.query(sql.toString(), new Object[] { tradeId }, new TradeRowMapper());

        if (result.isEmpty())
            return null;

        return result.get(0);
    }

    public List<Trade> getOpenTrades(long accountId, long stockId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append("where t.account_id=? and t.stock_id=? and t.sell_date is null");
        return jdbcTemplate.query(sql.toString(), new Object[] { accountId, stockId }, new TradeRowMapper());
    }

    public Trade getOpenTradeForSellOrder(long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append(TRADE_SELECT);
        sql.append(" inner join op_order bo on t.buy_order_id=bo.order_id ");
        sql.append(" inner join model bm on bo.model_id=bm.model_id ");
        sql.append(" inner join op_order so ");
        sql.append(" inner join model sm on sm.model_id=so.model_id and sm.account_id=bm.account_id ");
        sql.append("");
        sql.append(" where so.order_id=? and t.sell_order_id is null ");
        
        
//        sql.append("join op_order o on t.stock_id=o.stock_id and t.account_id=o.account_id ");
//        sql.append("where t.sell_date is null and o.order_id=?");

        List<Trade> result = jdbcTemplate.query(sql.toString(), new Object[] { orderId }, new TradeRowMapper());

        if (result.isEmpty())
            return null;

        return result.get(0);
    }
}
