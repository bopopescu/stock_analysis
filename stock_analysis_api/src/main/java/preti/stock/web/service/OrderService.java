package preti.stock.web.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.coremodel.Order;
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
            createdOrders.add(createOrder(o));
        }

        return createdOrders;
    }

    public Order createOrder(Order order) {
        long key = orderRepository.createOrder(order);
        return orderRepository.getOrder(key);
    }

    public List<Order> getAllOpenOrders(long accountId) {
        return orderRepository.getAllOpenOrders(accountId);
    }
}
