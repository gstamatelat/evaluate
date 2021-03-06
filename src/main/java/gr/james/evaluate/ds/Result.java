package gr.james.evaluate.ds;

import gr.james.evaluate.Helper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Immutable union for all data structures.
 * <p>
 * A {@code Result} can hold one of the following data structures:
 * <ul>
 * <li>{@link TiedRankedList}</li>
 * <li>{@link ValueList}</li>
 * <li>{@link RankedList}</li>
 * <li>{@link Partition}</li>
 * </ul>
 */
public final class Result<T> {
    /**
     * The {@link TiedRankedList} contained within this {@link Result} or {@code null} if this {@code Result} does not
     * contain a {@code TiedRankedList}.
     */
    public final TiedRankedList<T> tiedRankedList;

    /**
     * The {@link ValueList} contained within this {@link Result} or {@code null} if this {@code Result} does not
     * contain a {@code ValueList}.
     */
    public final ValueList<T> valueList;

    /**
     * The {@link RankedList} contained within this {@link Result} or {@code null} if this {@code Result} does not
     * contain a {@code RankedList}.
     */
    public final RankedList<T> rankedList;

    /**
     * The {@link Partition} contained within this {@link Result} or {@code null} if this {@code Result} does not
     * contain a {@code Partition}.
     */
    public final Partition<T> partition;

    /**
     * Construct a new {@link Result} from the given {@link TiedRankedList}.
     *
     * @param tiedRankedList the {@link TiedRankedList} to be contained within this {@code Result}
     * @throws NullPointerException if {@code tiedRankedList} is {@code null}
     */
    public Result(TiedRankedList<T> tiedRankedList) {
        this.tiedRankedList = Objects.requireNonNull(tiedRankedList);
        this.valueList = null;
        this.rankedList = null;
        this.partition = null;
    }

    /**
     * Construct a new {@link Result} from the given {@link ValueList}.
     *
     * @param valueList the {@link ValueList} to be contained within this {@code Result}
     * @throws NullPointerException if {@code valueList} is {@code null}
     */
    public Result(ValueList<T> valueList) {
        this.tiedRankedList = null;
        this.valueList = Objects.requireNonNull(valueList);
        this.rankedList = null;
        this.partition = null;
    }

    /**
     * Construct a new {@link Result} from the given {@link RankedList}.
     *
     * @param rankedList the {@link RankedList} to be contained within this {@code Result}
     * @throws NullPointerException if {@code rankedList} is {@code null}
     */
    public Result(RankedList<T> rankedList) {
        this.tiedRankedList = null;
        this.valueList = null;
        this.rankedList = Objects.requireNonNull(rankedList);
        this.partition = null;
    }

    /**
     * Construct a new {@link Result} from the given {@link Partition}.
     *
     * @param partition the {@link Partition} to be contained within this {@code Result}
     * @throws NullPointerException if {@code partition} is {@code null}
     */
    public Result(Partition<T> partition) {
        this.tiedRankedList = null;
        this.valueList = null;
        this.rankedList = null;
        this.partition = Objects.requireNonNull(partition);
    }

    /**
     * Construct and return a new {@link Result} from the given {@link Path}.
     * <p>
     * The file must be UTF-8 encoded and contain a hash value as the first line determining the type of structure. For
     * example:
     * <pre><code>
     * # values
     * dog 0.8
     * cat 0.2
     * bear -0.5
     * </code></pre>
     *
     * @param p the {@code Path} pointing to the file in the filesystem
     * @return a new {@link Result} from {@code p}
     * @throws NullPointerException     if {@code p} is {@code null}
     * @throws IOException              if some I/O exception occurs while processing the file
     * @throws IllegalArgumentException if {@code p} doesn't have a proper hash
     * @throws IllegalArgumentException if {@code p} doesn't have a recognizable hash
     * @throws RuntimeException         if {@code p} is not of proper format
     */
    public static Result<String> fromPath(Path p) throws IOException {
        final String hash = Helper.readHash(p);
        if (hash == null) {
            throw new IllegalArgumentException("No hash found on file");
        }
        switch (hash) {
            case "values":
                return new Result<>(ValueList.fromPath(p));
            case "tie-ranks":
                return new Result<>(TiedRankedList.fromPath(p));
            case "ranks":
                return new Result<>(RankedList.fromPath(p));
            case "partition":
                return new Result<>(Partition.fromPath(p));
            default:
                throw new IllegalArgumentException(String.format("Not a valid hash: %s", hash));
        }
    }

    /**
     * Returns {@code true} if this {@link Result} represents a {@link ValueList}, otherwise {@code false}.
     *
     * @return {@code true} if this {@link Result} represents a {@link ValueList}, otherwise {@code false}
     */
    public boolean isValueList() {
        return this.valueList != null;
    }

    /**
     * Returns {@code true} if this {@link Result} represents a {@link RankedList}, otherwise {@code false}.
     *
     * @return {@code true} if this {@link Result} represents a {@link RankedList}, otherwise {@code false}
     */
    public boolean isRankedList() {
        return this.rankedList != null;
    }

    /**
     * Returns {@code true} if this {@link Result} represents a {@link TiedRankedList}, otherwise {@code false}.
     *
     * @return {@code true} if this {@link Result} represents a {@link TiedRankedList}, otherwise {@code false}
     */
    public boolean isTiedRankedList() {
        return this.tiedRankedList != null;
    }

    /**
     * Returns {@code true} if this {@link Result} represents a {@link Partition}, otherwise {@code false}.
     *
     * @return {@code true} if this {@link Result} represents a {@link Partition}, otherwise {@code false}
     */
    public boolean isPartition() {
        return this.partition != null;
    }

    /**
     * Returns a {@link TiedRankedList} representing this {@link Result} if the object contained can be cast to
     * {@code TiedRankedList}, otherwise returns {@code null}.
     * <p>
     * Specifically, this method will return a {@code TiedRankedList} if the content of this {@code Result} is one of
     * the following:
     * <ul>
     * <li>{@link RankedList}</li>
     * <li>{@link TiedRankedList}</li>
     * <li>{@link ValueList}</li>
     * </ul>
     *
     * @return a {@link TiedRankedList} representing this {@link Result} or {@code null} if the data structure in this
     * {@code Result} cannot be cast to {@code TiedRankedList}.
     */
    public TiedRankedList<T> tryGetTiedRankedList() {
        if (this.tiedRankedList != null) {
            return this.tiedRankedList;
        } else if (this.rankedList != null) {
            return this.rankedList.toTiedRankedList();
        } else if (this.valueList != null) {
            return this.valueList.toTiedRankedList();
        } else {
            return null;
        }
    }

    /**
     * Returns a string representation of the underlying data structure of this {@link Result}.
     *
     * @return a string representation of the underlying data structure of this {@link Result}
     */
    @Override
    public String toString() {
        if (this.tiedRankedList != null) {
            return this.tiedRankedList.toString();
        } else if (this.valueList != null) {
            return this.valueList.toString();
        } else if (this.rankedList != null) {
            return this.rankedList.toString();
        } else if (this.partition != null) {
            return this.partition.toString();
        } else {
            throw new RuntimeException("This must never happen");
        }
    }
}
