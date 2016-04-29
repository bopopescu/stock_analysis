package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.BuyOrder;

public class BuyOrderMapper implements RowMapper<BuyOrder> {

    @Override
    public BuyOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BuyOrder(rs.getLong("order_id"), rs.getLong("account_id"), rs.getLong("stock_id"),
                rs.getLong("model_id"), rs.getDouble("size"), rs.getDate("creation_date"), rs.getDouble("value"),
                rs.getDouble("stop_pos"));
    }

}
