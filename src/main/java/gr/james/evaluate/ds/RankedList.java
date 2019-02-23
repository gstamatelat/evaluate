package gr.james.evaluate.ds;

import gr.james.evaluate.Helper;
import gr.james.evaluate.io.TokenReader;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Immutable data structure that represents a ranked list of unique elements. The ranked list can contain ties.
 * <p>
 * The elements contained in this data structure must me immutable or effectively immutable for it to work.
 * <p>
 * All of the class methods run in constant time unless otherwise stated.
 *
 * @param <T> the type of elements
 */
public final class RankedList<T> implements Iterable<Set<T>> {
    private final Map<T, Integer> indices;
    private final List<Set<T>> ranks;

    private RankedList(List<? extends Collection<T>> ranks) {
        this.indices = new HashMap<>();
        this.ranks = new ArrayList<>();
        for (int i = 0; i < ranks.size(); i++) {
            final Collection<T> tmpList = ranks.get(i);
            if (tmpList.isEmpty()) {
                throw new IllegalArgumentException("Input contains empty ranks");
            }
            for (T t : tmpList) {
                Objects.requireNonNull(t);
                if (this.indices.containsKey(t)) {
                    throw new IllegalArgumentException("Input contains duplicate elements");
                }
                this.indices.put(t, i);
            }
            this.ranks.add(new HashSet<>(tmpList));
        }
    }

    /**
     * Returns a new {@link RankedList} from the given ranked list with possible ties.
     * <p>
     * This method runs in linear time.
     *
     * @param ranks an ordered list of ranked elements with possible ties
     * @param <T>   the type of elements
     * @return a new {@link RankedList} from {@code ranks}
     * @throws NullPointerException     if {@code ranks} is {@code null}
     * @throws NullPointerException     if any element in {@code ranks} is {@code null}
     * @throws IllegalArgumentException if {@code ranks} contains duplicate elements
     * @throws IllegalArgumentException if {@code ranks} contains empty ranks
     */
    public static <T> RankedList<T> fromRanks(List<? extends Collection<T>> ranks) {
        return new RankedList<>(ranks);
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
    public static <T> RankedList<T> fromSingletonRanks(List<T> ranks) {
        return new RankedList<>(Helper.flatten(ranks));
    }

    /**
     * Returns a new {@link RankedList} from the given {@link Path}.
     *
     * @param p the {@link Path} of the file to read from
     * @return a new {@link RankedList} from {@code p}
     * @throws NullPointerException     if {@code p} is {@code null}
     * @throws IOException              if some I/O exception occurs while reading the file
     * @throws IllegalArgumentException if {@code p} contains duplicate elements
     */
    public static RankedList<String> fromPath(Path p) throws IOException {
        final List<Collection<String>> ranks = new ArrayList<>();
        try (final TokenReader reader = new TokenReader(p)) {
            reader.next();
            while (true) {
                final List<String> line = reader.next();
                if (line == null) {
                    break;
                }
                ranks.add(line);
            }
        }
        return fromRanks(ranks);
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
     * Returns the amount of unique ranks in this ranked list.
     * <p>
     * This number is always less than or equal to {@link #elementsCount()}.
     *
     * @return the amount of unique ranks in this ranked list
     */
    public int ranksCount() {
        return this.ranks.size();
    }

    /**
     * Returns the number of elements in this ranked list.
     * <p>
     * This number is always greater or equal than {@link #ranksCount()}.
     *
     * @return the number of elements in this ranked list
     */
    public int elementsCount() {
        return this.indices.size();
    }

    /**
     * Returns the element or elements at the specified index.
     * <p>
     * The returned {@link Set} cannot be empty. The elements are in no particular order inside the returned
     * {@link Set}.
     *
     * @param index the index inside this ranked list
     * @return the element or elements at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the bounds of the ranks in this list
     */
    public Set<T> get(int index) {
        return Collections.unmodifiableSet(this.ranks.get(index));
    }

    /**
     * Returns the single element at the specified index.
     *
     * @param index the index inside this ranked list
     * @return the element at {@code index}
     * @throws IndexOutOfBoundsException if {@code index} is outside the bounds of the ranks in this list
     * @throws IllegalArgumentException  if there are more than one elements at {@code index}
     */
    public T getSingle(int index) {
        final Set<T> tmp = get(index);
        if (tmp.size() != 1) {
            throw new IllegalArgumentException();
        }
        return tmp.iterator().next();
    }

    /**
     * Returns an {@link Iterator} over the ranks of this ranked list.
     *
     * @return an {@link Iterator} over the ranks of this ranked list
     */
    @Override
    public Iterator<Set<T>> iterator() {
        return new Iterator<Set<T>>() {
            int i = 0;

            @Override
            public boolean hasNext() {
                return i < RankedList.this.ranks.size();
            }

            @Override
            public Set<T> next() {
                if (RankedList.this.ranks.size() <= i) {
                    throw new NoSuchElementException();
                }
                return Collections.unmodifiableSet(RankedList.this.ranks.get(i++));
            }
        };
    }

    /**
     * Returns a string representation of the contents of this data structure.
     *
     * @return a string representation of the contents of this data structure
     */
    @Override
    public String toString() {
        return String.format("RankedList[%d] {%n%s%n}", elementsCount(),
                this.ranks.stream().map(ts -> String.format("  %s",
                        ts.stream().map(Object::toString).collect(Collectors.joining(" "))
                )).collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
