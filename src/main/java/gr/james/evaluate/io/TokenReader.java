package gr.james.evaluate.io;

import gr.james.evaluate.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation of a text file parser that reads a file line by line.
 * <p>
 * Outputs the contents of the file using the {@link #next()} method as a {@link List} of {@link String} tokens.
 */
public class TokenReader implements AutoCloseable {
    private final BufferedReader in;
    private final Path p;

    /**
     * Constructs a new {@link TokenReader} from a {@link Path}.
     *
     * @param p the {@link Path}
     * @throws IOException if some I/O exception occurs while opening the file
     */
    public TokenReader(Path p) throws IOException {
        this.in = Files.newBufferedReader(p, StandardCharsets.UTF_8);
        this.p = p;
    }

    /**
     * Read the next line that has at least one token and output it as a {@link List}.
     * <p>
     * Returns {@code null} if the resource has no more lines. Naturally, the list returned will never be empty (but
     * can be {@code null}).
     *
     * @return a list of tokens representing the next line
     * @throws IOException if some I/O exception occurs while reading the file
     */
    public List<String> next() throws IOException {
        final List<String> lineSplit = new ArrayList<>();
        do {
            final String line = in.readLine();
            if (line == null) {
                return null;
            }
            for (String l : line.split(Helper.WHITESPACE)) {
                if (!l.isEmpty()) {
                    lineSplit.add(l);
                }
            }
        } while (lineSplit.isEmpty());
        return Collections.unmodifiableList(lineSplit);
    }

    /**
     * Closes this resource.
     * <p>
     * This method is meant to be used with the try-with-resources block.
     *
     * @throws IOException if some I/O exception occurs while closing the file
     */
    @Override
    public void close() throws IOException {
        this.in.close();
    }
}
