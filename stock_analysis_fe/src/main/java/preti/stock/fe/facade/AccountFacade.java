package preti.stock.fe.facade;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.client.RemoteApiException;
import preti.stock.client.model.Account;

@Service
public class AccountFacade extends AbstractApiFacade {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    protected String getApiPath() {
        return "account";
    }

    public Account getAccountWithWallet(long accountId) throws RemoteApiException {
        URL resourceUrl = getResourceEndpoint("/getAccountWithWallet?accountId={accountId}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);

        ResponseEntity<Account> response = restTemplate.getForEntity(resourceUrl.toString(), Account.class, parameters);
        if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            return null;
        }
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RemoteApiException(response.getStatusCode());
        }
        Account account = response.getBody();

        return account;
    }

}
