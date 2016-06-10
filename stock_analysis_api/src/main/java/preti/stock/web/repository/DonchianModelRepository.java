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
import preti.stock.db.model.OperationType;
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
        parameters.put("buyType", OperationType.BUY.type);
        parameters.put("sellType", OperationType.SELL.type);

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
        query.append("inner join op_order o on o.model_id=m.model_id ");
        query.append("inner join operation op on op.order_id=o.order_id ");
        query.append("inner join donchian_model_entry dme on dme.model_id=o.model_id and dme.stock_id=o.stock_id ");
        query.append("where ");
        query.append(
                "o.type=:buyType and m.account_id=:accountId and op.dat_creation<:modelDate and m.dat_end<:modelDate ");
        query.append(" and not exists ( ");
        query.append(" select 1 from operation op2 ");
        query.append("    inner join op_order ord2 on ord2.order_id=op2.order_id ");
        query.append("    inner join model m2 on m2.model_id=ord2.model_id where m2.account_id=:accountId and ord2.type=:sellType and ord2.stock_id=o.stock_id and op2.dat_creation>op.dat_creation ");
        query.append(") ");
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
