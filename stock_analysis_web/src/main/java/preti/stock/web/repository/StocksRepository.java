package preti.stock.web.repository;

import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StocksRepository {
	private NamedParameterJdbcTemplate namedParameterjdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedParameterjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Date getLastStocksDate() {
		return jdbcTemplate.queryForObject("select max(date) from stock_history", Date.class);
	}

	public boolean existsStock(String stockCode) {
		return jdbcTemplate.queryForObject("select count(*) from stock where stock_code = ?",
				new Object[] { stockCode }, Integer.class) > 0;
	}

	public void createStock(String stockCode, String stockName) {
		jdbcTemplate.update("insert into stock (stock_code, stock_name) values (?, ?)", stockCode, stockName);
	}

	public Integer getStockId(String stockCode) {
		return jdbcTemplate.queryForObject("select stock_id from stock where stock_code=?", new Object[] { stockCode },
				Integer.class);
	}

	public void createHistory(Integer stockId, Date date, Double close, Double open, Double volume) {
		jdbcTemplate.update("insert into stock_history (stock_id, date, close, open, volume) values (?, ?, ?, ?, ?)",
				new Object[] { stockId, date, close, open, volume });
	}
}
