package preti.stock.fe.location.home;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class HomeVO implements Serializable {

    private long accountId;
    private double accountInitialPosition;
    private double accountBalance;
    private List<WalletVO> wallet;

    public HomeVO(long accountId, double accountInitialPosition, double accountBalance, List<WalletVO> wallet) {
        super();
        this.accountId = accountId;
        this.accountInitialPosition = accountInitialPosition;
        this.accountBalance = accountBalance;
        this.wallet = wallet;
    }

    public long getAccountId() {
        return accountId;
    }

    public double getAccountInitialPosition() {
        return accountInitialPosition;
    }

    public double getAccountBalance() {
        return accountBalance;
    }

    public List<WalletVO> getWallet() {
        return wallet;
    }

    public double getWalletTotalValue() {
        if (wallet == null || wallet.isEmpty())
            return 0;

        double totalValue = 0;
        for (WalletVO w : wallet)
            totalValue += w.getTotalValue();

        return totalValue;
    }

}
