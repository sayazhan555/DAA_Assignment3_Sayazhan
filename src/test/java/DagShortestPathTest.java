import algorithms.DagShortestPath;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import metrics.Metrics;


import java.util.Random;
import java.util.Arrays;



public class DagShortestPathTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 2);
        graph.addEdge(2, 1, 1);
        graph.addEdge(1, 3, 3);

        DagShortestPath.Result result = DagShortestPath.run(graph);

        assertArrayEquals(new long[]{0, 3, 2, 6}, result.dist);
        assertEquals(List.of(0, 2, 1, 3), result.getPath(3));
    }

    @Test
    void testNegativeWeights() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, -5);
        graph.addEdge(1, 2, 2);
        graph.addEdge(0, 2, 10);

        DagShortestPath.Result result = DagShortestPath.run(graph);

        assertArrayEquals(new long[]{0, -5, -3}, result.dist);
        assertEquals(List.of(0, 1, 2), result.getPath(2));
    }

    @Test
    void testMultipleSources() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 2, 5);
        graph.addEdge(1, 2, 2);
        graph.addEdge(2, 3, 1);

        DagShortestPath.Result result = DagShortestPath.run(graph);

        assertArrayEquals(new long[]{0, 0, 2, 3}, result.dist);
        assertEquals(List.of(1, 2, 3), result.getPath(3));
    }

    @Test
    void testCycleRejected() {
        Graph graph = new Graph(2, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);

        assertThrows(IllegalStateException.class,
                () -> DagShortestPath.run(graph));
    }

    @Test
    void testIsolatedVertices() {
        Graph graph = new Graph(3, true);

        DagShortestPath.Result result = DagShortestPath.run(graph);

        assertArrayEquals(new long[]{0, 0, 0}, result.dist);
        assertEquals(List.of(2), result.getPath(2));
    }


    @Test
    void testMetrics() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 2);
        graph.addEdge(2, 1, 1);
        graph.addEdge(1, 3, 3);

        Metrics metrics = new Metrics();

        DagShortestPath.Result result =
                DagShortestPath.run(graph, metrics);

        assertArrayEquals(new long[]{0, 3, 2, 6}, result.dist);
        assertEquals(4, metrics.edgeScans);
        assertEquals(0, metrics.dsuOps);
    }


    private long[] bellmanFord(Graph graph) {
        int n = graph.size();
        int[] indegree = new int[n];

        for (Graph.Edge edge : graph.getEdges()) {
            indegree[edge.to]++;
        }

        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE);

        // All vertices with indegree 0 are sources
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        for (int i = 0; i < n - 1; i++) {
            boolean changed = false;

            for (Graph.Edge edge : graph.getEdges()) {
                if (dist[edge.from] == Long.MAX_VALUE) {
                    continue;
                }

                long newDist = dist[edge.from] + edge.weight;

                if (newDist < dist[edge.to]) {
                    dist[edge.to] = newDist;
                    changed = true;
                }
            }

            if (!changed) {
                break;
            }
        }

        return dist;
    }


    @Test
    void testAgainstBellmanFord() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(30);
            Graph graph = new Graph(n, true);

            // Edges only go from smaller to larger vertices
            for (int i = 0; i < n * 4; i++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);

                if (a == b) {
                    continue;
                }

                if (a > b) {
                    int temp = a;
                    a = b;
                    b = temp;
                }

                int weight = random.nextInt(41) - 20;
                graph.addEdge(a, b, weight);
            }

            long[] expected = bellmanFord(graph);
            long[] actual = DagShortestPath.run(graph).dist;

            assertArrayEquals(expected, actual,
                    "Wrong distances in random test " + test);
        }
    }


}
