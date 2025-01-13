package com.example.tamaq.Services;

import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.Product;
import com.example.tamaq.Entities.User;
import com.example.tamaq.Repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {
    @Autowired
    private CartItemRepository cartItemRepository;

    public CartItem addToCart(Product product, int quantity, User user) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть больше нуля.");
        }

        CartItem cartItem = cartItemRepository.findByProductIdAndUserId(product.getId(), user.getId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUser(user);
        }

        cartItemRepository.save(cartItem);

        return cartItem;
    }

    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUserId(user.getId());
    }

    public void removeItem(Long id) {
        CartItem cartItem = findById(id);
        if (cartItem != null) {
            cartItemRepository.delete(cartItem);
        } else {
            throw new IllegalArgumentException("Элемент не найден в корзине.");
        }
    }

    public CartItem findById(Long id) {
        return cartItemRepository.findById(id).orElse(null);
    }


    public void clearCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (!cartItems.isEmpty()) {
            cartItemRepository.deleteAll(cartItems); // Удаляем все элементы корзины пользователя
        }
}

    public void save(CartItem cartItem) {
        cartItemRepository.save(cartItem);
    }

    public BigDecimal calculateTotalPrice(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        return cartItems.stream()
                .map(item -> {
                    BigDecimal price = BigDecimal.valueOf(item.getProduct().getPrice()); // Преобразование Double в BigDecimal
                    return price.multiply(BigDecimal.valueOf(item.getQuantity())); // Умножение цены на количество
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Суммирование итоговой стоимости
    }

}
