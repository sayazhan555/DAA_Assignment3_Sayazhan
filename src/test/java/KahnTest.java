import algorithms.Kahn;
import graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import metrics.Metrics;

public class KahnTest {

    @Test
    void testSimpleDAG() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        List<Integer> order = Kahn.topologicalSort(graph);

        assertEquals(4, order.size());

        int[] pos = new int[4];

        for (int i = 0; i < order.size(); i++) {
            pos[order.get(i)] = i;
        }

        for (Graph.Edge edge : graph.getEdges()) {
            assertTrue(pos[edge.from] < pos[edge.to]);
        }
    }

    @Test
    void testCycleDetected() {
        Graph graph = new Graph(3, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);

        assertThrows(IllegalStateException.class,
                () -> Kahn.topologicalSort(graph));
    }

    @Test
    void testDisconnectedDAG() {
        Graph graph = new Graph(5, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);

        List<Integer> order = Kahn.topologicalSort(graph);

        assertEquals(5, order.size());
        assertTrue(order.contains(4));
    }

    @Test
    void testSelfLoop() {
        Graph graph = new Graph(2, true);
        graph.addEdge(0, 0, 1);

        assertThrows(IllegalStateException.class,
                () -> Kahn.topologicalSort(graph));
    }


    @Test
    void testMetrics() {
        Graph graph = new Graph(4, true);

        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);

        Metrics metrics = new Metrics();

        List<Integer> order = Kahn.topologicalSort(graph, metrics);

        assertEquals(4, order.size());
        assertEquals(4, metrics.edgeScans);
        assertEquals(0, metrics.dsuOps);
        assertEquals(0, metrics.comparisons);
    }


}
