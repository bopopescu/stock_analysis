package preti.stock.web.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.coremodel.BuyOrder;
import preti.stock.coremodel.Order;
import preti.stock.coremodel.SellOrder;
import preti.stock.web.repository.OrderRepository;

@Service
public class OrderService {
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    public List<Order> createOrders(List<Order> orders) {
        List<Order> createdOrders = new ArrayList<>();
        for (Order o : orders) {
            Order createdOrder;
            switch (o.getType()) {
            case BUY:
                createdOrder = createBuyOrder((BuyOrder) o);
                break;
            case SELL:
                createdOrder = createSellOrder((SellOrder) o);
                break;
            default:
                throw new RuntimeException(String.format("Invalid order type: %s ", o.getType()));
            }
            createdOrders.add(createdOrder);
        }

        return createdOrders;
    }

    public BuyOrder createBuyOrder(BuyOrder order) {
        long key = orderRepository.createBuyOrder(order);
        return orderRepository.getBuyOrder(key);
    }

    public SellOrder createSellOrder(SellOrder order) {
        long key = orderRepository.createSellOrder(order);
        return orderRepository.getSellOrder(key);
    }

}
