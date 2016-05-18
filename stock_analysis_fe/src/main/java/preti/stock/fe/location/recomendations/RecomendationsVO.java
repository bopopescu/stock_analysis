package preti.stock.fe.location.recomendations;

import java.io.Serializable;
import java.util.List;

import preti.stock.fe.location.OrderVO;

@SuppressWarnings("serial")
public class RecomendationsVO implements Serializable {

    private long accountId;
    private List<OrderVO> orders;

    public RecomendationsVO(long accountId, List<OrderVO> orders) {
        super();
        this.accountId = accountId;
        this.orders = orders;
    }

    public long getAccountId() {
        return accountId;
    }

    public List<OrderVO> getOrders() {
        return orders;
    }

}
