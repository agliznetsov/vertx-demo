package com.example.demo;
//
//import com.example.demo.model.Article;
//import io.vertx.core.Vertx;
//import io.vertx.core.json.Json;
//import io.vertx.junit5.Timeout;
//import io.vertx.junit5.VertxExtension;
//import io.vertx.junit5.VertxTestContext;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@ExtendWith(VertxExtension.class)
//public class DBTest {
//
//    @BeforeEach
//    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
//        vertx.deployVerticle(new MainVerticle(), testContext.succeeding(id -> testContext.completeNow()));
//    }
//
//
//}

import io.vertx.core.Context;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.test.core.VertxTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class DBTest extends VertxTestBase {


    private static JsonObject dbConfig() {
        return new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver");
    }

    @BeforeClass
    public static void createDb() throws Exception {
        Connection conn = DriverManager.getConnection(dbConfig().getString("url"));
        conn.createStatement().execute("create table edges(source int, target int)");
    }

    SQLClient client;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = JDBCClient.createNonShared(vertx, dbConfig());
    }

    @After
    public void after() throws Exception {
        client.close();
        super.after();
    }

    @Test
    public void testStreamData() {
        SQLConnection connection = connection();

        UndirectedGraph graph = GraphUtils.createSimpleGraph(100, true);

        GraphDAO dao = new GraphDAO(connection);
        dao.insertData(graph.toJson(), onSuccess(insertResult -> {
            dao.streamData(onSuccess(graph2 -> {
                assertEquals(100, graph2.size());
                testComplete();
            }));
        }));

        await();
    }

    protected SQLConnection connection() {
        return connection(vertx.getOrCreateContext());
    }

    protected SQLConnection connection(Context context) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<SQLConnection> ref = new AtomicReference<>();
        context.runOnContext(v -> {
            client.getConnection(onSuccess(conn -> {
                ref.set(conn);
                latch.countDown();
            }));
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return ref.get();
    }

}