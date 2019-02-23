package gr.james.evaluate.ds;

import gr.james.evaluate.io.TokenReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data structure that represents a partition of unique elements.
 * <p>
 * The elements contained in this data structure must me immutable or effectively immutable for it to work.
 * <p>
 * All of the class methods run in constant time unless otherwise stated.
 *
 * @param <T> the type of elements
 */
public final class Partition<T> {
    private final Set<Set<T>> groups;
    private final Map<T, Set<T>> map;

    private Partition(Collection<? extends Collection<T>> groups) {
        this.groups = new HashSet<>();
        this.map = new HashMap<>();
        for (Collection<T> group : groups) {
            if (group.isEmpty()) {
                throw new IllegalArgumentException("A group was empty");
            }
            final Set<T> tmpSet = new HashSet<>();
            for (T t : group) {
                Objects.requireNonNull(t);
                if (this.map.containsKey(t)) {
                    throw new IllegalArgumentException(String.format("An element had duplicate entries: %s", t));
                }
                tmpSet.add(t);
                this.map.put(t, tmpSet);
            }
            this.groups.add(tmpSet);
        }
    }

    /**
     * Returns a new {@link Partition} from the given element groups.
     * <p>
     * This method runs in linear time.
     *
     * @param groups an unordered list of element groups
     * @param <T>    the type of elements
     * @return a new {@link Partition} from {@code groups}
     * @throws NullPointerException     if {@code groups} is {@code null}
     * @throws NullPointerException     if any element in {@code groups} is {@code null}
     * @throws IllegalArgumentException if {@code groups} contains duplicate elements
     * @throws IllegalArgumentException if {@code groups} contains empty groups
     */
    public static <T> Partition<T> fromGroups(Collection<? extends Collection<T>> groups) {
        return new Partition<>(groups);
    }

    /**
     * Returns a new {@link Partition} from the given {@link Path}.
     * <p>
     * This method runs in linear time.
     *
     * @param p the {@link Path} of the file to read from
     * @return a new {@link Partition} from {@code p}
     * @throws NullPointerException     if {@code p} is {@code null}
     * @throws IOException              if some I/O exception occurs while reading the file
     * @throws IllegalArgumentException if {@code p} contains duplicate elements
     */
    public static Partition<String> fromPath(Path p) throws IOException {
        final List<List<String>> groups = new ArrayList<>();
        try (final TokenReader reader = new TokenReader(p)) {
            reader.next();
            while (true) {
                final List<String> line = reader.next();
                if (line == null) {
                    break;
                }
                groups.add(line);
            }
        }
        return fromGroups(groups);
    }

    /**
     * Returns a {@link Set} view of the elements in this data structure.
     * <p>
     * The elements are in no particular order inside the returned {@link Set}.
     *
     * @return a {@link Set} view of the elements in this data structure
     */
    public Set<T> elements() {
        return Collections.unmodifiableSet(this.map.keySet());
    }

    /**
     * Returns {@code true} if two elements belong to the same group, otherwise {@code false}.
     *
     * @param a one element
     * @param b the other element
     * @return {@code true} if {@code a} and {@code b} belong to the same group, otherwise {@code false}
     * @throws NullPointerException     if {@code a} or {@code b} is {@code null}
     * @throws IllegalArgumentException if {@code a} or {@code b} is not an element of this partition
     */
    public boolean connected(T a, T b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        if (!map.containsKey(a)) {
            throw new IllegalArgumentException(String.format("Element is not in this partition: %s", a));
        }
        if (!map.containsKey(b)) {
            throw new IllegalArgumentException(String.format("Element is not in this partition: %s", b));
        }
        return this.map.get(a) == this.map.get(b);
    }

    /**
     * Returns a {@link Set} view of the group of a certain element.
     *
     * @param x the element
     * @return a {@link Set} view of the group of {@code x}
     * @throws NullPointerException     if {@code x} is {@code null}
     * @throws IllegalArgumentException if {@code x} is not an element of this partition
     */
    public Set<T> setOf(T x) {
        Objects.requireNonNull(x);
        if (!map.containsKey(x)) {
            throw new IllegalArgumentException(String.format("Element is not in this partition: %s", x));
        }
        return Collections.unmodifiableSet(this.map.get(x));
    }

    /**
     * Returns a string representation of the contents of this data structure.
     *
     * @return a string representation of the contents of this data structure
     */
    @Override
    public String toString() {
        return String.format("Partition[%d] {%n%s%n}", elements().size(),
                this.groups.stream().map(ts -> String.format("  %s",
                        ts.stream().map(Object::toString).collect(Collectors.joining(" "))
                )).collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
