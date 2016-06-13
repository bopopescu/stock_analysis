package preti.stock.api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import preti.stock.api.model.db.WalletDBEntity;
import preti.stock.api.repository.mappers.WalletRowMapper;

@Repository
public class WalletRepository {

    private JdbcTemplate jdbcTemplate;
    private final String WALLET_SELECT = "select w.wallet_id, w.stock_id, w.account_id, w.size, w.dat_creation, w.dat_update from wallet w ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public WalletDBEntity getWallet(long walletId) {
        StringBuilder sql = new StringBuilder();
        sql.append(WALLET_SELECT);
        sql.append("where w.wallet_id=?");
        List<WalletDBEntity> result = jdbcTemplate.query(sql.toString(), new Object[] { walletId },
                new WalletRowMapper());

        if (result.isEmpty())
            return null;

        return result.get(0);
    }

    public WalletDBEntity getWalletForAccountAndStock(long accountId, long stockId) {
        StringBuilder sql = new StringBuilder();
        sql.append(WALLET_SELECT);
        sql.append("where w.account_id=? and w.stock_id=?");
        List<WalletDBEntity> result = jdbcTemplate.query(sql.toString(), new Object[] { accountId, stockId },
                new WalletRowMapper());

        if (result.isEmpty())
            return null;

        return result.get(0);
    }

    public List<WalletDBEntity> getWalletForAccount(long accountId) {
        StringBuilder sql = new StringBuilder();
        sql.append(WALLET_SELECT);
        sql.append("where w.account_id=? ");
        return jdbcTemplate.query(sql.toString(), new Object[] { accountId }, new WalletRowMapper());
    }

    public long createWallet(WalletDBEntity w) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "insert into wallet (stock_id, account_id, size, dat_creation, dat_update) values (?, ?, ?, ?, ?) ",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, w.getStockId());
                ps.setLong(2, w.getAccountId());
                ps.setDouble(3, w.getSize());
                ps.setDate(4, new java.sql.Date(w.getCreationDate().getTime()));
                ps.setDate(5, new java.sql.Date(w.getUpdateDate().getTime()));

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void updateWalletSize(long walletId, double size) {
        jdbcTemplate.update("update wallet set size=? where wallet_id=?", size, walletId);
    }
}
