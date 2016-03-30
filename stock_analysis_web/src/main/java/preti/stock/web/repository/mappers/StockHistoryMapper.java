package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.StockHistory;

public class StockHistoryMapper implements RowMapper<StockHistory> {

	@Override
	public StockHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new StockHistory(rs.getLong("stock_history_id"), rs.getLong("stock_id"), rs.getDate("date"), rs.getDouble("high"), rs.getDouble("low"),
				rs.getDouble("close"), rs.getDouble("volume"));
	}

}
