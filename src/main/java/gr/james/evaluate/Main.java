package gr.james.evaluate;

import gr.james.evaluate.algorithms.*;
import gr.james.evaluate.ds.Result;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with the app {@code main} method.
 */
public class Main {
    /**
     * Main app entry.
     * <p>
     * Must have at least two filesystem paths as arguments. The first one is the ground truth and the rest are to be
     * considered for evaluation.
     *
     * @param args an array of paths
     * @throws IOException if some I/O error occurs while processing the file arguments
     */
    public static void main(String[] args) throws IOException {
        // Argument checks
        if (args.length < 2) {
            throw new IllegalArgumentException("Must have at least 2 arguments, the truth and one evaluation result");
        }

        // Load truth
        final Path truthPath = Paths.get(args[0]);
        final Result<String> truth = Result.fromPath(truthPath);

        // Load other files
        final List<Path> fileList = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            fileList.add(Paths.get(args[i]));
        }

        // Print the arguments
        System.out.printf("%s (truth):%n%s%n%n", truthPath.getFileName(), Result.fromPath(truthPath));
        for (Path f : fileList) {
            System.out.printf("%s:%n%s%n%n", f.getFileName(), Result.fromPath(f));
        }

        // Print overview information
        System.out.printf("Evaluating against %s%n", truthPath.getFileName());
        System.out.printf("Max Kendall tau-b (applicable on single ranked lists): %.4f%n%n",
                Kendall.maxKendall(truth.isRankedList() ? truth.rankedList : truth.valueList.toRankedList())
        );

        // Calculate max name for printing formats
        final int maximumLength = fileList.stream()
                .mapToInt(p -> p.getFileName().toString().length()).max().orElse(0);
        final String format = String.format("%%-%ds %%8.4f %%8.4f %%8.4f %%8.4f %%8.4f %%8.4f %%8.4f %%n", maximumLength);
        final String headerFormat = String.format("%%-%ds %%8s %%8s %%8s %%8s %%8s %%8s %%8s %%n", maximumLength);

        // Iterate other files and print correlations
        // TODO: Print hyphen instead of null
        System.out.printf(headerFormat, "Name", "Kendall", "Pearson", "Cosine", "Jaccard", "MI", "SMC", "F1");
        for (Path p : fileList) {
            final Result<String> r = Result.fromPath(p);
            System.out.printf(format,
                    p.getFileName(),
                    Kendall.kendall(truth, r),
                    Pearson.pearson(truth, r),
                    Cosine.cosine(truth, r),
                    Jaccard.jaccard(truth, r),
                    MI.mi(truth, r),
                    SMC.smc(truth, r),
                    Sorensen.sorensen(truth, r)
            );
        }
    }
}
