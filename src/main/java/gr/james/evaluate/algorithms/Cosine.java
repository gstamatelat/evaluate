package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Partition;
import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.ValueList;

import java.util.ArrayList;
import java.util.List;

/**
 * Cosine similarity implementation.
 */
public final class Cosine {
    private Cosine() {
    }

    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #cosine(ValueList, ValueList)}</li>
     * <li>{@link #cosine(Partition, Partition)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Cosine similarity between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double cosine(Result<T> a, Result<T> b) {
        if (a.isValueList() && b.isValueList()) {
            return cosine(a.valueList, b.valueList);
        } else if (a.isPartition() && b.isPartition()) {
            return cosine(a.partition, b.partition);
        } else {
            return null;
        }
    }

    /**
     * Returns the Cosine similarity of two value maps.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert cosine(a, b) == cosine(b, a);
     * </code></pre>
     *
     * @param a   one value map
     * @param b   the other value map
     * @param <T> the type of elements
     * @return the Cosine similarity between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double cosine(ValueList<T> a, ValueList<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        double numerator = 0;
        double denominatorA = 0;
        double denominatorB = 0;
        for (T t : a.elements()) {
            numerator += a.get(t) * b.get(t);
            denominatorA += Math.pow(a.get(t), 2);
            denominatorB += Math.pow(b.get(t), 2);
        }

        return numerator / (Math.sqrt(denominatorA) * Math.sqrt(denominatorB));
    }

    /**
     * Returns the Cosine similarity of two partitions.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert cosine(a, b) == cosine(b, a);
     * </code></pre>
     *
     * @param a   one partition
     * @param b   the other partition
     * @param <T> the type of elements
     * @return the Cosine similarity between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double cosine(Partition<T> a, Partition<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        long intersection = 0;
        long cardinality1 = 0;
        long cardinality2 = 0;

        final List<T> vertices = new ArrayList<>(a.elements());
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                final boolean aHas = a.connected(vertices.get(i), vertices.get(j));
                final boolean bHas = b.connected(vertices.get(i), vertices.get(j));
                if (aHas && bHas) {
                    intersection++;
                }
                if (aHas) {
                    cardinality1++;
                }
                if (bHas) {
                    cardinality2++;
                }
            }
        }

        return (double) intersection / Math.sqrt(1.0 * cardinality1 * cardinality2);
    }
}
