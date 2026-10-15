import algorithms.DSU;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DSUTest {

    @Test
    void testInitialComponents() {
        DSU dsu = new DSU(5);

        assertEquals(5, dsu.components());

        for (int i = 0; i < 5; i++) {
            assertEquals(i, dsu.find(i));
        }
    }

    @Test
    void testUnion() {
        DSU dsu = new DSU(5);

        assertTrue(dsu.union(0, 1));
        assertTrue(dsu.union(1, 2));

        assertEquals(dsu.find(0), dsu.find(2));
        assertEquals(3, dsu.components());
    }

    @Test
    void testSameSet() {
        DSU dsu = new DSU(4);

        assertTrue(dsu.union(0, 1));
        assertFalse(dsu.union(0, 1));

        assertEquals(3, dsu.components());
        assertEquals(1, dsu.getUnionCalls());
    }

    @Test
    void testMakeSet() {
        DSU dsu = new DSU(5);

        dsu.union(0, 1);
        dsu.makeSet(3);

        assertEquals(3, dsu.components());
        assertEquals(0, dsu.getUnionCalls());
        assertEquals(0, dsu.getFindCalls());
    }

    @Test
    void testInvalidSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new DSU(-1));
    }

    @Test
    void testRandomUnions() {
        int n = 100;
        DSU dsu = new DSU(n);
        int[] reference = new int[n];

        for (int i = 0; i < n; i++) {
            reference[i] = i;
        }

        Random random = new Random(42);

        for (int i = 0; i < 500; i++) {
            int a = random.nextInt(n);
            int b = random.nextInt(n);

            int oldGroup = reference[b];
            int newGroup = reference[a];

            dsu.union(a, b);

            for (int j = 0; j < n; j++) {
                if (reference[j] == oldGroup) {
                    reference[j] = newGroup;
                }
            }
        }

        java.util.Set<Integer> groups = new java.util.HashSet<>();

        for (int i = 0; i < n; i++) {
            groups.add(reference[i]);
        }

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {
                assertEquals(
                        reference[i] == reference[j],
                        dsu.find(i) == dsu.find(j)
                );
            }
        }

        assertEquals(groups.size(), dsu.components());
    }
}
