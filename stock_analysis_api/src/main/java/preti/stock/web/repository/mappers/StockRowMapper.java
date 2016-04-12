package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.coremodel.Stock;

public class StockRowMapper implements RowMapper<Stock> {

	@Override
	public Stock mapRow(ResultSet rs, int rowNum) throws SQLException {
		Stock stock = new Stock();
		stock.setId(rs.getLong("stock_id"));
		stock.setCode(rs.getString("stock_code"));
		stock.setName(rs.getString("stock_name"));
		return stock;
	}

}
