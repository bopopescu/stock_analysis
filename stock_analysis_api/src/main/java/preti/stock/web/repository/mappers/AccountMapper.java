package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.analysismodel.donchian.Account;

public class AccountMapper implements RowMapper<Account> {

	@Override
	public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
		Account account = new Account();
		account.setId(rs.getLong("account_id"));
		account.setBalance(rs.getDouble("balance"));
		account.setInitialPosition(rs.getDouble("initial_position"));
		return account;
	}

}
