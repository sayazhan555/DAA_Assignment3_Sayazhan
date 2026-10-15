package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class Kahn {

    public static List<Integer> topologicalSort(Graph graph) {
        return topologicalSort(graph, new Metrics());
    }

    public static List<Integer> topologicalSort(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException(
                    "Topological sort needs a directed graph"
            );
        }

        int n = graph.size();
        int[] indegree = new int[n];

        for (Graph.Edge edge : graph.getEdges()) {
            indegree[edge.to]++;
        }

        ArrayDeque<Integer> queue = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }

        List<Integer> order = new ArrayList<>();

        while (!queue.isEmpty()) {
            int v = queue.poll();
            order.add(v);

            for (Graph.Edge edge : graph.neighbors(v)) {
                metrics.edgeScans++;
                indegree[edge.to]--;

                if (indegree[edge.to] == 0) {
                    queue.add(edge.to);
                }
            }
        }

        if (order.size() != n) {
            throw new IllegalStateException("Graph contains a cycle");
        }

        return order;
    }



}
