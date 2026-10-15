import algorithms.Kruskal;
import algorithms.Prim;
import algorithms.Kosaraju;
import algorithms.Kahn;
import algorithms.DagShortestPath;
import benchmark.GraphGenerator;
import graph.Graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphGeneratorTest {

    @Test
    void testReference1000Sparse() {
        int n = 1000;
        int m = 4000;

        Graph undirected = GraphGenerator.undirected(n, m);
        Graph directed = GraphGenerator.directed(n, m);
        Graph dag = GraphGenerator.dag(n, m);

        assertEquals(m, undirected.getEdges().size());
        assertEquals(m, directed.getEdges().size());
        assertEquals(m, dag.getEdges().size());

        long kruskalWeight = Kruskal.run(undirected).totalWeight;
        long primWeight = Prim.run(undirected).totalWeight;

        assertEquals(kruskalWeight, primWeight);
        assertEquals(147970L, kruskalWeight);

        assertEquals(27, Kosaraju.countSCC(directed));
        assertEquals(n, Kahn.topologicalSort(dag).size());

        DagShortestPath.Result result = DagShortestPath.run(dag);

        long sum = 0;
        for (long distance : result.dist) {
            sum += distance;
        }

        assertEquals(396274L, sum);
    }


    @Test
    void testAllReferenceValues() {
        int[] sizes = {
                1000, 3162, 10000, 31623, 100000
        };

        long[][] mstExpected = {
                {147970, 30363},
                {468911, 97589},
                {1486139, 309550},
                {4715515, 968517},
                {15012094, 3056681}
        };

        int[][] sccExpected = {
                {27, 1},
                {137, 1},
                {373, 1},
                {1188, 1},
                {3923, 1}
        };

        long[][] dagExpected = {
                {396274, 252411},
                {1271911, 723157},
                {3941828, 2381342},
                {12551891, 7376612},
                {40096539, 23512087}
        };

        for (int i = 0; i < sizes.length; i++) {
            int n = sizes[i];

            for (int density = 0; density < 2; density++) {
                int m = (density == 0) ? 4 * n : 20 * n;

                Graph undirected = GraphGenerator.undirected(n, m);

                long kruskal = Kruskal.run(undirected).totalWeight;
                long prim = Prim.run(undirected).totalWeight;

                assertEquals(mstExpected[i][density], kruskal,
                        "Wrong MST for n=" + n + ", m=" + m);
                assertEquals(kruskal, prim,
                        "Kruskal and Prim disagree");

                Graph directed = GraphGenerator.directed(n, m);

                assertEquals(sccExpected[i][density],
                        Kosaraju.countSCC(directed),
                        "Wrong SCC for n=" + n + ", m=" + m);

                Graph dag = GraphGenerator.dag(n, m);

                assertEquals(n, Kahn.topologicalSort(dag).size());

                DagShortestPath.Result result =
                        DagShortestPath.run(dag);

                long sum = 0;

                for (long distance : result.dist) {
                    sum += distance;
                }

                assertEquals(dagExpected[i][density], sum,
                        "Wrong DAG distances for n=" + n + ", m=" + m);

                System.out.println(
                        "Passed: n=" + n + ", m=" + m
                );
            }
        }
    }


}
