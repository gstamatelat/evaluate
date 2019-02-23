package gr.james.evaluate.algorithms;

import gr.james.evaluate.ds.Partition;
import gr.james.evaluate.ds.Result;
import gr.james.evaluate.ds.ValueList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Pearson correlation coefficient implementation.
 */
public final class Pearson {
    /**
     * Convenience method that automatically invokes one of these methods based on the type of the arguments:
     * <ul>
     * <li>{@link #pearson(ValueList, ValueList)}</li>
     * <li>{@link #pearson(Partition, Partition)}</li>
     * </ul>
     * <p>
     * Returns {@code null} if the type of the arguments is not compatible with the algorithm.
     *
     * @param a   one {@link Result}
     * @param b   the other {@link Result}
     * @param <T> the type of elements
     * @return the Pearson correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException if {@code a} or {@code b} is {@code null}
     * @throws RuntimeException     if the arguments have some property that causes the algorithm to fail, specified in
     *                              the wrapped methods above
     */
    public static <T> Double pearson(Result<T> a, Result<T> b) {
        if (a.isValueList() && b.isValueList()) {
            return pearson(a.valueList, b.valueList);
        } else if (a.isPartition() && b.isPartition()) {
            return pearson(a.partition, b.partition);
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

    /**
     * Returns the Pearson correlation coefficient of two partitions.
     * <p>
     * This method is commutative (given no exception):
     * <pre><code>
     * assert pearson(a, b) == pearson(b, a);
     * </code></pre>
     *
     * @param a   one partition
     * @param b   the other partition
     * @param <T> the type of elements
     * @return the Pearson correlation coefficient between {@code a} and {@code b}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} and {@code b} do not contain exactly the same elements
     */
    public static <T> double pearson(Partition<T> a, Partition<T> b) {
        if (!a.elements().equals(b.elements())) {
            throw new IllegalArgumentException("a and b must have the same elements");
        }

        long aCount = 0;
        long bCount = 0;
        double aAvg = 0;
        double bAvg = 0;
        double aStd = 0;
        double bStd = 0;

        long totalPairs = a.elements().size() * (a.elements().size() - 1) / 2;

        /* Averages */
        for (Set<T> s : a.groups()) {
            aCount += s.size() * (s.size() - 1) / 2;
        }
        for (Set<T> s : b.groups()) {
            bCount += s.size() * (s.size() - 1) / 2;
        }
        aAvg = (double) aCount / (double) totalPairs;
        bAvg = (double) bCount / (double) totalPairs;

        /* Standard deviations */
        aStd = aCount * (1 - aAvg) * (1 - aAvg) + (totalPairs - aCount) * aAvg * aAvg;
        aStd = aStd / totalPairs;
        bStd = bCount * (1 - bAvg) * (1 - bAvg) + (totalPairs - bCount) * bAvg * bAvg;
        bStd = bStd / totalPairs;

        /* Covariance */
        double cov = 0;
        final List<T> vertices = new ArrayList<>(a.elements());
        for (int i = 0; i < vertices.size() - 1; i++) {
            for (int j = i + 1; j < vertices.size(); j++) {
                final boolean aHas = a.connected(vertices.get(i), vertices.get(j));
                final boolean bHas = b.connected(vertices.get(i), vertices.get(j));
                double cov1 = -aAvg;
                double cov2 = -bAvg;
                if (aHas) {
                    cov1 += 1;
                }
                if (bHas) {
                    cov2 += 1;
                }
                cov += cov1 * cov2;
            }
        }

        return (cov / totalPairs) / (Math.sqrt(aStd) * Math.sqrt(bStd));
    }
}
