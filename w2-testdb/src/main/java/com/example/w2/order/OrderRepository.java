package com.example.w2.order;

import java.util.Optional;

/** DBなしユニットテスト用の最小インターフェース */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    boolean exisitsById(Long id);
    void deleteById(Long id);
}
