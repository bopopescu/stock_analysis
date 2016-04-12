package preti.stock.web.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.DonchianModelRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TradeRepository tradeRepository;
	@Autowired
	private DonchianModelRepository modelRepository;

	public Account loadAccount(long accountId) {
		return accountRepository.getAccount(accountId);
	}

	public Account loadAccountWithWallet(long accountId) {
		Account account = loadAccount(accountId);
		account.setWallet(tradeRepository.getOpenTradesForAccount(accountId));
		return account;
	}

	public Account loadCompleteAccount(long accountId, Date recomendationDate) {
		Account account = loadAccount(accountId);
		account.setModel(modelRepository.getActiveModel(accountId, recomendationDate));
		account.setStockCodesToAnalyze(accountRepository.getStocksToAnalyse(accountId));
		account.setWallet(tradeRepository.getOpenTradesForAccount(accountId));

		return account;

	}

}
