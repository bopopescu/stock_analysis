package preti.stock.fe.location.home;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.Trade;
import preti.stock.fe.facade.AccountFacade;
import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.facade.StockFacade;

@Service
public class HomeService {

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private StockFacade stockFacade;

	public HomeVO getHomeData(long accountId) throws RemoteApiException {
		Account account = accountFacade.getAccountWithWallet(accountId);

		List<TradeVO> trades = createTradesForAccount(account);
		return new HomeVO(accountId, account.getInitialPosition(), account.getBalance(), trades);
	}

	public List<TradeVO> createTradesForAccount(Account account) throws RemoteApiException {
		List<TradeVO> trades = new ArrayList<>();

		for (Trade t : account.getWallet()) {
			Stock stock = stockFacade.getStock(t.getStockId());

			trades.add(new TradeVO(t.getId(), stock.getId(), stock.getCode(), stock.getName(), t.getSize(),
					t.getStopPos(), t.getBuyDate(), t.getSellDate(), t.getBuyValue(), t.getSellValue()));
		}

		return trades;
	}
}
