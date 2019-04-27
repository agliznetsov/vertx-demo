package com.example.demo;

public class GraphUtils {
    private static int edgeId;

    public static UndirectedGraph createSimpleGraph(int size, boolean closed) {
        edgeId = 1;
        UndirectedGraph graph = new UndirectedGraph();
        int first = edgeId;
        for (int i = 0; i < size - 1; i++) {
            graph.addEdge(edgeId, ++edgeId);
        }
        if (closed) {
            graph.addEdge(edgeId, first);
        }
        return graph;
    }

    public static UndirectedGraph createBinaryTree(int deep) {
        edgeId = 1;
        UndirectedGraph graph = new UndirectedGraph();
        addNode(graph, edgeId, 1, deep);
        return graph;
    }

    private static void addNode(UndirectedGraph graph, int root, int level, int deep) {
        int left = ++edgeId;
        int right = ++edgeId;
        graph.addEdge(root, left);
        graph.addEdge(root, right);
        if (level < deep) {
            addNode(graph, left, level + 1, deep);
            addNode(graph, right, level + 1, deep);
        }
    }

}
