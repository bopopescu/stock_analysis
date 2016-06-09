package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.OrderDBEntityType;

public class OrderMapper implements RowMapper<OrderDBEntity> {

    @Override
    public OrderDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new OrderDBEntity(rs.getLong("order_id"), OrderDBEntityType.getFromValue(rs.getString("type")),
                rs.getLong("stock_id"), rs.getLong("model_id"), rs.getDouble("size"), rs.getDate("creation_date"),
                rs.getDouble("value"), rs.getDouble("stop_pos"));
    }

}
