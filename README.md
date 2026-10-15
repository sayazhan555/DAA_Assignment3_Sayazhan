## DAA Assignment 3 — Graph Algorithms

**Student:** Sayazhan Kanat
**Group:** SE-2517

#### Project Description

In this assignment, I implemented graph algorithms using Java. The project supports directed and undirected weighted graphs using an adjacency list.

The goal was to implement the algorithms, test their correctness and compare their performance using operation counters.

#### Implemented Algorithms

- DSU with path compression and union by rank
- Kruskal's Minimum Spanning Tree
- Prim's Minimum Spanning Tree using PriorityQueue
- Kosaraju's SCC using iterative DFS
- Kahn's Topological Sorting
- DAG Shortest Paths using topological order

#### Project Structure

- `src/main/java/graph/` — Graph class
- `src/main/java/algorithms/` — Algorithm implementations
- `src/main/java/metrics/` — Operation counters
- `src/main/java/benchmark/` — Graph generator and benchmark
- `src/test/java/` — JUnit 5 tests
- `data/` — Small graph input files
- `results/results.csv` — Benchmark results
- `results/plots/` — Three PNG charts
- `plot_results.py` — Python script for plots
- `REPORT.md` — Report and complexity analysis

#### How to Build and Test

Requirements: Java 17 and Maven 3.

Open the project in IntelliJ IDEA.

1. Open the Maven panel on the right.
2. Expand Lifecycle.
3. Run `clean` and then `test`.

If Maven is installed in the terminal, use:

```bash
mvn clean test
```

The project passed 37 JUnit tests with 0 failures and 0 errors.

#### How to Run the Benchmark

Open `src/main/java/benchmark/Benchmark.java` in IntelliJ IDEA and run the `main()` method.

The benchmark generates graphs using `Random(42)` with five sizes and two densities. Each algorithm is executed five times, and the median execution time is recorded.

The program checks the reference values and saves 50 result rows to `results/results.csv`.

#### How to Generate Plots

Install Python dependencies:

```bash
python3 -m pip install pandas matplotlib
```

Run the script from the project root:

```bash
python3 plot_results.py
```

The script generates three plots in `results/plots/` and prints the log-log slopes.

#### Testing

I used JUnit 5 to check the algorithms.

Kruskal and Prim were compared on 100 random connected graphs. Kosaraju was compared with brute-force BFS, and DAG Shortest Paths was compared with Bellman-Ford.

I also tested Kosaraju on a graph with 100,000 vertices to check stack safety.

#### Results

All 10 graph configurations matched the reference values from the assignment. The benchmark produced 50 rows, and the operation counters were used to analyze algorithm complexity.

More detailed results and explanations are available in `REPORT.md`.

#### AI Use Acknowledgement

I used ChatGPT to help understand some parts of the assignment, get coding suggestions, and check my work. I tested the results using JUnit 5 and the reference values.