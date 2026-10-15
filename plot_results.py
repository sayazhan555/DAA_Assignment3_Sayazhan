import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

data = pd.read_csv("results/results.csv")

output = Path("results/plots")
output.mkdir(parents=True, exist_ok=True)

# Plot 1: Operations vs n
plt.figure(figsize=(10, 6))

for (algorithm, graph), group in data.groupby(["algorithm", "graph"]):
    group = group.sort_values("n")

    if algorithm == "Kruskal":
        operations = group["comparisons"] + group["dsu_ops"]
    elif algorithm == "Prim":
        operations = group["comparisons"] + group["edge_scans"]
    else:
        operations = group["edge_scans"]

    plt.plot(group["n"], operations, marker="o",
             label=f"{algorithm} ({graph})")

plt.xscale("log")
plt.yscale("log")
plt.xlabel("Number of vertices (n)")
plt.ylabel("Counted operations")
plt.title("Operations vs Graph Size")
plt.legend(fontsize=8)
plt.grid(True)
plt.tight_layout()
plt.savefig(output / "operations_vs_n.png", dpi=200)
plt.close()

# Plot 2: Scans per edge
plt.figure(figsize=(10, 6))

for (algorithm, graph), group in data.groupby(["algorithm", "graph"]):
    group = group.sort_values("n")
    ratio = group["edge_scans"] / group["m"]

    plt.plot(group["n"], ratio, marker="o",
             label=f"{algorithm} ({graph})")

plt.xscale("log")
plt.xlabel("Number of vertices (n)")
plt.ylabel("edgeScans / m")
plt.title("Scans per Edge")
plt.legend(fontsize=8)
plt.grid(True)
plt.tight_layout()
plt.savefig(output / "scans_per_edge.png", dpi=200)
plt.close()

# Plot 3: Kruskal examined edges
kruskal = data[data["algorithm"] == "Kruskal"]

plt.figure(figsize=(9, 5))

for graph, group in kruskal.groupby("graph"):
    group = group.sort_values("n")
    ratio = group["edge_scans"] / group["m"]

    plt.plot(group["n"], ratio, marker="o", label=graph)

plt.xscale("log")
plt.xlabel("Number of vertices (n)")
plt.ylabel("edgeScans / m")
plt.title("Share of Edges Examined by Kruskal")
plt.legend()
plt.grid(True)
plt.tight_layout()
plt.savefig(output / "kruskal_edge_share.png", dpi=200)
plt.close()

print("Three plots saved to results/plots/")


import numpy as np

print("\nLog-log slopes:")

for (algorithm, graph), group in data.groupby(["algorithm", "graph"]):
    group = group.sort_values("n")

    x = np.log10(group["n"].to_numpy())

    if algorithm == "Kruskal":
        operations = (
            group["comparisons"] + group["dsu_ops"]
        )
    elif algorithm == "Prim":
        operations = (
            group["comparisons"] + group["edge_scans"]
        )
    else:
        operations = group["edge_scans"]

    y = np.log10(operations.to_numpy())

    slope = np.polyfit(x, y, 1)[0]

    print(f"{algorithm:17s} {graph:7s} slope = {slope:.3f}")


print("\nEdge scan slopes:")

for (algorithm, graph), group in data.groupby(["algorithm", "graph"]):
    group = group.sort_values("n")

    x = np.log10(group["n"].to_numpy())
    y = np.log10(group["edge_scans"].to_numpy())

    slope = np.polyfit(x, y, 1)[0]

    print(f"{algorithm:17s} {graph:7s} slope = {slope:.3f}")

