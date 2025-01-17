package com.example.tamaq.Repositories;
import com.example.tamaq.Entities.Order;
import com.example.tamaq.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
