package benchmark;

import graph.Graph;
import java.util.Random;

public class GraphGenerator {

    private static int[] shuffle(int n, Random random) {
        int[] arr = new int[n];

        for (int i = 0; i < n; i++) {
            arr[i] = i;
        }

        for (int i = n - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);

            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }

        return arr;
    }

    public static Graph undirected(int n, int m) {
        Random random = new Random(42);
        Graph graph = new Graph(n, false);

        if (n < 1 || m < n - 1) {
            throw new IllegalArgumentException("Invalid graph size");
        }

        int[] perm = shuffle(n, random);

        for (int i = 1; i < n; i++) {
            int a = perm[random.nextInt(i)];
            int b = perm[i];
            int weight = 1 + random.nextInt(1000);

            graph.addEdge(a, b, weight);
        }

        for (int i = n - 1; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }

    public static Graph directed(int n, int m) {
        Random random = new Random(42);
        Graph graph = new Graph(n, true);

        for (int i = 0; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }

    public static Graph dag(int n, int m) {
        Random random = new Random(42);
        Graph graph = new Graph(n, true);

        int[] rank = shuffle(n, random);

        for (int i = 0; i < m; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            while (b == a) {
                b = random.nextInt(n);
            }

            if (rank[a] > rank[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            int weight = 1 + random.nextInt(1000);
            graph.addEdge(a, b, weight);
        }

        return graph;
    }
}
