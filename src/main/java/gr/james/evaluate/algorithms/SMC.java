package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Partition;
import gr.james.evaluate.ds.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Matching coefficient implementation.
 */
public final class SMC {
    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #smc(Partition, Partition)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Simple Matching coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double smc(Result<T> a, Result<T> b) {
        if (a.isPartition() && b.isPartition()) {
            return smc(a.partition, b.partition);
        } else {
            return null;
        }
    }

    /**
     * Returns the Simple Matching coefficient of two partitions.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert smc(a, b) == smc(b, a);
     * </code></pre>
     *
     * @param a   one partition
     * @param b   the other partition
     * @param <T> the type of elements
     * @return the Simple Matching coefficient between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double smc(Partition<T> a, Partition<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        long common = 0;
        long all = 0;

        final List<T> vertices = new ArrayList<>(a.elements());
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                final boolean aHas = a.connected(vertices.get(i), vertices.get(j));
                final boolean bHas = b.connected(vertices.get(i), vertices.get(j));
                if (aHas && bHas) {
                    common++;
                }
                if (!aHas && !bHas) {
                    common++;
                }
                all++;
            }
        }

        return (double) common / (double) all;
    }
}
