package com.example.w2.book;

import com.example.w2.book.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJpaTest(properties = {
        // Hibernateにスキーマ作成/破棄させる（Flyway派なら削ってFlywayに任せる）
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2への差し替えを禁止
class BookRepositoryPgTest {

    // Postgres 16 の軽量イメージ例
    @Container
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    // Testcontainersの接続情報をSpringに注入
    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // 方言はURLから自動判定される（BootがHibernate Dialectを設定）
    }

    @Autowired
    BookRepository repo;

    @Test
    void save_and_find() {
        Book b = new Book("Effective Java", new BigDecimal("5500"));
        Book saved = repo.saveAndFlush(b);

        assertThat(saved.getId()).isNotNull();

        var found = repo.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void ddl_violation_when_title_is_null() {
        Book bad = new Book(null, new BigDecimal("1000"));
        assertThatThrownBy(() -> repo.saveAndFlush(bad))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void case_insensitive_search_should_work_across_dialects() {
        // H2とPostgresでケース感度が違っても通したい → Spring Dataの IgnoreCase を利用
        repo.saveAndFlush(new Book("Java Concurrency in Practice", new BigDecimal("6600")));

        // 例：BookRepository に findByTitleContainingIgnoreCase(String q) を用意しておく
        var results = repo.findByTitleContainingIgnoreCase("concurrency");
        assertThat(results).extracting(Book::getTitle)
                .contains("Java Concurrency in Practice");
    }
}
