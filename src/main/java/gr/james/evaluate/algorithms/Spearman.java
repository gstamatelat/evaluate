package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.RankedList;
import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.ValueList;

import java.util.HashMap;
import java.util.Map;

/**
 * Spearman's rank correlation coefficient implementation.
 */
public final class Spearman {
    private Spearman() {
    }

    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #spearman(RankedList, RankedList)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Spearman's rank correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double spearman(Result<T> a, Result<T> b) {
        if (a.isRankedList() && b.isRankedList()) {
            return spearman(a.rankedList, b.rankedList);
        } else {
            return null;
        }
    }

    /**
     * Returns the Spearman's rank correlation coefficient of two value maps.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert spearman(a, b) == spearman(b, a);
     * </code></pre>
     *
     * @param a   one value map
     * @param b   the other value map
     * @param <T> the type of elements
     * @return the Spearman's rank correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     * @throws IllegalArgumentException if {@code a} or {@code b} is empty
     */
    public static <T> double spearman(RankedList<T> a, RankedList<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        final Map<T, Double> aMap = new HashMap<>();
        final Map<T, Double> bMap = new HashMap<>();

        for (T t : a) {
            aMap.put(t, a.indexOf(t) + 1.0);
            bMap.put(t, b.indexOf(t) + 1.0);
        }

        return Pearson.pearson(ValueList.fromMap(aMap), ValueList.fromMap(bMap));
    }
}
