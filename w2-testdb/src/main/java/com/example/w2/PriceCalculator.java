package com.example.w2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class PriceCalculator {

    static final int SCALE = 0; // 円未満は四捨五入

    public BigDecimal calculateTotal(BigDecimal unitPrice,
                                     int quantity,
                                     BigDecimal taxRate,
                                     BigDecimal discountRate) {
        Objects.requireNonNull(unitPrice, "unitPrice");
        Objects.requireNonNull(taxRate, "taxRate");
        Objects.requireNonNull(discountRate, "discountRate");

        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("unitPrice must be >= 0");
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
        if (isOutOfZeroToOne(taxRate)) throw new IllegalArgumentException("taxRate must be in [0,1]");
        if (isOutOfZeroToOne(discountRate)) throw new IllegalArgumentException("discountRate must be in [0,1]");

        if (quantity == 0 || unitPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal afterDiscount = subtotal.multiply(BigDecimal.ONE.subtract(discountRate));
        BigDecimal afterTax = afterDiscount.multiply(BigDecimal.ONE.add(taxRate));

        return afterTax.setScale(SCALE, RoundingMode.HALF_UP);
    }

    private boolean isOutOfZeroToOne(BigDecimal v) {
        return v.compareTo(BigDecimal.ZERO) < 0 || v.compareTo(BigDecimal.ONE) > 0;
    }
}
