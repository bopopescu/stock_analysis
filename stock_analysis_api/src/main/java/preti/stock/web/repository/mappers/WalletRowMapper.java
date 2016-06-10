package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.db.model.WalletDBEntity;

public class WalletRowMapper implements RowMapper<WalletDBEntity> {

    @Override
    public WalletDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new WalletDBEntity(rs.getLong("wallet_id"), rs.getLong("stock_id"), rs.getLong("account_id"),
                rs.getDouble("size"), rs.getDate("dat_creation"), rs.getDate("dat_update"));
    }

}
