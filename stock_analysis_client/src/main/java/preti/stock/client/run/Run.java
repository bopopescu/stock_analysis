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

import preti.stock.client.OrderExecutionData;
import preti.stock.client.model.Operation;
import preti.stock.client.model.Order;

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
            Order[] orders = restTemplate.getForObject(
                    "http://localhost:8080/recomendation/generate?accountId={accountId}&date={date}", Order[].class,
                    parameters);
            System.out.println(orders.length + " recomendations generated");

            orders = restTemplate.postForObject("http://localhost:8080/order/create", orders, Order[].class);
            System.out.println(orders.length + " orders created");

            parameters.put("accountId", 1l);
            parameters.put("execDate", dateFormat.format(currentDate.toDate()));
            List<OrderExecutionData> ordersData = new ArrayList<>();
            for (Order o : orders) {
                ordersData.add(new OrderExecutionData(o.getOrderId(), currentDate.toDate(), o.getValue()));
            }
            Operation[] trades = restTemplate.postForObject("http://localhost:8080/order/execute?accountId={accountId}",
                    ordersData.toArray(new OrderExecutionData[] {}), Operation[].class, parameters);
            System.out.println(trades.length + " trades executed");

            currentDate = currentDate.plusDays(1);
        }

        // FALTA CHAMAR CLOSE ALL OPEN TRADES;
    }

}
