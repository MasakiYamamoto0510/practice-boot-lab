package com.example.w2.order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** 集約ルート（計算はここで完結） */
public class Order {
    private Long id;                  // 永続化前はnull想定
    private String userId;
    private List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.NEW;

    public Order(Long id, String userId, List<OrderItem> items) {
        if (userId == null || items == null) throw new IllegalArgumentException("null not allowed");
        if (items.isEmpty()) throw new IllegalArgumentException("items empty");
        this.id = id;
        this.userId = userId;
        this.items = List.copyOf(items);
    }
    public BigDecimal totalAmount() {
        return items.stream().map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void cancel() { this.status = OrderStatus.CANCELED; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) &&
                userId.equals(order.userId) &&
                items.equals(order.items) &&
                status == order.status;
    }
    @Override public int hashCode() { return Objects.hash(id, userId, items, status); }
}
