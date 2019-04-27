package com.example.demo;

import io.vertx.core.json.JsonArray;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Undirected graph
 */
public class UndirectedGraph {
    private final Map<Integer, Set<Integer>> verticles = new HashMap<>();

    public void addEdge(int source, int target) {
        if (source < 0 || target < 0) {
            throw new IllegalArgumentException("vertex id must be >=0");
        }
        verticles.computeIfAbsent(source, key -> new HashSet<>()).add(target);
        verticles.computeIfAbsent(target, key -> new HashSet<>()).add(source);
    }

    public int size() {
        return verticles.size();
    }

    public List<JsonArray> toJson() {
        return verticles.keySet().stream()
                .flatMap(v -> verticles.get(v).stream().map(u -> new JsonArray().add(Math.min(v, u)).add(Math.max(v, u))))
                .distinct()
                .collect(Collectors.toList());
    }

    public static UndirectedGraph fromJson(List<JsonArray> list) {
        UndirectedGraph graph = new UndirectedGraph();
        list.forEach(a -> graph.addEdge(a.getInteger(0), a.getInteger(1)));
        return graph;
    }

    /**
     * Check graph for cycles using DFS algorithm
     *
     * @return true if there is at least one cycle
     */
    public boolean hasCycles() {
        Set<Integer> visited = new HashSet<>();
        for (Integer v : verticles.keySet()) {
            if (!visited.contains(v)) {
                if (dfs(v, -1, visited)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean dfs(Integer v, int parent, Set<Integer> visited) {
        visited.add(v);
        for (int u : verticles.get(v)) {
            if (!visited.contains(u)) {
                if (dfs(u, v, visited)) {
                    return true;
                }
            } else if (u != parent) {
                return true;
            }
        }
        return false;
    }
}
