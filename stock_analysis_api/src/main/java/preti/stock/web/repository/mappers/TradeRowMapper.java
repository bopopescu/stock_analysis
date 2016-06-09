package preti.stock.web.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import preti.stock.db.model.TradeDBEntity;

public class TradeRowMapper implements RowMapper<TradeDBEntity> {

    @Override
    public TradeDBEntity mapRow(ResultSet rs, int rowNum) throws SQLException {

        TradeDBEntity trade = new TradeDBEntity();
        trade.setId(rs.getLong("trade_id"));
        trade.setStockId(rs.getLong("stock_id"));
        trade.setBuyDate(rs.getDate("buy_date"));
        trade.setSellDate(rs.getDate("sell_date"));
        trade.setBuyValue(rs.getDouble("buy_value"));
        trade.setSellValue(rs.getDouble("sell_value"));
        trade.setSize(rs.getDouble("size"));
        trade.setStopPos(rs.getDouble("stop_pos"));

        return trade;
    }
}
