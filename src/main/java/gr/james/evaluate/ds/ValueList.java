package gr.james.evaluate.ds;

import gr.james.evaluate.io.TokenReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data structure that represents a map of unique elements into number values. The values cannot be
 * {@link Double#NaN}, {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY}.
 * <p>
 * The elements contained in this data structure must me immutable or effectively immutable for it to work.
 * <p>
 * All of the class methods run in constant time unless otherwise stated.
 *
 * @param <T> the type of elements
 */
public final class ValueList<T> {
    private final Map<T, Double> map;

    private ValueList(Map<T, Double> map) {
        for (Map.Entry<T, Double> e : map.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) {
                throw new NullPointerException();
            }
            if (!Double.isFinite(e.getValue())) {
                throw new IllegalArgumentException("A value in the map was not finite");
            }
        }
        this.map = new HashMap<>(map);
    }

    /**
     * Returns a new {@link ValueList} from the given value map.
     * <p>
     * This method runs in linear time.
     *
     * @param map the value map
     * @param <T> the type of elements
     * @return a new {@link ValueList} from {@code map}
     * @throws NullPointerException     if {@code map} is {@code null}
     * @throws NullPointerException     if any key or value in {@code map} is {@code null}
     * @throws IllegalArgumentException if any value in {@code map} is non-finite
     */
    public static <T> ValueList<T> fromMap(Map<T, Double> map) {
        return new ValueList<>(map);
    }

    /**
     * Returns a new {@link ValueList} from the given {@link Path}.
     *
     * @param p the {@link Path} of the file to read from
     * @return a new {@link ValueList} from {@code p}
     * @throws NullPointerException     if {@code p} is {@code null}
     * @throws IOException              if some I/O exception occurs while reading the file
     * @throws IllegalArgumentException if {@code p} is not of valid format
     */
    public static ValueList<String> fromPath(Path p) throws IOException {
        final Map<String, Double> values = new HashMap<>();
        try (final TokenReader reader = new TokenReader(p)) {
            reader.next();
            while (true) {
                final List<String> line = reader.next();
                if (line == null) {
                    break;
                }
                if (line.size() != 2) {
                    throw new IllegalArgumentException(String.format(
                            "Each line of a value list must contain exactly 2 entries, received: %s",
                            line)
                    );
                }
                final double value;
                try {
                    value = Double.parseDouble(line.get(1));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(String.format(
                            "Each line of a value list must have a number as the second entry, received: %s",
                            line)
                    );
                }
                if (values.containsKey(line.get(0))) {
                    throw new IllegalArgumentException(String.format(
                            "Each line of a value list must have a unique element as the first entry, received: %s",
                            line)
                    );
                }
                values.put(line.get(0), value);
            }
        }
        return fromMap(values);
    }

    /**
     * Returns a {@link RankedList} from this value list.
     * <p>
     * This method runs in linearithmic time.
     *
     * @return a {@link RankedList} from this value list
     */
    public RankedList<T> toRankedList() {
        final TreeMap<Double, Set<T>> treeMap = new TreeMap<>();
        for (Map.Entry<T, Double> e : map.entrySet()) {
            if (!treeMap.containsKey(e.getValue())) {
                treeMap.put(e.getValue(), new HashSet<>());
            }
            treeMap.get(e.getValue()).add(e.getKey());
        }
        final List<Set<T>> rankedList = new ArrayList<>(treeMap.values());
        return RankedList.fromRanks(rankedList);
    }

    /**
     * Returns a {@link Set} view of the elements in this data structure.
     * <p>
     * The elements are in no particular order inside the returned set.
     *
     * @return a {@link Set} view of the elements in this data structure
     */
    public Set<T> elements() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * Returns a {@link Collection} view of all values in this data structure.
     * <p>
     * The values are in no particular order inside the returned collection.
     *
     * @return a {@link Collection} view of all values in this data structure
     */
    public Collection<Double> values() {
        return Collections.unmodifiableCollection(this.map.values());
    }

    /**
     * Returns the value associated with an element in this data structure.
     *
     * @param t the element
     * @return the value associated with {@code t}
     * @throws NullPointerException     if {@code t} is {@code null}
     * @throws IllegalArgumentException if {@code t} is not an element of this data structure
     */
    public double get(T t) {
        Objects.requireNonNull(t);
        if (!this.map.containsKey(t)) {
            throw new IllegalArgumentException();
        }
        return this.map.get(t);
    }

    /**
     * Returns a string representation of the contents of this data structure.
     *
     * @return a string representation of the contents of this data structure
     */
    @Override
    public String toString() {
        return String.format("ValueList[%d] {%n%s%n}", elements().size(),
                this.map.entrySet().stream()
                        .map(e -> String.format("  %s %s", e.getKey(), e.getValue()))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
