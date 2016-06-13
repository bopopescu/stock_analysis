package preti.stock.fe.location.home;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.client.RemoteApiException;
import preti.stock.client.model.Account;
import preti.stock.client.model.Stock;
import preti.stock.client.model.StockHistory;
import preti.stock.client.model.Wallet;
import preti.stock.fe.facade.AccountFacade;
import preti.stock.fe.facade.StockFacade;
import preti.stock.fe.service.DateService;

@Service
public class HomeService {

    @Autowired
    private AccountFacade accountFacade;

    @Autowired
    private StockFacade stockFacade;

    @Autowired
    private DateService dateService;

    public HomeVO getHomeData(long accountId) throws RemoteApiException {
        Account account = accountFacade.getAccountWithWallet(accountId);
        account.getWallet().removeIf(w -> w.getSize() == 0);

        List<WalletVO> wallet = buildWalletForAccount(account);
        return new HomeVO(accountId, account.getInitialPosition(), account.getBalance(), wallet);
    }

    private List<WalletVO> buildWalletForAccount(Account account) throws RemoteApiException {
        List<WalletVO> wallet = new ArrayList<>();

        // FIXME: 20 dias fixo
        Date today = dateService.getCurrentSystemDate();
        Date initialHistoryDate = new DateTime(today.getTime()).minusDays(20).toDate();

        for (Wallet w : account.getWallet()) {
            Stock stock = stockFacade.getStock(w.getStockId());
            StockHistory[] history = stockFacade.getStockHistory(w.getStockId(), initialHistoryDate, today);
            stock.setHistory(Arrays.asList(history));

            wallet.add(new WalletVO(w.getWalletId(), stock.getName(), stock.getCode(), w.getSize(),
                    stock.getCloseValueAtDate(today), w.getCreationDate(), w.getUpdateDate()));
        }

        return wallet;
    }
}
