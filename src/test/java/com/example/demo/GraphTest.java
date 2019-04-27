package com.example.demo;

import io.vertx.core.json.JsonArray;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GraphTest {

    @Test
    public void testJson() {
        UndirectedGraph graph = GraphUtils.createBinaryTree(2);
        List<JsonArray> list = graph.toJson();
        UndirectedGraph graph2 = UndirectedGraph.fromJson(list);
        List<JsonArray> list2 = graph2.toJson();
        compareJson(list, list2);
    }

    private void compareJson(List<JsonArray> list, List<JsonArray> list2) {
        assertEquals(list.size(), list2.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(list.get(0).encode(), list2.get(0).encode());
        }
    }

    @Test
    public void testCycleGraph() {
        UndirectedGraph graph = GraphUtils.createSimpleGraph(3, true);
        assertEquals(3, graph.size());
        assertTrue(graph.hasCycles());
    }

    @Test
    public void testNonCycleGraph() {
        UndirectedGraph graph = GraphUtils.createSimpleGraph(3, false);
        assertEquals(3, graph.size());
        assertFalse(graph.hasCycles());
    }

    @Test
    public void testBinaryTree() {
        UndirectedGraph graph = GraphUtils.createBinaryTree(2);
        assertFalse(graph.hasCycles());
        graph.addEdge(7, 1);
        assertTrue(graph.hasCycles());
    }

}
