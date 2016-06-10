package preti.stock.client.run;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.web.client.RestTemplate;

import preti.stock.analysismodel.donchian.DonchianModel;
import preti.stock.db.model.OperationDBEntity;
import preti.stock.db.model.OrderDBEntity;
import preti.stock.db.model.OrderExecutionData;

public class Run {

    public static void main(String[] args) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date initialDate = dateFormat.parse("2014-02-01");
        Date finalDate = dateFormat.parse("2014-10-01");

        Map<String, Object> parameters = new HashMap<>();
        DateTime currentDate = new DateTime(initialDate.getTime());
        while (currentDate.isBefore(finalDate.getTime())) {
            System.out.println("Analysing " + dateFormat.format(currentDate.toDate()));

            parameters.put("date", dateFormat.format(currentDate.toDate()));
            parameters.put("accountId", 1);
            OrderDBEntity[] orders = restTemplate.getForObject(
                    "http://localhost:8080/recomendation/generate?accountId={accountId}&date={date}",
                    OrderDBEntity[].class, parameters);
            System.out.println(orders.length + " recomendations generated");

            orders = restTemplate.postForObject("http://localhost:8080/order/create", orders, OrderDBEntity[].class);
            System.out.println(orders.length + " orders created");

            parameters.put("accountId", 1l);
            parameters.put("execDate", dateFormat.format(currentDate.toDate()));
            List<OrderExecutionData> ordersData = new ArrayList<>();
            for (OrderDBEntity o : orders) {
                ordersData.add(new OrderExecutionData(o.getOrderId(), currentDate.toDate(), o.getValue()));
            }
            OperationDBEntity[] trades = restTemplate.postForObject(
                    "http://localhost:8080/order/execute?accountId={accountId}",
                    ordersData.toArray(new OrderExecutionData[] {}), OperationDBEntity[].class, parameters);
            System.out.println(trades.length + " trades executed");

            currentDate = currentDate.plusDays(1);
        }

        // FALTA CHAMAR CLOSE ALL OPEN TRADES;
    }

    private static DonchianModel[] mergeModels(DonchianModel[] newModel, DonchianModel[] oldModel) {
        Map<Long, DonchianModel> mapModels = new HashMap<>();
        for (DonchianModel m : newModel) {
            mapModels.put(m.getStockId(), m);
        }

        for (DonchianModel m : oldModel) {
            if (!mapModels.containsKey(m.getStockId())) {
                mapModels.put(m.getStockId(),
                        new DonchianModel(0l, m.getStockId(), 0, m.getExitDonchianSize(), m.getRiskRate()));
            }
        }

        DonchianModel[] mergedModels = new DonchianModel[mapModels.size()];
        int i = 0;
        for (DonchianModel m : mapModels.values()) {
            mergedModels[i] = m;
            i++;
        }
        return mergedModels;
    }

}
