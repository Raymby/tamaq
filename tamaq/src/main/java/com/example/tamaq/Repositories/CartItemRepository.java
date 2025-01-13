package com.example.tamaq.Repositories;

import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.CartItem;
import com.example.tamaq.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUser(User user);

    CartItem findByProductIdAndUserId(Long id, Long id1);

    List<CartItem> findByUserId(Long id);
}
