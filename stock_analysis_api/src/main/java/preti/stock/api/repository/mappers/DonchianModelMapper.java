package preti.stock.api.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.analysismodel.donchian.DonchianModel;

public class DonchianModelMapper implements RowMapper<DonchianModel> {

	@Override
	public DonchianModel mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new DonchianModel(rs.getLong("model_id"), rs.getLong("stock_id"), rs.getInt("entry_size"),
				rs.getInt("exit_size"), rs.getDouble("risk_rate"));
	}

}
