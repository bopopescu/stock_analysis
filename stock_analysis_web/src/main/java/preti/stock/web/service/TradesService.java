package preti.stock.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.coremodel.Trade;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class TradesService {

	@Autowired
	private TradeRepository tradeRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void realizeTrades(long accountId, List<Trade> trades) {
		double balanceChange = 0;
		for (Trade t : trades) {
			if (t.isOpen()) {
				tradeRepository.createTrade(t, accountId);
				balanceChange -= t.getSize() * t.getBuyValue();
			} else {
				tradeRepository.updateTrade(t.getId(), t.getSellDate(), t.getSellValue());
				balanceChange += t.getSize() * t.getSellValue();
			}
		}

		accountRepository.updateBalance(accountId, balanceChange);
	}
}
