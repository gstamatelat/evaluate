package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.RankedList;
import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.SingleRankedList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Kendall tau-b correlation coefficient implementation.
 */
public final class Kendall {
    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #kendall(RankedList, RankedList)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Kendall tau-b correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double kendall(Result<T> a, Result<T> b) {
        if (!a.isValueList() && !a.isRankedList() && !a.isSingleRankList()) {
            return null;
        }
        if (!b.isValueList() && !b.isRankedList() && !b.isSingleRankList()) {
            return null;
        }
        final RankedList<T> aList = a.isValueList() ? a.valueList.toRankedList() :
                (a.isSingleRankList() ? a.singleRankedList.torankedList() : a.rankedList);
        final RankedList<T> bList = b.isValueList() ? b.valueList.toRankedList() :
                (b.isSingleRankList() ? b.singleRankedList.torankedList() : b.rankedList);
        return kendall(aList, bList);
    }

    /**
     * Returns the Kendall tau-b correlation of two ranked lists.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert kendall(a, b) == kendall(b, a);
     * </code></pre>
     *
     * @param a   one ranked list
     * @param b   the other ranked list
     * @param <T> the type of elements
     * @return the Kendall tau-b correlation between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double kendall(RankedList<T> a, RankedList<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        int numerator = 0;
        for (T x : a.elements()) {
            for (T y : b.elements()) {
                long sign = (long) (a.indexOf(x) - a.indexOf(y)) * (long) (b.indexOf(x) - b.indexOf(y));
                if (sign > 0) {
                    numerator += 1;
                } else if (sign < 0) {
                    numerator -= 1;
                }
            }
        }
        assert numerator % 2 == 0;
        numerator /= 2;

        long n = (a.elementsCount() * (a.elementsCount() - 1)) / 2;

        long n1 = 0;
        long n2 = 0;
        for (Collection<T> a0 : a) {
            n1 += (a0.size() * (a0.size() - 1)) / 2;
        }
        for (Collection<T> b0 : b) {
            n2 += (b0.size() * (b0.size() - 1)) / 2;
        }

        return numerator / (Math.sqrt(n - n1) * Math.sqrt(n - n2));
    }

    /**
     * Returns the maximum value of Kendall tau-b correlation of a tied ranked list with any other ranked list given
     * the assertion that the latter does not contain any ties.
     *
     * @param x   the tied ranked list
     * @param <T> the type of elements
     * @return the maximum value of Kendall tau-b correlation of {@code x} with any other ranked list without ties
     * @throws NullPointerException if {@code x} is {@code null}
     */
    public static <T> double maxKendall(RankedList<T> x) {
        final List<T> flattened = new ArrayList<>();
        for (Set<T> s : x) {
            flattened.addAll(s);
        }
        return kendall(x, SingleRankedList.fromRanks(flattened).torankedList());
    }
}
