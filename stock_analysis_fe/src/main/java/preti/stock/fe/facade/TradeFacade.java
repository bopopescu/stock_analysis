package preti.stock.fe.facade;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import preti.stock.coremodel.Trade;
import preti.stock.fe.location.TradeVO;

@Service
public class TradeFacade extends AbstractApiFacade {
    
    @Autowired
    private RestTemplate restTemplate;

    @Override
    protected String getApiPath() {
        return "trade";
    }

    //FIXME: mostrar a mensagem de erro, caso não haja saldo suficiente
    public void realizetrades(long accountId, List<TradeVO> tradesVO) {        
        URL resourceUrl = getResourceEndpoint("/realize?accountId={accountId}");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("accountId", accountId);

        Trade[] trades = new Trade[tradesVO.size()];
        for (int i = 0; i < tradesVO.size(); i++) {
            TradeVO vo = tradesVO.get(i);
            trades[i] = new Trade(vo.getStockId(), vo.getModelId(), vo.getSize(), vo.getStopPos(), vo.getBuyDate(),
                    vo.getBuyValue());
        }
        
        ResponseEntity<Void> response = restTemplate.postForEntity(resourceUrl.toString(), trades, Void.class, parameters);
        System.out.println(response);
    }

}
