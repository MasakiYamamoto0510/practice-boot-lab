package com.example.w2.order;

import java.math.BigDecimal;
import java.util.Objects;

/** 単価*数量の行アイテム */
public class OrderItem {
    private final String name;
    private final BigDecimal unitPrice;
    private final int quantity;


    public OrderItem(String name, BigDecimal unitPrice, int quantity) {
        if (name == null || unitPrice == null) throw new IllegalArgumentException("null not allowed");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be positive");
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return quantity == that.quantity &&
                name.equals(that.name) && unitPrice.equals(that.unitPrice);
    }
    @Override public int hashCode() { return Objects.hash(name, unitPrice, quantity); }
    }
