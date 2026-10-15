package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayList;
import java.util.List;

public class Kruskal {

    public static MSTResult run(Graph graph) {
        return run(graph, new Metrics());
    }

    public static MSTResult run(Graph graph, Metrics metrics) {
        if (graph.isDirected()) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }

        List<Graph.Edge> edges = new ArrayList<>(graph.getEdges());

        edges.sort((a, b) -> {
            metrics.comparisons++;
            return Integer.compare(a.weight, b.weight);
        });

        DSU dsu = new DSU(graph.size());

        long totalWeight = 0;
        int edgeCount = 0;

        for (Graph.Edge edge : edges) {
            metrics.edgeScans++;

            if (dsu.union(edge.from, edge.to)) {
                totalWeight += edge.weight;
                edgeCount++;
            }

            if (edgeCount == graph.size() - 1) {
                break;
            }
        }

        metrics.dsuOps = dsu.getFindCalls() + dsu.getUnionCalls();

        return new MSTResult(
                totalWeight,
                edgeCount,
                dsu.components()
        );
    }
}
