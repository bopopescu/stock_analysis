package preti.stock.web.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.mysql.jdbc.Statement;

import preti.stock.db.model.OperationDBEntity;
import preti.stock.db.model.OperationType;
import preti.stock.web.repository.mappers.OperationRowMapper;

@Repository
public class OperationRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterjdbcTemplate;

    private final String OPERATION_SELECT = "select op.operation_id, op.order_id, op.dat_creation, op.size, op.value, op.stop_loss from operation op ";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public OperationDBEntity getOperation(long operationId) {
        StringBuilder sql = new StringBuilder();
        sql.append(OPERATION_SELECT);
        sql.append("where op.operation_id=?");
        return jdbcTemplate.queryForObject(sql.toString(), new Object[] { operationId }, new OperationRowMapper());
    }

    public List<OperationDBEntity> getOpenOperations(long accountId, long stockId) {
        StringBuilder sql = new StringBuilder();
        sql.append(OPERATION_SELECT);
        sql.append(" inner join op_order ord on ord.order_id=op.order_id ");
        sql.append(" inner join model m on m.model_id=ord.model_id ");
        sql.append(" where m.account_id=:accountId  and ord.type=:buyType and ord.stock_id=:stockId and not exists ");
        sql.append("    (select 1 from operation op2 ");
        sql.append("    inner join op_order ord2 on ord2.order_id=op2.order_id ");
        sql.append(
                "    inner join model m2 on m2.model_id=ord2.model_id where m2.account_id=:accountId and ord2.type=:sellType and ord2.stock_id=:stockId and op2.dat_creation>op.dat_creation) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("buyType", OperationType.BUY.type);
        parameters.put("sellType", OperationType.SELL.type);
        parameters.put("stockId", stockId);

        return namedParameterjdbcTemplate.query(sql.toString(), parameters, new OperationRowMapper());
    }

    public List<OperationDBEntity> getOpenOperations(long accountId) {
        StringBuilder sql = new StringBuilder();
        sql.append(OPERATION_SELECT);
        sql.append(" inner join op_order ord on ord.order_id=op.order_id ");
        sql.append(" inner join model m on m.model_id=ord.model_id ");
        sql.append(" where m.account_id=:accountId  and ord.type=:buyType and not exists ");
        sql.append("    (select 1 from operation op2 ");
        sql.append("    inner join op_order ord2 on ord2.order_id=op2.order_id ");
        sql.append(
                "    inner join model m2 on m2.model_id=ord2.model_id where m2.account_id=:accountId and ord2.type=:sellType and ord2.stock_id=ord.stock_Id and op2.dat_creation>op.dat_creation) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("buyType", OperationType.BUY.type);
        parameters.put("sellType", OperationType.SELL.type);

        return namedParameterjdbcTemplate.query(sql.toString(), parameters, new OperationRowMapper());
    }

    public long createOperation(OperationDBEntity op) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(new PreparedStatementCreator() {

            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(
                        "insert into operation (order_id, dat_creation, size, value, stop_loss) values (?, ?, ?, ?, ?) ",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, op.getOrderId());
                ps.setDate(2, new java.sql.Date(op.getCreationDate().getTime()));
                ps.setDouble(3, op.getSize());
                ps.setDouble(4, op.getValue());
                ps.setDouble(5, op.getStopLoss());

                return ps;
            }
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public long getStockIdForOperation(long operationId) {
        return jdbcTemplate.queryForObject(
                "select ord.stock_id from operation op inner join op_order ord on ord.order_id=op.order_id where op.operation_id=?",
                new Object[] { operationId }, Long.class);
    }

    public Date getOldestOpenOperationForAccount(long accountId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select min(dat_creation) from operation op ");
        sql.append(" inner join op_order ord on ord.order_id=op.order_id ");
        sql.append(" inner join model m on m.model_id=ord.model_id ");
        sql.append(" where m.account_id=:accountId  and ord.type=:buyType and not exists ");
        sql.append("    (select 1 from operation op2 ");
        sql.append("    inner join op_order ord2 on ord2.order_id=op2.order_id ");
        sql.append(
                "    inner join model m2 on m2.model_id=ord2.model_id where m2.account_id=:accountId and ord2.type=:sellType and ord2.stock_id=ord.stock_Id and op2.dat_creation>op.dat_creation) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("buyType", OperationType.BUY.type);
        parameters.put("sellType", OperationType.SELL.type);

        return namedParameterjdbcTemplate.queryForObject(sql.toString(), parameters, Date.class);
    }

}
