package gr.james.evaluate;

import gr.james.evaluate.ds.Result;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Tests {
    /**
     * Use the {@link Result#fromPath(Path)} method to load all types of data structures.
     */
    @Test
    public void fromPath() throws IOException, URISyntaxException {
        final List<String> files = Arrays.asList(
                "/simple-ranks.txt",
                "/simple-values.txt",
                "/simple-single-ranks.txt",
                "/simple-partition.txt"
        );
        for (String f : files) {
            final URL url = Tests.class.getResource(f);
            Result<String> resultRanks = Result.fromPath(Paths.get(url.toURI()));
            System.out.println(resultRanks);
        }
    }
}
