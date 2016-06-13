package preti.stock.api.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.api.model.db.OperationDBEntity;

public class OperationRowMapper implements RowMapper<OperationDBEntity> {

    @Override
    public OperationDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new OperationDBEntity(rs.getLong("operation_id"), rs.getLong("order_id"), rs.getDate("dat_creation"),
                rs.getDouble("size"), rs.getDouble("value"), rs.getDouble("stop_loss"));
    }

}
