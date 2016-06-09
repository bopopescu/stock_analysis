package preti.stock.web.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import preti.stock.db.model.OrderDBEntity;
import preti.stock.web.repository.OrderRepository;

@Service
public class OrderService {
    @SuppressWarnings("unused")
    private Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    public List<OrderDBEntity> createOrders(List<OrderDBEntity> orders) {
        List<OrderDBEntity> createdOrders = new ArrayList<>();
        for (OrderDBEntity o : orders) {
            createdOrders.add(createOrder(o));
        }

        return createdOrders;
    }

    public OrderDBEntity createOrder(OrderDBEntity order) {
        long key = orderRepository.createOrder(order);
        return orderRepository.getOrder(key);
    }

    public List<OrderDBEntity> getAllOpenOrders(long accountId) {
        return orderRepository.getAllOpenOrders(accountId);
    }
}
