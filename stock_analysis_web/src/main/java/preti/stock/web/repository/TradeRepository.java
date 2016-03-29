package preti.stock.web.repository;

import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import preti.stock.coremodel.Trade;
import preti.stock.web.repository.mappers.TradeRowMapper;

@Repository
public class TradeRepository {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<Trade> getTrades(long accountId) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				"select t.trade_id, t.account_id, t.stock_id, t.model_id, s.stock_code, s.stock_name, t.buy_date, t.sell_date, t.buy_value, t.sell_value, t.size, t.stop_pos ");
		sql.append("from trade t inner join stock s on t.stock_id=s.stock_id ");
		sql.append("where t.account_id=? and t.sell_date is null");
		return jdbcTemplate.query(sql.toString(), new Object[] { accountId }, new TradeRowMapper());
	}

	public Date getOldestOpenTradeBuyDate(long accountId) {
		return jdbcTemplate.queryForObject("select min(buy_date) from trade where sell_date is null and account_id=?",
				new Object[] { accountId }, Date.class);
	}

	public void createTrade(Trade t, long accountId) {
		jdbcTemplate.update(
				"insert into trade (account_id, stock_id, model_id, buy_date, buy_value, size, stop_pos) values (?, ?, ?, ?, ?, ?, ?) ",
				accountId, t.getStock().getId(), t.getModelId(), t.getBuyDate(), t.getBuyValue(), t.getSize(), t.getStopPos());
	}

	public void updateTrade(long tradeId, Date sellDate, double sellValue) {
		jdbcTemplate.update("update trade set sell_date=?, sell_value=? where trade_id=?", sellDate, sellValue,
				tradeId);
	}
}
