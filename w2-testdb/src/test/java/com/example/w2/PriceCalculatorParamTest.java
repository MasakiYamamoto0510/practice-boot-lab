package com.example.w2;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class PriceCalculatorParamTest {

    final PriceCalculator calc = new PriceCalculator();

    @ParameterizedTest(name = "[{index}] qty={0}, price={1}, tax={2}, disc={3} => {4}")
    @CsvSource({
            // qty, price, tax,  disc, expected
            "0,   1000,  0.10,  0.00,  0",
            "1,    800,  0.00,  0.00,  800",
            "2,    500,  1.00,  0.10,  1800",
            "1,    201,  0.00,  0.50,  101"
    })
    void calc_patterns(int qty, String price, String tax, String disc, String expected) {
        var total = calc.calculateTotal(new BigDecimal(price), qty,
                new BigDecimal(tax), new BigDecimal(disc));
        assertThat(total).isEqualByComparingTo(expected);
    }
}
