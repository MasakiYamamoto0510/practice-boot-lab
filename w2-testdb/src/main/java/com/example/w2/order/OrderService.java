package com.example.w2.order;

import java.math.BigDecimal;
import java.util.List;;

public class OrderService{
    private final OrderRepository repo;

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    /** 受注：計算はドメイン、保存はリポジトリ */
    public Order place(String userId, List<OrderItem> items) {
        Order order = new Order(null, userId, items);
        return repo.save(order);
    }

    /** 合計金額の取得（例外系あり） */
    public BigDecimal totalOf(Long orderId) {
        Order o = repo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));
        return o.totalAmount();
    }

    /** 取消：id存在しない→例外、存在→状態変更して保存 */
    public void cancel(Long orderId) {
        Order o = repo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("order not found: " + orderId));
        if (o.getStatus() != OrderStatus.CANCELED) {
            o.cancel();
            repo.save(o);
        }
    }
}