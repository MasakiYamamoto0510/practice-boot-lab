package com.example.w2;

import java.text.Normalizer;

public class StringNormalizer {

    public String normalize(String input) {
        if (input == null) return "";

        String s = Normalizer.normalize(input, Normalizer.Form.NFKC);

        s = s
                .replace('\u2013', '-') // EN DASH
                .replace('\u2014', '-') // EM DASH
                .replace('\u2212', '-'); // MINUS SIGN

        s = s.replaceAll("[\\s\\u3000]+", " ").trim();
        return s;
    }
}
