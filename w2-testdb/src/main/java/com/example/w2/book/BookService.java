package com.example.w2.book;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BookService {
    private final BookRepository repo;
    public BookService(BookRepository repo) { this.repo = repo; }

    // ① 取引境界あり：1次キャッシュが効く
    @Transactional
    public String readTwiceWithTx(Long id) {
        var a = repo.findById(id).orElseThrow();
        var b = repo.findById(id).orElseThrow(); // 同一 Tx → 2 回目は SQL が出にくい
        return a.getTitle() + "/" + b.getTitle();
    }

    // ② 取引境界なし：リポジトリ呼び出し毎に個別トランザクション
    public String readTwiceNoTx(Long id) {
        var a = repo.findById(id).orElseThrow();
        var b = repo.findById(id).orElseThrow(); // 2 回とも SELECT が出やすい
        return a.getTitle() + "/" + b.getTitle();
    }

    @Transactional
    public void changePrice(Long id, BigDecimal newPrice) {
        var book = repo.findById(id).orElseThrow();
        book.setPrice(newPrice); // Dirty Checking で UPDATE
    }

    public long count() { return repo.count(); }
}
