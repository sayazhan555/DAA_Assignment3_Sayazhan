package algorithms;

public class MSTResult {
    public long totalWeight;
    public int edgeCount;
    public int components;

    public MSTResult(long totalWeight, int edgeCount, int components) {
        this.totalWeight = totalWeight;
        this.edgeCount = edgeCount;
        this.components = components;
    }
}
