package com.example.w2.book;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop" // テスト毎にDDL作成・破棄
})
class BookRepositoryTest {

    @Autowired
    BookRepository repo;

    @Test
    void save_and_find() {
        Book b = new Book("プロになるJava", new BigDecimal("2750"));
        Book saved = repo.saveAndFlush(b);

        assertThat(saved.getId()).isNotNull();

        assertThat(repo.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Book::getTitle)
                .isEqualTo("プロになるJava");

        assertThat(repo.findByTitle("プロになるJava"))
                .isPresent()
                .get()
                .extracting(Book::getPrice)
                .asInstanceOf(BIG_DECIMAL)
                .isEqualByComparingTo("2750");
    }

    @Test
    void ddl_violation_when_title_is_null() {
        Book bad = new Book(null, new BigDecimal("1000"));
        assertThatThrownBy(() -> repo.saveAndFlush(bad))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
