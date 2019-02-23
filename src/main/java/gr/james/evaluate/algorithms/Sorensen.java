package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Partition;
import gr.james.evaluate.ds.Result;

/**
 * Sorensen-Dice coefficient implementation.
 */
public class Sorensen {
    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #sorensen(Partition, Partition)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Sorensen-Dice coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double sorensen(Result<T> a, Result<T> b) {
        if (a.isPartition() && b.isPartition()) {
            return sorensen(a.partition, b.partition);
        } else {
            return null;
        }
    }

    public static <T> double sorensen(Partition<T> a, Partition<T> b) {
        // TODO
        return 0;
    }
}
