package algorithms;

import graph.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import metrics.Metrics;

public class DagShortestPath {

    public static class Result {
        public long[] dist;
        public int[] parent;

        public Result(int n) {
            dist = new long[n];
            parent = new int[n];

            Arrays.fill(dist, Long.MAX_VALUE);
            Arrays.fill(parent, -1);
        }

        public List<Integer> getPath(int vertex) {
            if (dist[vertex] == Long.MAX_VALUE) {
                return Collections.emptyList();
            }

            List<Integer> path = new ArrayList<>();
            int current = vertex;

            while (current != -1) {
                path.add(current);
                current = parent[current];
            }

            Collections.reverse(path);
            return path;
        }
    }


    public static Result run(Graph graph) {
        return run(graph, new Metrics());
    }

    public static Result run(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("DAG must be directed");
        }


        int n = graph.size();
        int[] indegree = new int[n];

        for (Graph.Edge edge : graph.getEdges()) {
            indegree[edge.to]++;
        }

        Result result = new Result(n);

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                result.dist[i] = 0;
            }
        }

        List<Integer> order = Kahn.topologicalSort(graph);

        for (int v : order) {
            if (result.dist[v] == Long.MAX_VALUE) {
                continue;
            }


            for (Graph.Edge edge : graph.neighbors(v)) {
                metrics.edgeScans++;

                long newDist = result.dist[v] + edge.weight;

                if (newDist < result.dist[edge.to]) {
                    result.dist[edge.to] = newDist;
                    result.parent[edge.to] = v;
                }
            }
        }

        return result;
    }
}

