package graph;

import java.util.ArrayList;
import java.util.List;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Graph {

    public static class Edge {
        public int from;
        public int to;
        public int weight;

        public Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    private final int n;
    private final boolean directed;
    private final List<List<Edge>> adj;
    private final List<Edge> edges;

    public Graph(int n, boolean directed) {
        if (n < 0) {
            throw new IllegalArgumentException("Invalid number of vertices");
        }

        this.n = n;
        this.directed = directed;
        this.adj = new ArrayList<>();
        this.edges = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IllegalArgumentException("Invalid vertex");
        }

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adj.get(from).add(edge);

        if (!directed) {
            adj.get(to).add(new Edge(to, from, weight));
        }
    }

    public int size() {
        return n;
    }

    public boolean isDirected() {
        return directed;
    }

    public List<Edge> neighbors(int vertex) {
        return adj.get(vertex);
    }

    public List<Edge> getEdges() {
        return edges;
    }


    public static Graph readFromFile(String fileName)
            throws FileNotFoundException {

        try (Scanner scanner = new Scanner(new File(fileName))) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            String type = scanner.next();

            if (!type.equals("directed") && !type.equals("undirected")) {
                throw new IllegalArgumentException("Invalid graph type");
            }

            if (m < 0) {
                throw new IllegalArgumentException("Invalid edge count");
            }

            Graph graph = new Graph(n, type.equals("directed"));

            for (int i = 0; i < m; i++) {
                int u = scanner.nextInt();
                int v = scanner.nextInt();
                int w = scanner.nextInt();

                graph.addEdge(u, v, w);
            }

            if (scanner.hasNext()) {
                throw new IllegalArgumentException("Extra data in graph file");
            }

            return graph;
        }
    }

}
