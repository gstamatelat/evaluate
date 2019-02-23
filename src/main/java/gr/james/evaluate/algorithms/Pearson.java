package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.ValueList;

/**
 * Pearson correlation coefficient implementation.
 */
public final class Pearson {
    /**
     * Convenience method that invokes {@link #pearson(ValueList, ValueList)} if both inputs are of type
     * {@link ValueList}.
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Pearson correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              {@link #pearson(ValueList, ValueList)}
     */
    public static <T> Double pearson(Result<T> a, Result<T> b) {
        if (a.isValueList() && b.isValueList()) {
            return pearson(a.valueList, b.valueList);
        } else {
            return null;
        }
    }

    /**
     * Returns the Pearson correlation coefficient of two value maps.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert pearson(a, b) == pearson(b, a);
     * </code></pre>
     *
     * @param a   one value map
     * @param b   the other value map
     * @param <T> the type of elements
     * @return the Pearson correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     * @throws IllegalArgumentException if {@code a} or {@code b} is empty
     */
    public static <T> double pearson(ValueList<T> a, ValueList<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        double averageA = a.values().stream().mapToDouble(x -> x).average()
                .orElseThrow(() -> new IllegalArgumentException("Cannot get the average of a"));
        double averageB = b.values().stream().mapToDouble(x -> x).average()
                .orElseThrow(() -> new IllegalArgumentException("Cannot get the average of b"));

        double cov = 0;
        for (T t : a.elements()) {
            cov += (a.get(t) - averageA) * (b.get(t) - averageB);
        }
        cov /= a.elements().size();

        double varA = 0;
        double varB = 0;
        for (T t : a.elements()) {
            varA += Math.pow(a.get(t) - averageA, 2);
            varB += Math.pow(b.get(t) - averageA, 2);
        }
        varA /= a.elements().size();
        varB /= a.elements().size();
        varA = Math.sqrt(varA);
        varB = Math.sqrt(varB);

        return cov / (varA * varB);
    }
}
