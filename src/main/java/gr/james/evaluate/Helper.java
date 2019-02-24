package gr.james.evaluate;

import gr.james.evaluate.algorithms.Kendall;
import gr.james.evaluate.ds.RankedList;
import gr.james.evaluate.ds.TiedRankedList;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Utility static methods.
 */
public final class Helper {
    /**
     * Definition of whitespace, equal to the regex {@code \\s+}.
     */
    public static final String WHITESPACE = "\\s+";

    /**
     * Returns the flattened view of a {@link List} of elements as a list of singleton collections.
     *
     * @param x   the list
     * @param <T> the type of elements
     * @return the flattened view of {@code x}
     * @throws NullPointerException if {@code x} is {@code null}
     */
    public static <T> List<Collection<T>> flatten(List<T> x) {
        final List<Collection<T>> otherList = new ArrayList<>();
        for (T t : x) {
            otherList.add(Collections.singleton(t));
        }
        return otherList;
    }

    /**
     * Trim {@link #WHITESPACE} from the beginning and the end of a {@link String}.
     *
     * @param s the {@link String} to trim.
     * @return a new trimmed {@link String}
     * @throws NullPointerException if {@code s} is {@code null}
     */
    public static String trim(String s) {
        s = s.replaceAll("^" + WHITESPACE, "");
        s = s.replaceAll(WHITESPACE + "$", "");
        return s;
    }

    /**
     * Returns the hash of a path in the filesystem.
     * <p>
     * The hash is the string in the first line of the file after the hash character.
     * <p>
     * Returns {@code null} if no hash was found.
     *
     * @param p the {@link Path} of the file
     * @return the hash string contained in the first line of the file represented by {@code p}
     * @throws NullPointerException if {@code p} is {@code null}
     * @throws IOException          if some I/O exception occurs while reading the file
     */
    public static String readHash(Path p) throws IOException {
        try (final BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return null;
            }
            firstLine = trim(firstLine);
            if (firstLine.startsWith("#")) {
                firstLine = firstLine.substring(1);
                return trim(firstLine);
            } else {
                return null;
            }
        }
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
    public static <T> double maxKendall(TiedRankedList<T> x) {
        final List<T> flattened = new ArrayList<>();
        for (Set<T> s : x) {
            flattened.addAll(s);
        }
        return Kendall.kendall(x, RankedList.fromRanks(flattened).toTiedRankedList());
    }
}
