package preti.stock.api.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.api.model.db.StockDBEntity;

public class StockRowMapper implements RowMapper<StockDBEntity> {

    @Override
    public StockDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        StockDBEntity stock = new StockDBEntity();
        stock.setId(rs.getLong("stock_id"));
        stock.setCode(rs.getString("stock_code"));
        stock.setName(rs.getString("stock_name"));
        return stock;
    }

}
