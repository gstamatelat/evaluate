package gr.james.evaluate;

import gr.james.evaluate.algorithms.Pearson;
import gr.james.evaluate.ds.Result;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Modifier;
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
                "/simple-tied-ranks.txt",
                "/simple-values.txt",
                "/simple-ranks.txt",
                "/simple-partition.txt"
        );
        for (String f : files) {
            final URL url = Tests.class.getResource(f);
            Result<String> resultRanks = Result.fromPath(Paths.get(url.toURI()));
            System.out.println(resultRanks);
        }
    }

    /**
     * Pearson coefficient must be commutative. This test applies values lists.
     */
    @Test
    public void pearsonValuesCommutativity() throws URISyntaxException, IOException {
        final Result<String> valuesRandomA =
                Result.fromPath(Paths.get(Tests.class.getResource("/values-random-a.txt").toURI()));
        final Result<String> valuesRandomB =
                Result.fromPath(Paths.get(Tests.class.getResource("/values-random-b.txt").toURI()));
        Assert.assertEquals(
                Pearson.pearson(valuesRandomA, valuesRandomB),
                Pearson.pearson(valuesRandomB, valuesRandomA)
        );
    }

    /**
     * All classes inside the algorithms package must be final and have a single private constructor.
     */
    @Test
    public void algorithmsFinalAndPrivateConstructor() throws IOException, ClassNotFoundException {
        final Class[] classes = TestsHelper.getClasses("gr.james.evaluate.algorithms");
        Assert.assertNotEquals(0, classes.length);
        for (Class c : classes) {
            Assert.assertTrue(Modifier.isFinal(c.getModifiers()));
            Assert.assertEquals(1, c.getDeclaredConstructors().length);
            Assert.assertTrue(Modifier.isPrivate(c.getDeclaredConstructors()[0].getModifiers()));
        }
        System.out.println();
    }
}
