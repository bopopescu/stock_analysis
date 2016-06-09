package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.db.model.AccountDBEntity;

public class AccountMapper implements RowMapper<AccountDBEntity> {

    @Override
    public AccountDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AccountDBEntity account = new AccountDBEntity();
        account.setId(rs.getLong("account_id"));
        account.setBalance(rs.getDouble("balance"));
        account.setInitialPosition(rs.getDouble("initial_position"));
        return account;
    }

}
