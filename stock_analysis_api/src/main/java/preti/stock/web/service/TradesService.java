package preti.stock.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import preti.stock.analysismodel.donchian.Account;
import preti.stock.coremodel.Trade;
import preti.stock.web.ApiError;
import preti.stock.web.exception.ApiValidationException;
import preti.stock.web.repository.AccountRepository;
import preti.stock.web.repository.TradeRepository;

@Service
public class TradesService {

    @Autowired
    private TradeRepository tradeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ApiValidationException.class)
    public void realizeTrades(long accountId, List<Trade> trades) throws ApiValidationException {
        double balanceChange = 0;
        for (Trade t : trades) {
            if (t.isOpen()) {
                tradeRepository.createTrade(t, accountId);
                balanceChange -= t.getSize() * t.getBuyValue();
            } else {
                tradeRepository.closeTrade(t.getId(), t.getSellDate(), t.getSellValue());
                balanceChange += t.getSize() * t.getSellValue();
            }
        }

        Account account = accountRepository.getAccount(accountId);
        if (account.getBalance() + balanceChange < 0) {
            throw new ApiValidationException(ApiError.TRADE_INSUFICIENT_BALANCE);
        }

        accountRepository.updateBalance(accountId, balanceChange);
    }
}
