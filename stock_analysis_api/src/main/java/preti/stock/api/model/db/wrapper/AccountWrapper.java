package preti.stock.api.model.db.wrapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.api.model.db.AccountDBEntity;
import preti.stock.api.model.db.WalletDBEntity;

@SuppressWarnings("serial")
public class AccountWrapper implements Serializable {

    private AccountDBEntity target;
    private List<DonchianModel> model;
    private Collection<WalletDBEntity> wallet;
    private List<String> stockCodesToAnalyze;

    public AccountDBEntity getTarget() {
        return target;
    }

    public void setTarget(AccountDBEntity target) {
        this.target = target;
    }

    public List<DonchianModel> getModel() {
        return model;
    }

    public void setModel(List<DonchianModel> model) {
        this.model = model;
    }

    public Collection<WalletDBEntity> getWallet() {
        return wallet;
    }

    public void setWallet(Collection<WalletDBEntity> wallet) {
        this.wallet = wallet;
    }

    public List<String> getStockCodesToAnalyze() {
        return stockCodesToAnalyze;
    }

    public void setStockCodesToAnalyze(List<String> stockCodesToAnalyze) {
        this.stockCodesToAnalyze = stockCodesToAnalyze;
    }

}
