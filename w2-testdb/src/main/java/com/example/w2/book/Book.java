package com.example.w2.book;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "books", indexes = @Index(name = "idx_books_title", columnList = "title"))
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    protected Book() {} // JPA用 JPAがリフレクションでエンティティをNEWする時に使うもの

    public Book(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public BigDecimal getPrice() { return price; }
    public void setTitle(String title) { this.title = title; }
    public void setPrice(BigDecimal price) { this.price = price; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book other = (Book) o;
        return id != null && id.equals(other.id);
    }
    @Override public int hashCode() { return Objects.hashCode(id); }

    @Override public String toString() {
        return "Book{id=" + id + ", title='" + title + "', price=" + price + "}";
    }
}
