package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.Order;
import preti.stock.coremodel.OrderType;

public class OrderMapper implements RowMapper<Order> {

    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        return new Order(rs.getLong("order_id"), OrderType.getFromValue(rs.getString("type")), rs.getLong("account_id"), rs.getLong("stock_id"),
                rs.getLong("model_id"), rs.getDouble("size"), rs.getDate("creation_date"), rs.getDouble("value"),
                rs.getDouble("stop_pos"));
    }

}
