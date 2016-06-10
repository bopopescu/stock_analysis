package preti.stock.db.model.wrapper;

import preti.stock.db.model.OperationDBEntity;

public class TradeWrapper {

    private OperationDBEntity buyOp, sellOp;

    public TradeWrapper(OperationDBEntity buyOp, OperationDBEntity sellOp) {
        super();
        this.buyOp = buyOp;
        this.sellOp = sellOp;
    }

    public OperationDBEntity getBuyOp() {
        return buyOp;
    }

    public OperationDBEntity getSellOp() {
        return sellOp;
    }

}
