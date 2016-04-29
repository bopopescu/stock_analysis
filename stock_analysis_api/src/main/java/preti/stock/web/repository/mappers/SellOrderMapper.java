package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.SellOrder;

public class SellOrderMapper implements RowMapper<SellOrder> {


    @Override
    public SellOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SellOrder(rs.getLong("order_id"), rs.getLong("account_id"), rs.getLong("stock_id"),
                rs.getLong("model_id"), rs.getDouble("size"), rs.getDate("creation_date"), rs.getDouble("value"));
    }

}
