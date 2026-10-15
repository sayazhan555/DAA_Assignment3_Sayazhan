package benchmark;

import algorithms.*;
import graph.Graph;
import metrics.Metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class Benchmark {

    private static final int[] SIZES = {
            1000, 3162, 10000, 31623, 100000
    };

    private static final long[][] MST_EXPECTED = {
            {147970, 30363},
            {468911, 97589},
            {1486139, 309550},
            {4715515, 968517},
            {15012094, 3056681}
    };

    private static final int[][] SCC_EXPECTED = {
            {27, 1},
            {137, 1},
            {373, 1},
            {1188, 1},
            {3923, 1}
    };

    private static final long[][] DAG_EXPECTED = {
            {396274, 252411},
            {1271911, 723157},
            {3941828, 2381342},
            {12551891, 7376612},
            {40096539, 23512087}
    };

    private static class RunResult {
        long result;
        Metrics metrics;

        RunResult(long result, Metrics metrics) {
            this.result = result;
            this.metrics = metrics;
        }
    }

    private static RunResult runAlgorithm(String name, Graph graph) {
        Metrics metrics = new Metrics();
        long result;

        switch (name) {
            case "Kruskal":
                result = Kruskal.run(graph, metrics).totalWeight;
                break;

            case "Prim":
                result = Prim.run(graph, metrics).totalWeight;
                break;

            case "Kosaraju":
                result = Kosaraju.countSCC(graph, metrics);
                break;

            case "Kahn":
                List<Integer> order = Kahn.topologicalSort(graph, metrics);
                validateOrder(graph, order);
                result = order.size();
                break;

            case "DagShortestPath":
                DagShortestPath.Result paths =
                        DagShortestPath.run(graph, metrics);

                long sum = 0;
                for (long distance : paths.dist) {
                    if (distance == Long.MAX_VALUE) {
                        throw new IllegalStateException("Unreachable vertex");
                    }
                    sum += distance;
                }

                result = sum;
                break;

            default:
                throw new IllegalArgumentException("Unknown algorithm");
        }

        return new RunResult(result, metrics);
    }

    private static void validateOrder(Graph graph, List<Integer> order) {
        int n = graph.size();

        if (order.size() != n) {
            throw new IllegalStateException("Invalid topological order size");
        }

        int[] pos = new int[n];
        boolean[] seen = new boolean[n];

        for (int i = 0; i < n; i++) {
            int v = order.get(i);

            if (v < 0 || v >= n || seen[v]) {
                throw new IllegalStateException("Invalid vertex in order");
            }

            seen[v] = true;
            pos[v] = i;
        }

        for (Graph.Edge edge : graph.getEdges()) {
            if (pos[edge.from] >= pos[edge.to]) {
                throw new IllegalStateException("Invalid topological order");
            }
        }
    }

    private static void checkResult(String name, long actual,
                                    long expected, int n, int m) {
        if (actual != expected) {
            throw new IllegalStateException(
                    name + " failed for n=" + n + ", m=" + m
                            + ". Expected " + expected + ", got " + actual
            );
        }
    }

    private static void measure(String name, String graphType,
                                Graph graph, long expected,
                                PrintWriter writer) {

        int n = graph.size();
        int m = graph.getEdges().size();

        double[] times = new double[5];
        RunResult first = null;

        for (int i = 0; i < 5; i++) {
            long start = System.nanoTime();

            RunResult current = runAlgorithm(name, graph);

            long end = System.nanoTime();
            times[i] = (end - start) / 1_000_000.0;

            checkResult(name, current.result, expected, n, m);

            if (first == null) {
                first = current;
            } else {
                Metrics a = first.metrics;
                Metrics b = current.metrics;

                if (a.dsuOps != b.dsuOps ||
                        a.edgeScans != b.edgeScans ||
                        a.comparisons != b.comparisons ||
                        a.maxDepth != b.maxDepth) {
                    throw new IllegalStateException(
                            "Metrics changed between runs: " + name
                    );
                }
            }
        }

        Arrays.sort(times);
        double median = times[2];

        Metrics metrics = first.metrics;

        writer.printf(java.util.Locale.US,
                "%s,%s,%d,%d,%.4f,%d,%d,%d,%d,%d%n",
                name, graphType, n, m, median,
                metrics.dsuOps, metrics.edgeScans,
                metrics.comparisons, metrics.maxDepth,
                first.result);
    }

    public static void main(String[] args) throws IOException {
        File folder = new File("results");
        if (!folder.exists() && !folder.mkdirs()) {
            throw new IOException("Cannot create results folder");
        }

        try (PrintWriter writer =
                     new PrintWriter(new FileWriter("results/results.csv"))) {

            writer.println(
                    "algorithm,graph,n,m,time_ms,dsu_ops,"
                            + "edge_scans,comparisons,max_depth,result"
            );

            for (int i = 0; i < SIZES.length; i++) {
                int n = SIZES[i];

                for (int density = 0; density < 2; density++) {
                    int m = (density == 0) ? 4 * n : 20 * n;
                    String label = density == 0 ? "sparse" : "dense";

                    System.out.println("Running n=" + n + ", m=" + m);

                    Graph undirected = GraphGenerator.undirected(n, m);

                    measure("Kruskal", label, undirected,
                            MST_EXPECTED[i][density], writer);

                    measure("Prim", label, undirected,
                            MST_EXPECTED[i][density], writer);

                    Graph directed = GraphGenerator.directed(n, m);

                    measure("Kosaraju", label, directed,
                            SCC_EXPECTED[i][density], writer);

                    Graph dag = GraphGenerator.dag(n, m);

                    measure("Kahn", label, dag, n, writer);

                    measure("DagShortestPath", label, dag,
                            DAG_EXPECTED[i][density], writer);

                    writer.flush();
                    System.out.println("Passed n=" + n + ", m=" + m);
                }
            }
        }

        System.out.println("Results saved to results/results.csv");
    }
}
