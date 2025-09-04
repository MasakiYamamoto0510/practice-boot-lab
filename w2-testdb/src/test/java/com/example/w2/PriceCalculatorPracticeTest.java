package com.example.w2;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*; // ← assertThat / assertThatTrownBy を使う

import java.math.BigDecimal;

class PriceCalculatorPracticeTest {

    final PriceCalculator calc = new PriceCalculator();

    @Test
    void qty1_price1200_tax10_discount20_returns_1056() {
        // Arrange & Act
        var total = calc.calculateTotal(
                new BigDecimal("1200"), 1,
                new BigDecimal("0.10"),
                new BigDecimal("0.20"));

        // Assert
        // 1200 * (1-0.2)=960 → *1.1=1056 → 0桁四捨五入で 1056
        assertThat(total).isEqualByComparingTo("1056");
    }

    @Test
    void unitPrice_zero_returns_zero() {
        // TODO: 単価0, qty>0, 税率/割引色々 → 期待 0
    }

    @Test
    void very_large_quantity_still_works() {
        // TODO: gty=100000などでも例外にならず、計算ができること
        // ヒント：期待値は電卓/紙で計算し、isEqualByComparingToで比較
    }

    @Test
    void negative_discount_throws() {
        // TODO: 割引率 -0.01でIllegalArgumnetException
        // assertThatThrownBy(() -> calc.calculateTotal(...)).isInstanceOf(IllegalArgumentException.class);
    }
}
