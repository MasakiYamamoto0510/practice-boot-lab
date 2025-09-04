package com.example.w2;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class StringNormalizerPracticeTest {

    final StringNormalizer n = new StringNormalizer();

    @Test
    void multiple_spaces_and_tabs_and_newlines_are_collapsed() {
        // Arrange
        String in  = "foo    \t bar  \n  baz";
        // Act
        String out = n.normalize(in);
        // Assert
        assertThat(out).isEqualTo("foo bar baz");
        // おまけ：余計な空白や制御文字が残っていないことも確認
        assertThat(out).doesNotContain("  ").doesNotContain("\t").doesNotContain("\n");
    }

    @Test
    void zenkaku_katakana_and_dashes_unified() {
        String in  = "ﾊﾟﾝ—ｹｰｷ";          // 半角カナ + EM DASH
        String out = n.normalize(in);
        assertThat(out).isEqualTo("パン-ケーキ"); // NFKC + ダッシュ統一
    }

    @Test
    void leading_and_trailing_fullwidth_spaces_trimmed() {
        String in = "　 A 　";             // 全角スペースで囲む
        assertThat(n.normalize(in)).isEqualTo("A");
    }

    // 境界値の追加（任意だがおすすめ）
    @Test
    void whitespace_only_becomes_empty() {
        assertThat(n.normalize(" \t\n　 ")).isEmpty();
    }
}
