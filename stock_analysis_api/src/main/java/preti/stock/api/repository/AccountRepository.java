package preti.stock.api.repository;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import preti.stock.api.model.db.AccountDBEntity;
import preti.stock.api.repository.mappers.AccountMapper;

@Repository
public class AccountRepository {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

    public AccountDBEntity getAccount(long accountId) {
		return jdbcTemplate.queryForObject(
				"select account_id, balance, initial_position from account where account_id=?",
				new Object[] { accountId }, new AccountMapper());
	}

	public List<String> getStocksToAnalyse(long accountId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select s.stock_code ");
		sql.append("from stock s inner join account_analyse_stock aas on s.stock_id=aas.stock_id ");
		sql.append("where aas.account_id=? order by s.stock_code");
		return jdbcTemplate.queryForList(sql.toString(), String.class, new Object[] { accountId });
	}

	public void updateBalance(long accountId, double balanceChange) {
		jdbcTemplate.update("update account set balance = balance + ? where account_id = ?", balanceChange, accountId);
	}

}
