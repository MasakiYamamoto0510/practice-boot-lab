package com.example.w2;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StringNormalizerTest {

    StringNormalizer n = new StringNormalizer();

    @Test void null_becomes_empty() {
        assertThat(n.normalize(null)).isEmpty();
    }
    @Test void trims_and_collapses_spaces_tabs_newlines() {
        String in = "  foo\tbar   \n baz  ";
        assertThat(n.normalize(in)).isEqualTo("foo bar baz");
    }
    @Test void fullwidth_space_is_collapsed() {
        String in = "　foo　bar　";
        assertThat(n.normalize(in)).isEqualTo("foo bar");
    }
    @Test void unicode_width_nfkc() {
        String in = "ＡＢｃ１２３";
        assertThat(n.normalize(in)).isEqualTo("ABc123");
    }
    @Test void various_dashes_to_hyphen() {
        String in = "a–b—c−d";
        assertThat(n.normalize(in)).isEqualTo("a-b-c-d");
    }
}
