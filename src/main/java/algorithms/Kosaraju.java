package algorithms;

import graph.Graph;
import metrics.Metrics;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;


public class Kosaraju {

    public static int countSCC(Graph graph) {
        return countSCC(graph, new Metrics());
    }

    public static int countSCC(Graph graph, Metrics metrics) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("SCC needs a directed graph");
        }

        int n = graph.size();
        boolean[] visited = new boolean[n];
        List<Integer> order = new ArrayList<>();

        // First DFS pass
        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }

            ArrayDeque<int[]> stack = new ArrayDeque<>();
            stack.push(new int[]{start, 0});
            visited[start] = true;
            metrics.updateDepth(stack.size());

            while (!stack.isEmpty()) {
                int[] current = stack.peek();
                int v = current[0];
                int index = current[1];

                if (index < graph.neighbors(v).size()) {
                    Graph.Edge edge = graph.neighbors(v).get(index);
                    current[1]++;
                    metrics.edgeScans++;

                    if (!visited[edge.to]) {
                        visited[edge.to] = true;
                        stack.push(new int[]{edge.to, 0});
                        metrics.updateDepth(stack.size());
                    }
                } else {
                    order.add(v);
                    stack.pop();
                }
            }
        }

        // Reverse graph
        List<List<Integer>> reverse = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            reverse.add(new ArrayList<>());
        }

        for (Graph.Edge edge : graph.getEdges()) {
            reverse.get(edge.to).add(edge.from);
        }

        // Second DFS pass
        boolean[] seen = new boolean[n];
        int components = 0;

        for (int i = order.size() - 1; i >= 0; i--) {
            int start = order.get(i);

            if (seen[start]) {
                continue;
            }

            components++;

            ArrayDeque<Integer> stack = new ArrayDeque<>();
            stack.push(start);
            seen[start] = true;
            metrics.updateDepth(stack.size());

            while (!stack.isEmpty()) {
                int v = stack.pop();

                for (int next : reverse.get(v)) {
                    metrics.edgeScans++;

                    if (!seen[next]) {
                        seen[next] = true;
                        stack.push(next);
                        metrics.updateDepth(stack.size());
                    }
                }
            }
        }

        return components;
    }



}
