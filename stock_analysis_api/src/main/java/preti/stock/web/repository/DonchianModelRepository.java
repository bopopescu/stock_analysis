package preti.stock.web.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.web.repository.mappers.DonchianModelMapper;

@Repository
public class DonchianModelRepository {
    private NamedParameterJdbcTemplate namedParameterjdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterjdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<DonchianModel> getActiveModel(long accountId, Date modelDate) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);
        parameters.put("modelDate", modelDate);

        StringBuilder query = new StringBuilder();
        query.append("select m.model_id, dme.stock_id, dme.entry_size, dme.exit_size, dme.risk_rate ");
        query.append("from model m ");
        query.append("inner join donchian_model_entry dme on m.model_id=dme.model_id ");
        query.append("where ");
        query.append("m.account_id=:accountId and m.dat_start<=:modelDate and  m.dat_end>=:modelDate ");
        List<DonchianModel> newModel = namedParameterjdbcTemplate.query(query.toString(), parameters,
                new DonchianModelMapper());

        query = new StringBuilder();
        query.append("select  ");
        query.append("m.model_id, dme.stock_id, 0 as entry_size, dme.exit_size, dme.risk_rate ");
        query.append("from model m ");
        query.append("inner join op_order o on o.model_id=m.model_id and o.account_id=m.account_id ");
        query.append("inner join trade t on t.buy_order_id=o.order_id and m.account_id=t.account_id ");
        query.append("inner join donchian_model_entry dme on dme.model_id=o.model_id and dme.stock_id=t.stock_id ");
        query.append("where ");
        query.append(
                "t.account_id=:accountId and t.buy_date<:modelDate and t.sell_date is null and m.dat_end<:modelDate ");
        List<DonchianModel> oldModel = namedParameterjdbcTemplate.query(query.toString(), parameters,
                new DonchianModelMapper());

        List<DonchianModel> resultModel = new ArrayList<>();
        resultModel.addAll(newModel);

        // FIXME: melhorar isso
        outer: for (DonchianModel om : oldModel) {
            for (DonchianModel nm : newModel) {
                if (nm.getStockId() == om.getStockId()) {
                    continue outer;
                }
            }
            resultModel.add(om);
        }

        return resultModel;
    }

    public int getMaxDonchianChannelSize() {
        return 100;
    }
}
