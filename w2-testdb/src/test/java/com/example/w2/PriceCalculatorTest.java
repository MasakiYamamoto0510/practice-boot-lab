package com.example.w2;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PriceCalculatorTest {

    PriceCalculator calc = new PriceCalculator();

    @Nested @DisplayName("正常系（境界値含む）")
    class Normal {
        @Test void quantity_zero_returns_zero() {
            var total = calc.calculateTotal(new BigDecimal("1000"), 0, new BigDecimal("0.10"), new BigDecimal("0.00"));
            assertThat(total).isEqualByComparingTo("0");
        }
        @Test void no_tax_no_discount_is_simple_multiply() {
            var total = calc.calculateTotal(new BigDecimal("800"), 3, new BigDecimal("0.00"), new BigDecimal("0.00"));
            assertThat(total).isEqualByComparingTo("2400");
        }
        @Test void discount_full_100_percent_results_zero() {
            var total = calc.calculateTotal(new BigDecimal("9999"), 5, new BigDecimal("0.10"), new BigDecimal("1.00"));
            assertThat(total).isEqualByComparingTo("0");
        }
        @Test void tax_full_100_percent_doubles_after_discount() {
            var total = calc.calculateTotal(new BigDecimal("500"), 2, new BigDecimal("1.00"), new BigDecimal("0.10"));
            assertThat(total).isEqualByComparingTo("1800");
        }
        @Test void rounding_half_up_at_0_scale() {
            var total = calc.calculateTotal(new BigDecimal("201"), 1, new BigDecimal("0.00"), new BigDecimal("0.50"));
            assertThat(total).isEqualByComparingTo("101");
        }
    }

    @Nested @DisplayName("異常系（範囲外や負数）")
    class Abnormal {
        @Test void negative_price_throws() {
            assertThatThrownBy(() -> calc.calculateTotal(new BigDecimal("-1"), 1, new BigDecimal("0.1"), new BigDecimal("0.0")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        @Test void negative_quantity_throws() {
            assertThatThrownBy(() -> calc.calculateTotal(new BigDecimal("1"), -1, new BigDecimal("0.1"), new BigDecimal("0.0")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        @Test void tax_over_one_throws() {
            assertThatThrownBy(() -> calc.calculateTotal(new BigDecimal("1"), 1, new BigDecimal("1.01"), new BigDecimal("0.0")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
        @Test void discount_negative_throws() {
            assertThatThrownBy(() -> calc.calculateTotal(new BigDecimal("1"), 1, new BigDecimal("0.0"), new BigDecimal("-0.01")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
