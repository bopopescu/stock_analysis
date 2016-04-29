package preti.stock.web.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import preti.stock.coremodel.BuyOrder;
import preti.stock.coremodel.SellOrder;
import preti.stock.web.repository.mappers.BuyOrderMapper;
import preti.stock.web.repository.mappers.SellOrderMapper;

@Repository
public class OrderRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public BuyOrder getBuyOrder(long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(
                "    o.order_id, o.account_id, o.type, o.stock_id, o.model_id, o.size, o.creation_date, bo.value, bo.stop_pos ");
        sql.append("from ");
        sql.append("     op_order o ");
        sql.append("    inner join buy_order bo on o.order_id=bo.order_id ");
        sql.append("where ");
        sql.append("    o.order_id=? ");

        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { orderId }, new BuyOrderMapper());
    }

    public SellOrder getSellOrder(long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append("    o.order_id, o.account_id, o.type, o.stock_id, o.model_id, o.size, o.creation_date, so.value ");
        sql.append("from ");
        sql.append("     op_order o ");
        sql.append("    inner join sell_order so on o.order_id=so.order_id ");
        sql.append("where ");
        sql.append("    o.order_id=? ");

        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { orderId }, new SellOrderMapper());
    }
    
    public long createBuyOrder(BuyOrder order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("insert into op_order (account_id, type, stock_id, model_id, size, creation_date) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getAccountId());
                ps.setString(2, order.getType().type);
                ps.setLong(3, order.getStockId());
                ps.setLong(4, order.getModelId());
                ps.setDouble(5, order.getSize());
                ps.setDate(6, new java.sql.Date(order.getDate().getTime()));
                
                return ps;
            }
        }, keyHolder);
        
        long orderId = keyHolder.getKey().longValue();
        jdbcTemplate.update("insert into buy_order (order_id, value, stop_pos) values (?, ?, ?) ", orderId, order.getValue(), order.getStopPos());
        return orderId;
    }
    
    public long createSellOrder(SellOrder order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(new PreparedStatementCreator() {
            
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement("insert into op_order (account_id, type, stock_id, model_id, size, creation_date) values (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getAccountId());
                ps.setString(2, order.getType().type);
                ps.setLong(3, order.getStockId());
                ps.setLong(4, order.getModelId());
                ps.setDouble(5, order.getSize());
                ps.setDate(6, new java.sql.Date(order.getDate().getTime()));
                
                return ps;
            }
        }, keyHolder);
        
        long orderId = keyHolder.getKey().longValue();
        jdbcTemplate.update("insert into sell_order (order_id, value) values (?, ?) ", orderId, order.getValue());
        return orderId;
    }

}
