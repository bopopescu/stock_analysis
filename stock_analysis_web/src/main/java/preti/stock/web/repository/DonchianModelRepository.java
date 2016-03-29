package preti.stock.web.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.web.repository.mappers.DonchianModelMapper;

@Repository
public class DonchianModelRepository {
	private JdbcTemplate jdbcTemplate;
	private NamedParameterJdbcTemplate namedParameterjdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.namedParameterjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<DonchianModel> getActiveModel(long accountId, Date modelDate) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("accountId", accountId);
		parameters.put("modelDate", modelDate);
		
		StringBuilder query = new StringBuilder();
		query.append("select s.stock_code, dm.model_id, dme.entry_size, dme.exit_size, dme.risk_rate ");
		query.append("from donchian_model_entry dme ");
		query.append("inner join stock s on s.stock_id=dme.stock_id ");
		query.append("inner join model dm on dm.model_id=dme.model_id ");
		query.append("where ");
		query.append("dm.account_id=:accountId and dm.dat_start<=:modelDate and  dm.dat_end>=:modelDate ");
		List<DonchianModel> newModel = namedParameterjdbcTemplate.query(query.toString(), parameters, new DonchianModelMapper());
		
		
		query = new StringBuilder();
		query.append("select  ");
		query.append("s.stock_code, m.model_id, 0 as entry_size, dme.exit_size, dme.risk_rate ");
		query.append("from ");
		query.append("trade t ");
		query.append("inner join stock s on s.stock_id=t.stock_id ");
		query.append("inner join model m on m.model_id=t.model_id and m.account_id=t.account_id ");
		query.append("inner join donchian_model_entry dme on dme.model_id=t.model_id and dme.stock_id=t.stock_id ");
		query.append("where ");
		query.append("t.account_id=:accountId and t.buy_date<:modelDate and t.sell_date is null and m.dat_end<:modelDate ");
		List<DonchianModel> oldModel = namedParameterjdbcTemplate.query(query.toString(), parameters, new DonchianModelMapper());
		
		List<DonchianModel> resultModel = new ArrayList<>();
		resultModel.addAll(newModel);
		
		//FIXME: melhorar isso
		outer: for(DonchianModel om : oldModel){
			for(DonchianModel nm : newModel) {
				if(nm.getStock().equals(om.getStock())){
					continue outer;
				}
			}
			resultModel.add(om);
		}

		

		return resultModel;
	}

	public int getMaxEntrySize(long accountId, Date modelDate) {
		StringBuilder query = new StringBuilder();
		query.append("select max(entry_size) ");
		query.append("from donchian_model_entry dme ");
		query.append("inner join model dm on dm.model_id=dme.model_id ");
		query.append("where ");
		query.append("dm.account_id=? and dm.dat_start<=? and  dm.dat_end>=?");
		return jdbcTemplate.queryForObject(query.toString(), new Object[] { accountId, modelDate, modelDate },
				Integer.class);
	}

}
