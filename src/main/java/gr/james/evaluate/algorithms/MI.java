package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Partition;
import gr.james.evaluate.ds.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Normalized Mutual Information implementation.
 */
public class MI {
    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #mi(Partition, Partition)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Normalized Mutual Information between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double mi(Result<T> a, Result<T> b) {
        if (a.isPartition() && b.isPartition()) {
            return mi(a.partition, b.partition);
        } else {
            return null;
        }
    }

    /**
     * Returns the Normalized Mutual Information of two partitions.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert mi(a, b) == mi(b, a);
     * </code></pre>
     *
     * @param a   one partition
     * @param b   the other partition
     * @param <T> the type of elements
     * @return the Normalized Mutual Information between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double mi(Partition<T> a, Partition<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        long N00 = 0;
        long N01 = 0;
        long N10 = 0;
        long N11 = 0;

        final List<T> vertices = new ArrayList<>(a.elements());
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                final boolean aHas = a.connected(vertices.get(i), vertices.get(j));
                final boolean bHas = b.connected(vertices.get(i), vertices.get(j));
                if (aHas && bHas) {
                    N11++;
                }
                if (aHas && !bHas) {
                    N10++;
                }
                if (!aHas && bHas) {
                    N01++;
                }
                if (!aHas && !bHas) {
                    N00++;
                }
            }
        }

        final long N = N00 + N01 + N10 + N11;
        final long N1X = N10 + N11;
        final long N0X = N00 + N01;
        final long NX0 = N00 + N10;
        final long NX1 = N01 + N11;

        final double pmi1 = (1.0 * N11 / N) * Math.log((1.0 * N * N11) / (1.0 * N1X * NX1)) / Math.log(2);
        final double pmi2 = (1.0 * N01 / N) * Math.log((1.0 * N * N01) / (1.0 * N0X * NX1)) / Math.log(2);
        final double pmi3 = (1.0 * N10 / N) * Math.log((1.0 * N * N10) / (1.0 * N1X * NX0)) / Math.log(2);
        final double pmi4 = (1.0 * N00 / N) * Math.log((1.0 * N * N00) / (1.0 * N0X * NX0)) / Math.log(2);

        final double entropy1 = (1.0 * N0X / N) * Math.log(1.0 * N0X / N) / Math.log(2) +
                (1.0 * N1X / N) * Math.log(1.0 * N1X / N) / Math.log(2);

        final double entropy2 = (1.0 * NX0 / N) * Math.log(1.0 * NX0 / N) / Math.log(2) +
                (1.0 * NX1 / N) * Math.log(1.0 * NX1 / N) / Math.log(2);

        final double mi = (Double.isNaN(pmi1) ? 0.0 : pmi1) +
                (Double.isNaN(pmi2) ? 0.0 : pmi2) +
                (Double.isNaN(pmi3) ? 0.0 : pmi3) +
                (Double.isNaN(pmi4) ? 0.0 : pmi4);

        return -(2 * mi) / (entropy1 + entropy2);
    }
}
