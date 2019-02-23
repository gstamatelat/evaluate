package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.ValueList;

/**
 * Cosine similarity coefficient implementation.
 */
public final class Cosine {
    /**
     * Convenience method that invokes {@link #cosine(ValueList, ValueList)} if both inputs are of type
     * {@link ValueList}.
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Cosine similarity between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              {@link #cosine(ValueList, ValueList)}
     */
    public static <T> Double cosine(Result<T> a, Result<T> b) {
        if (a.isValueList() && b.isValueList()) {
            return cosine(a.valueList, b.valueList);
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
}
