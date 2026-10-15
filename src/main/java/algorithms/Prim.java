package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Prim {

    public static MSTResult run(Graph graph) {
        return run(graph, new Metrics());
    }

    public static MSTResult run(Graph graph, Metrics metrics) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }

        int n = graph.size();
        boolean[] visited = new boolean[n];

        PriorityQueue<Graph.Edge> pq =
                new PriorityQueue<>((a, b) -> {
                    metrics.comparisons++;
                    return Integer.compare(a.weight, b.weight);
                });

        long totalWeight = 0;
        int edgeCount = 0;
        int components = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            components++;
            pq.add(new Graph.Edge(-1, start, 0));

            while (!pq.isEmpty()) {
                Graph.Edge edge = pq.poll();
                int v = edge.to;

                if (visited[v]) {
                    continue;
                }

                visited[v] = true;

                if (edge.from != -1) {
                    totalWeight += edge.weight;
                    edgeCount++;
                }

                for (Graph.Edge next : graph.neighbors(v)) {
                    metrics.edgeScans++;

                    if (!visited[next.to]) {
                        pq.add(next);
                    }
                }
            }
        }

        return new MSTResult(totalWeight, edgeCount, components);
    }
}
