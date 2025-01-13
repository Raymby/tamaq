package com.example.tamaq.Services;

import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.Order;
import com.example.tamaq.Entities.OrderItem;
import com.example.tamaq.Entities.User;
import com.example.tamaq.Repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    // Создание заказа
    public Order createOrder(User user, String phone, String address, String deliveryDate, String deliveryTime, String comments) {
        List<CartItem> cartItems = cartService.getCartItems(user);  // Получаем товары из корзины

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("Корзина пуста");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("Ожидается");
        order.setPhone(phone);
        order.setAddress(address);
        order.setDeliveryDate(deliveryDate);
        order.setDeliveryTime(deliveryTime);
        order.setComments(comments);

        double totalAmount = 0.0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());

            totalAmount += orderItem.getPrice();  // Рассчитываем общую сумму
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);  // Устанавливаем общую сумму
        order.setOrderItems(orderItems);  // Добавляем элементы заказа

        orderRepository.save(order);  // Сохраняем заказ

        cartService.clearCart(user);  // Очищаем корзину

        return order;
    }

    // Получение списка заказов пользователя
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public Order getOrderById(Long id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            return optionalOrder.get();
        }
        throw new IllegalStateException("Заказ с ID " + id + " не найден.");
    }
}
