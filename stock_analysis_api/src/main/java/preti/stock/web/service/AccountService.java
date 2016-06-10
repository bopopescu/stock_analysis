package preti.stock.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.db.model.AccountDBEntity;
import preti.stock.db.model.wrapper.AccountWrapper;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.DonchianModelRepository;
import preti.stock.web.repository.WalletRepository;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DonchianModelRepository modelRepository;

    @Autowired
    private WalletRepository walletRepository;

    public AccountDBEntity loadAccount(long accountId) {
        return accountRepository.getAccount(accountId);
    }

    public AccountWrapper loadAccountWithWallet(long accountId) {
        AccountWrapper account = new AccountWrapper();
        account.setTarget(loadAccount(accountId));
        account.setWallet(walletRepository.getWalletForAccount(accountId));
        return account;
    }

    public AccountWrapper loadCompleteAccount(long accountId, Date recomendationDate) {
        AccountWrapper account = new AccountWrapper();
        account.setTarget(loadAccount(accountId));
        account.setModel(modelRepository.getActiveModel(accountId, recomendationDate));
        account.setStockCodesToAnalyze(accountRepository.getStocksToAnalyse(accountId));
        account.setWallet(walletRepository.getWalletForAccount(accountId));

        return account;

    }

}
