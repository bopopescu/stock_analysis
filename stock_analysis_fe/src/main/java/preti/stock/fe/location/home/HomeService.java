package preti.stock.fe.location.home;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.coremodel.Stock;
import preti.stock.coremodel.StockHistory;
import preti.stock.coremodel.Trade;
import preti.stock.fe.facade.AccountFacade;
import preti.stock.fe.facade.RemoteApiException;
import preti.stock.fe.facade.StockFacade;
import preti.stock.fe.service.DateService;

@Service
public class HomeService {
	private final String DATE_FORMAT = "yyyy-MM-dd";

	@Autowired
	private AccountFacade accountFacade;

	@Autowired
	private StockFacade stockFacade;

	@Autowired
	private DateService dateService;

	public HomeVO getHomeData(long accountId) throws RemoteApiException {
		Account account = accountFacade.getAccountWithWallet(accountId);

		List<TradeVO> trades = createTradesForAccount(account);
		return new HomeVO(accountId, account.getInitialPosition(), account.getBalance(), trades);
	}

	private List<TradeVO> createTradesForAccount(Account account) throws RemoteApiException {
		List<TradeVO> trades = new ArrayList<>();

		Date today = dateService.getCurrentSystemDate();
		Date initialHistoryDate = new DateTime(today.getTime()).minusDays(20).toDate();

		for (Trade t : account.getWallet()) {
			Stock stock = stockFacade.getStock(t.getStockId());
			StockHistory[] history = stockFacade.getStockHistory(t.getStockId(), initialHistoryDate, today);
			stock.setHistory(Arrays.asList(history));

			trades.add(
					new TradeVO(t.getId(), stock.getId(), stock.getCode(), stock.getName(), t.getSize(), t.getStopPos(),
							t.getBuyDate(), t.getSellDate(), t.getBuyValue(), stock.getCloseValueAtDate(today)));
		}

		return trades;
	}
}
