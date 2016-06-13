package preti.stock.api.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.api.model.db.StockHistoryDBEntity;

public class StockHistoryMapper implements RowMapper<StockHistoryDBEntity> {

    @Override
    public StockHistoryDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new StockHistoryDBEntity(rs.getLong("stock_history_id"), rs.getLong("stock_id"), rs.getDate("date"),
                rs.getDouble("high"), rs.getDouble("low"), rs.getDouble("close"), rs.getDouble("volume"));
    }

}
