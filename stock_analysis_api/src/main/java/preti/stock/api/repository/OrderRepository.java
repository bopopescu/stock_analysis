package preti.stock.api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import preti.stock.api.model.db.OrderDBEntity;
import preti.stock.api.repository.mappers.OrderMapper;

@Repository
public class OrderRepository {

    private JdbcTemplate jdbcTemplate;
    private final String ORDER_SELECT = "select o.order_id, o.type, o.stock_id, o.model_id, o.size, o.creation_date, o.value, o.stop_pos from op_order o ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public OrderDBEntity getOrder(long orderId) {
        StringBuilder sql = new StringBuilder();
        sql.append(ORDER_SELECT);
        sql.append("where ");
        sql.append("    o.order_id=? ");

        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { orderId }, new OrderMapper());
    }

    public long createOrder(OrderDBEntity order) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "insert into op_order (type, stock_id, model_id, size, creation_date, value, stop_pos) values (?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, order.getType().type);
                ps.setLong(2, order.getStockId());
                ps.setLong(3, order.getModelId());
                ps.setDouble(4, order.getSize());
                ps.setDate(5, new java.sql.Date(order.getCreationDate().getTime()));
                ps.setDouble(6, order.getValue());
                ps.setDouble(7, order.getStopPos());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public List<OrderDBEntity> getAllOpenOrders(long accountId) {
        StringBuilder sql = new StringBuilder();
        sql.append(ORDER_SELECT);
        sql.append(" inner join model m on m.model_id=o.model_id ");
        sql.append(" where ");
        sql.append("    m.account_id=? ");
        sql.append("    and not exists (select 1 from operation op where op.order_id=o.order_id) ");

        return jdbcTemplate.query(sql.toString(), new Object[] { accountId }, new OrderMapper());
    }

}
