package preti.stock.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import preti.stock.db.model.wrapper.AccountWrapper;
import preti.stock.web.service.AccountService;

@RestController
public class AccountController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @RequestMapping(path = "/account/getAccountWithWallet", headers = "Accept=application/json", method = RequestMethod.GET)
    public AccountWrapper getAccountWithWallet(@RequestParam(name = "accountId", required = true) long accountId) {
        logger.info(String.format("getAccountWithWallet accountId=%s", accountId));
        return accountService.loadAccountWithWallet(accountId);
    }
}
