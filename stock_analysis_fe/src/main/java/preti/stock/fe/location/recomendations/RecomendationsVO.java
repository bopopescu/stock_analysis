package preti.stock.fe.location.recomendations;

import java.io.Serializable;
import java.util.List;

import preti.stock.fe.location.TradeVO;

@SuppressWarnings("serial")
public class RecomendationsVO implements Serializable {

    private long accountId;
    private List<TradeVO> trades;

    public RecomendationsVO(long accountId, List<TradeVO> trades) {
        super();
        this.accountId = accountId;
        this.trades = trades;
    }

    public long getAccountId() {
        return accountId;
    }

    public List<TradeVO> getTrades() {
        return trades;
    }

}
