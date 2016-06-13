package preti.stock.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.client.model.Account;
import preti.stock.client.model.Wallet;
import preti.stock.db.model.AccountDBEntity;
import preti.stock.db.model.wrapper.AccountWrapper;
import preti.stock.web.service.AccountService;

@RestController
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(path = "/account/getAccountWithWallet", headers = "Accept=application/json", method = RequestMethod.GET)
    public Account getAccountWithWallet(@RequestParam(name = "accountId", required = true) long accountId) {
        logger.info(String.format("getAccountWithWallet accountId=%s", accountId));

        AccountWrapper accountWrapper = accountService.loadAccountWithWallet(accountId);
        AccountDBEntity accountDB = accountWrapper.getTarget();
        List<Wallet> wallet = new ArrayList<>();
        accountWrapper.getWallet().forEach(w -> wallet.add(new Wallet(w.getWalletId(), w.getStockId(), w.getAccountId(),
                w.getSize(), w.getCreationDate(), w.getUpdateDate())));
        Account result = new Account(accountDB.getId(), accountDB.getBalance(), accountDB.getInitialPosition(), wallet);

        return result;
    }
}
