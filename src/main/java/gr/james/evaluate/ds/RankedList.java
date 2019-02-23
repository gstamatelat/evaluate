package gr.james.evaluate.ds;

import gr.james.evaluate.Helper;
import gr.james.evaluate.io.TokenReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data structure that represents a ranked list of unique elements. The ranked list cannot contain ties.
 * <p>
 * The elements contained in this data structure must me immutable or effectively immutable for it to work.
 * <p>
 * All of the class methods run in constant time unless otherwise stated.
 *
 * @param <T> the type of elements
 */
public final class RankedList<T> implements Iterable<T> {
    private final Map<T, Integer> indices;
    private final List<T> ranks;

    private RankedList(List<T> ranks) {
        this.indices = new HashMap<>();
        this.ranks = new ArrayList<>();
        for (int i = 0; i < ranks.size(); i++) {
            final T key = ranks.get(i);
            Objects.requireNonNull(key);
            if (this.indices.containsKey(key)) {
                throw new IllegalArgumentException("Input contains duplicate elements");
            }
            this.indices.put(key, i);
            this.ranks.add(key);
        }
    }

    /**
     * Returns a new {@link RankedList} from the given ranked list.
     * <p>
     * This method runs in linear time.
     *
     * @param ranks an ordered list of ranked elements
     * @param <T>   the type of elements
     * @return a new {@link RankedList} from {@code ranks}
     * @throws NullPointerException     if {@code ranks} is {@code null}
     * @throws NullPointerException     if any element in {@code ranks} is {@code null}
     * @throws IllegalArgumentException if {@code ranks} contains duplicate elements
     */
    public static <T> RankedList<T> fromRanks(List<T> ranks) {
        return new RankedList<>(ranks);
    }

    /**
     * Returns a new {@link RankedList} from the given {@link Path}.
     * <p>
     * This method runs in linear time.
     *
     * @param p the {@link Path} of the file to read from
     * @return a new {@link RankedList} from {@code p}
     * @throws NullPointerException     if {@code p} is {@code null}
     * @throws IOException              if some I/O exception occurs while reading the file
     * @throws IllegalArgumentException if {@code p} contains duplicate elements
     * @throws IllegalArgumentException if {@code p} is not of valid format
     */
    public static RankedList<String> fromPath(Path p) throws IOException {
        final List<String> ranks = new ArrayList<>();
        try (final TokenReader reader = new TokenReader(p)) {
            reader.next();
            while (true) {
                final List<String> line = reader.next();
                if (line == null) {
                    break;
                }
                if (line.size() != 1) {
                    throw new IllegalArgumentException(
                            String.format("Each line must have exactly one entry, received: %s", line)
                    );
                }
                ranks.add(line.get(0));
            }
        }
        return fromRanks(ranks);
    }

    /**
     * Returns a {@link TiedRankedList} from this data structure.
     * <p>
     * This method runs in linear time.
     *
     * @return a {@link TiedRankedList} from this data structure
     */
    public TiedRankedList<T> toTiedRankedList() {
        return TiedRankedList.fromRanks(Helper.flatten(ranks));
    }

    /**
     * Returns the index of the specified element.
     *
     * @param t the element to find in the ranked list
     * @return the index of {@code t}
     * @throws NullPointerException     if {@code t} is {@code null}
     * @throws IllegalArgumentException if {@code t} is not an element of this list
     */
    public int indexOf(T t) {
        Objects.requireNonNull(t);
        if (!this.indices.containsKey(t)) {
            throw new IllegalArgumentException();
        }
        return this.indices.get(t);
    }

    /**
     * Returns a {@link Set} view of the elements in this data structure.
     * <p>
     * The elements are in no particular order inside the returned {@link Set}.
     *
     * @return a {@link Set} view of the elements in this data structure
     */
    public Set<T> elements() {
        return this.indices.keySet();
    }

    /**
     * Returns the number of elements in this ranked list.
     *
     * @return the number of elements in this ranked list
     */
    public int elementsCount() {
        return this.indices.size();
    }

    /**
     * Returns the element at the specified index.
     *
     * @param index the index inside this ranked list
     * @return the element or elements at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the bounds of the ranks in this list
     */
    public T get(int index) {
        return this.ranks.get(index);
    }

    /**
     * Returns an {@link Iterator} over the ranks of this ranked list.
     *
     * @return an {@link Iterator} over the ranks of this ranked list
     */
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableList(this.ranks).iterator();
    }

    /**
     * Returns a string representation of the contents of this data structure.
     *
     * @return a string representation of the contents of this data structure
     */
    @Override
    public String toString() {
        return String.format("RankedList[%d] {%n%s%n}", elementsCount(),
                this.ranks.stream().map(ts -> String.format("  %s", ts))
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
