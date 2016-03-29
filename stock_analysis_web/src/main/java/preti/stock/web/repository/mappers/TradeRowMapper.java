package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;

public class TradeRowMapper implements RowMapper<Trade> {

	@Override
	public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
		Set<String> columnNames = getColumnNames(rs);

		Trade trade = new Trade();
		trade.setId(rs.getLong("trade_id"));
		trade.setModelId(rs.getLong("model_id"));
		trade.setBuyDate(rs.getDate("buy_date"));
		trade.setSellDate(rs.getDate("sell_date"));
		trade.setBuyValue(rs.getDouble("buy_value"));
		trade.setSellValue(rs.getDouble("sell_value"));
		trade.setSize(rs.getDouble("size"));
		trade.setStopPos(rs.getDouble("stop_pos"));

		Stock s = new Stock(rs.getString("stock_code"));
		if (columnNames.contains("stock_name"))
			s.setName(rs.getString("stock_name"));

		trade.setStock(s);
		return trade;
	}

	private Set<String> getColumnNames(ResultSet rs) throws SQLException {
		Set<String> names = new HashSet<>();

		ResultSetMetaData rsmd = rs.getMetaData();
		for (int x = 1; x <= rsmd.getColumnCount(); x++) {
			names.add(rsmd.getColumnName(x));
		}

		return names;
	}

}
