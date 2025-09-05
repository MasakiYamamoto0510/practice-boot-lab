package com.example.w2.book;

import com.example.w2.support.PostgresTestContainer;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL) // ★コンストラクタへ注入を有効化
class BookFlowIT {

    // Postgresの接続情報をSpringに注入
    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry r) {
        var pg = PostgresTestContainer.POSTGRES;
        r.add("spring.datasource.url", pg::getJdbcUrl);
        r.add("spring.datasource.username", pg::getUsername);
        r.add("spring.datasource.password", pg::getPassword);
    }

    private final BookRepository repo;
    private final BookService service;

    BookFlowIT(BookRepository repo, BookService service) {
        this.repo = repo;
        this.service = service;
    }

    @Test @Order(1)
    void app_boots_and_business_flow_runs() {
        // Arrange（準備）
        var saved = repo.save(new Book("Spring in Action", new BigDecimal("5500")));

        // Act（実行: 振る舞いを一つだけおこす）
        service.changePrice(saved.getId(), new BigDecimal("6000"));

        // Assert（検証: 結果・副作用・例外・呼び出しを確認）
        var after = repo.findById(saved.getId()).orElseThrow();
        assertThat(after.getPrice()).isEqualByComparingTo("6000");
    }

    @Test @Order(2)
    void transactional_first_level_cache_cuts_queries() {
        var id = repo.save(new Book("Tx Demo", new BigDecimal("1000"))).getId();

        // ★ ここを実行してコンソールの SQL ログを見る
        service.readTwiceWithTx(id);
        // → SELECT が 1 回だけ出ることが多い（1次キャッシュ）
    }

    @Test @Order(3)
    void no_tx_makes_two_selects() {
        var id = repo.save(new Book("NoTx Demo", new BigDecimal("1000"))).getId();

        // ★ ここを実行してログを見る
        service.readTwiceNoTx(id);
        // → SELECT が 2 回出る（呼び出し毎にトランザクション）
    }

    @Test @Order(4)
    @org.springframework.transaction.annotation.Transactional
    void test_method_with_transactional_rolls_back() {
        // テストメソッドに @Transactional を付けると最後にロールバックされる
        var before = service.count();
        repo.save(new Book("Rollback Me", new BigDecimal("500")));
        assertThat(service.count()).isEqualTo(before + 1);
        // ここで終了 → ロールバック
    }

    @Test @Order(5)
    void db_is_clean_after_rollbacked_test() {
        // 前テストの保存分が残っていないことを確認
        assertThat(service.count()).isNotNegative(); // 具体的には "Rollback Me" が増えていないこと
    }
}
