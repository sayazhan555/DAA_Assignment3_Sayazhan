import algorithms.Kruskal;
import algorithms.Prim;
import algorithms.MSTResult;
import graph.Graph;

import org.junit.jupiter.api.Test;
import java.io.FileNotFoundException;
import java.util.Random;

import metrics.Metrics;

import static org.junit.jupiter.api.Assertions.*;

public class MSTTest {

    @Test
    void testConnectedGraph() throws FileNotFoundException {
        Graph graph = Graph.readFromFile("data/connected.txt");

        MSTResult kruskal = Kruskal.run(graph);
        MSTResult prim = Prim.run(graph);

        assertEquals(10, kruskal.totalWeight);
        assertEquals(kruskal.totalWeight, prim.totalWeight);

        assertEquals(3, kruskal.edgeCount);
        assertEquals(3, prim.edgeCount);
        assertEquals(1, kruskal.components);
        assertEquals(1, prim.components);
    }

    @Test
    void testDisconnectedGraph() throws FileNotFoundException {
        Graph graph = Graph.readFromFile("data/disconnected.txt");

        MSTResult kruskal = Kruskal.run(graph);
        MSTResult prim = Prim.run(graph);

        assertEquals(7, kruskal.totalWeight);
        assertEquals(kruskal.totalWeight, prim.totalWeight);

        assertEquals(2, kruskal.edgeCount);
        assertEquals(2, prim.edgeCount);

        assertEquals(3, kruskal.components);
        assertEquals(3, prim.components);
    }

    @Test
    void testSelfLoopAndParallelEdges() {
        Graph graph = new Graph(3, false);

        graph.addEdge(0, 0, 1);
        graph.addEdge(0, 1, 10);
        graph.addEdge(0, 1, 3);
        graph.addEdge(1, 2, 5);

        MSTResult kruskal = Kruskal.run(graph);
        MSTResult prim = Prim.run(graph);

        assertEquals(8, kruskal.totalWeight);
        assertEquals(kruskal.totalWeight, prim.totalWeight);
        assertEquals(2, kruskal.edgeCount);
        assertEquals(2, prim.edgeCount);
    }

    @Test
    void testRandomGraphs() {
        Random random = new Random(42);

        for (int test = 0; test < 100; test++) {
            int n = 2 + random.nextInt(30);
            Graph graph = new Graph(n, false);

            // First create a connected graph
            for (int i = 1; i < n; i++) {
                int parent = random.nextInt(i);
                int weight = 1 + random.nextInt(100);

                graph.addEdge(parent, i, weight);
            }

            // Add some extra edges
            for (int i = 0; i < n * 3; i++) {
                int a = random.nextInt(n);
                int b = random.nextInt(n);
                int weight = 1 + random.nextInt(100);

                graph.addEdge(a, b, weight);
            }

            MSTResult kruskal = Kruskal.run(graph);
            MSTResult prim = Prim.run(graph);

            assertEquals(kruskal.totalWeight, prim.totalWeight);
            assertEquals(n - 1, kruskal.edgeCount);
            assertEquals(n - 1, prim.edgeCount);
            assertEquals(1, kruskal.components);
            assertEquals(1, prim.components);
        }
    }

    @Test
    void testDirectedGraphRejected() {
        Graph graph = new Graph(3, true);

        assertThrows(IllegalArgumentException.class,
                () -> Kruskal.run(graph));

        assertThrows(IllegalArgumentException.class,
                () -> Prim.run(graph));
    }


    @Test
    void testMetrics() throws FileNotFoundException {
        Graph graph = Graph.readFromFile("data/connected.txt");

        Metrics kruskalMetrics = new Metrics();
        Metrics primMetrics = new Metrics();

        MSTResult kruskal = Kruskal.run(graph, kruskalMetrics);
        MSTResult prim = Prim.run(graph, primMetrics);

        assertEquals(kruskal.totalWeight, prim.totalWeight);

        assertTrue(kruskalMetrics.edgeScans > 0);
        assertTrue(kruskalMetrics.comparisons > 0);
        assertTrue(kruskalMetrics.dsuOps > 0);

        assertTrue(primMetrics.edgeScans > 0);
        assertTrue(primMetrics.comparisons > 0);
        assertEquals(0, primMetrics.dsuOps);
    }


}
