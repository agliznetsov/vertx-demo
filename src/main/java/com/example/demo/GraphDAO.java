package com.example.demo;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLRowStream;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class GraphDAO {
    final SQLConnection connection;

    public GraphDAO(SQLConnection connection) {
        this.connection = connection;
    }

    public void insertData(List<JsonArray> data, Handler<AsyncResult<List<Integer>>> resultHandler) {
        connection.batchWithParams("insert into edges values(?, ?)", data, resultHandler);
    }

    public void queryData(Handler<AsyncResult<UndirectedGraph>> resultHandler) {
        connection.query("select source, target from edges", res -> {
            List<JsonArray> rows = res.result().getResults();
            log.info("Rows fetched: {}", rows.size());
            UndirectedGraph graph = UndirectedGraph.fromJson(rows);
            Future<UndirectedGraph> future = Future.future();
            future.complete(graph);
            resultHandler.handle(future);
        });
    }

    public void streamData(Handler<AsyncResult<UndirectedGraph>> resultHandler) {
        UndirectedGraph graph = new UndirectedGraph();
        Future<UndirectedGraph> future = Future.future();
        connection.queryStream("select source, target from edges", res -> {
            if (res.succeeded()) {
                SQLRowStream rowStream = res.result();
                rowStream.handler(row -> {
                    graph.addEdge(row.getInteger(0), row.getInteger(1));
                }).endHandler(e -> {
                    future.complete(graph);
                    resultHandler.handle(future);
                });
            } else {
                future.fail(res.cause());
                resultHandler.handle(future);
            }
        });
    }
}
