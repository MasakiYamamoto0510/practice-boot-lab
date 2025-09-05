package com.example.w2.support;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer {

    // ① コンテナの「ハンドル（取っ手）」を 1 個だけ作る
    public static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine") // ← 使うイメージ（軽量な Alpine 版）
                    .withDatabaseName("appdb")                              // ← 起動時に作るDB名
                    .withUsername("app")                                    // ← ユーザー
                    .withPassword("app");                                   // ← パスワード

    // ② クラス読み込み時に1度だけ start()
    static {
        POSTGRES.start();
        // ここで Testcontainers が:
        // - 画像が無ければ pull
        // - コンテナ起動
        // - "DB 受け付け可能" になるまで待機（ログやヘルスチェック）
        // - ランダムなポートに公開（固定5432ではない）
    }

    // ③ インスタンス化させない（ユーティリティクラス）
    private PostgresTestContainer() {}
}
