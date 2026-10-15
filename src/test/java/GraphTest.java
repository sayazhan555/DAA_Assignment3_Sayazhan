import graph.Graph;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    void testConnectedGraph() throws FileNotFoundException {
        Graph g = Graph.readFromFile("data/connected.txt");

        assertEquals(4, g.size());
        assertEquals(5, g.getEdges().size());
        assertFalse(g.isDirected());
    }

    @Test
    void testDisconnectedGraph() throws FileNotFoundException {
        Graph g = Graph.readFromFile("data/disconnected.txt");

        assertEquals(5, g.size());
        assertEquals(2, g.getEdges().size());
        assertTrue(g.neighbors(4).isEmpty());
    }

    @Test
    void testCyclicGraph() throws FileNotFoundException {
        Graph g = Graph.readFromFile("data/cyclic.txt");

        assertEquals(4, g.size());
        assertEquals(4, g.getEdges().size());
        assertTrue(g.isDirected());
    }

    @Test
    void testSelfLoopAndParallelEdges() {
        Graph g = new Graph(3, false);

        g.addEdge(0, 0, 5);
        g.addEdge(0, 1, 3);
        g.addEdge(0, 1, 7);

        assertEquals(3, g.getEdges().size());
        assertEquals(2, g.neighbors(1).size());
    }

    @Test
    void testInvalidVertex() {
        Graph g = new Graph(3, true);

        assertThrows(IllegalArgumentException.class,
                () -> g.addEdge(0, 5, 10));
    }
}

