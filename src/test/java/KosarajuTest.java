import algorithms.Kosaraju;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.Random;

public class KosarajuTest {

    @Test
    void testCyclicGraph() throws FileNotFoundException {
        Graph graph = Graph.readFromFile("data/cyclic.txt");

        assertEquals(2, Kosaraju.countSCC(graph));
    }

    @Test
    void testSimpleCycle() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        assertEquals(1, Kosaraju.countSCC(graph));
    }

    @Test
    void testIsolatedVertices() {
        Graph graph = new Graph(5, true);

        assertEquals(5, Kosaraju.countSCC(graph));
    }

    @Test
    void testLargePath() {
        int n = 100_000;
        Graph graph = new Graph(n, true);

        for (int i = 0; i < n - 1; i++) {
            graph.addEdge(i, i + 1, 1);
        }

        assertEquals(n, Kosaraju.countSCC(graph));
    }


    @Test
    void testMetrics() throws FileNotFoundException {
        Graph graph = Graph.readFromFile("data/cyclic.txt");
        Metrics metrics = new Metrics();

        int components = Kosaraju.countSCC(graph, metrics);

        assertEquals(2, components);
        assertEquals(2L * graph.getEdges().size(), metrics.edgeScans);
        assertTrue(metrics.maxDepth > 0);
    }


    private boolean[] reachable(Graph graph, int start) {
        boolean[] visited = new boolean[graph.size()];
        ArrayDeque<Integer> queue = new ArrayDeque<>();

        visited[start] = true;
        queue.add(start);

        while (!queue.isEmpty()) {
            int v = queue.poll();

            for (Graph.Edge edge : graph.neighbors(v)) {
                if (!visited[edge.to]) {
                    visited[edge.to] = true;
                    queue.add(edge.to);
                }
            }
        }

        return visited;
    }

    @Test
    void testAgainstBruteForce() {
        Random random = new Random(42);

        for (int test = 0; test < 30; test++) {
            int n = 2 + random.nextInt(199);
            Graph graph = new Graph(n, true);

            for (int i = 0; i < n * 3; i++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);
                graph.addEdge(a, b, 1);
            }

            boolean[][] reach = new boolean[n][];

            for (int i = 0; i < n; i++) {
                reach[i] = reachable(graph, i);
            }

            int expected = 0;
            boolean[] assigned = new boolean[n];

            for (int i = 0; i < n; i++) {
                if (assigned[i]) {
                    continue;
                }

                expected++;

                for (int j = 0; j < n; j++) {
                    if (reach[i][j] && reach[j][i]) {
                        assigned[j] = true;
                    }
                }
            }

            assertEquals(expected, Kosaraju.countSCC(graph),
                    "Wrong SCC count in random test " + test);
        }
    }



}
