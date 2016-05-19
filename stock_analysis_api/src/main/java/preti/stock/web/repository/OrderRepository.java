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

import preti.stock.coremodel.Order;
import preti.stock.web.repository.mappers.OrderMapper;

@Repository
public class OrderRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Order getOrder(long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(
                "    o.order_id, o.account_id, o.type, o.stock_id, o.model_id, o.size, o.creation_date, o.value, o.stop_pos ");
        sql.append("from ");
        sql.append("     op_order o ");
        sql.append("where ");
        sql.append("    o.order_id=? ");

        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { orderId }, new OrderMapper());
    }

    public long createOrder(Order order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "insert into op_order (account_id, type, stock_id, model_id, size, creation_date, value, stop_pos) values (?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, order.getAccountId());
                ps.setString(2, order.getType().type);
                ps.setLong(3, order.getStockId());
                ps.setLong(4, order.getModelId());
                ps.setDouble(5, order.getSize());
                ps.setDate(6, new java.sql.Date(order.getCreationDate().getTime()));
                ps.setDouble(7, order.getValue());
                ps.setDouble(8, order.getStopPos());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

}
