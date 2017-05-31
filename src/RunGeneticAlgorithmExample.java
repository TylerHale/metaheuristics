import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.PopEle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RunGeneticAlgorithmExample {

    private static float valZ = 12501.4f * 100;
    private static Random rng = new Random();

    // One point cross over
    private static BiFunction<Vector<Integer>, Vector<Integer>, Vector<Integer>> onePointCrs = (a, b) -> {
        for (int i = rng.nextInt((a.size() - 2) + 1) + 2; i < a.size(); i++) {
            a.set(i, b.get(i));
        }
        return a;
    };

    // Find optimal index or return -1
    private static BiFunction<List<PopEle<Vector<Integer>, Float>>, Integer, Integer> optFunc = (curr, generation) -> {
        for (int i = 0; i < curr.size(); i++) {
            if (curr.get(i).getFit() == 1.f) {
                return i;
            }
        }
        return -1;
    };

    // Select Func
    private static BiFunction<List<PopEle<Vector<Integer>, Float>>, Integer, Vector<Integer>> selFunc = (list, k) -> {
        PopEle<Vector<Integer>, Float> curr = list.get(rng.nextInt(list.size()));
        for (int i = 1; i < k; i++) {
            PopEle<Vector<Integer>, Float> nxt = list.get(rng.nextInt(list.size()));
            if (nxt.getFit() > curr.getFit()) curr = nxt;
        }
        return curr.getEle();
    };


    private static Function<Vector<Integer>, Vector<Integer>> mutFunc = s -> s.stream()
            .map(x -> x ^ ((rng.nextFloat() <= 1.f / s.size()) ? 1 : 0))
            .collect(Collectors.toCollection(Vector::new));

    private static Vector<Map.Entry<String, Long>> readApplianceData() throws IOException {
        Vector<Map.Entry<String, Long>> applianceData = new Vector<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("appliances_unix.csv")))) {
            br.readLine();
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                String[] tokens = line.split(",");
                long sclWtt = (long) (Float.parseFloat(tokens[1]) * 100);
                applianceData.add(new AbstractMap.SimpleEntry<>(tokens[0], sclWtt));
            }
        }
        return applianceData;
    }

    public static void main(String[] args) throws IOException {
        if (false) {
            int numGenes = 100;
            int lenGene = 10;

            Function<Vector<Integer>, Float> fitFunc;
            fitFunc = s -> (float) s.stream().mapToInt(Integer::intValue).sum() / (float) s.size();

            // Create initial elements
            List<Vector<Integer>> init = (new ArrayList<>(
                    Collections.nCopies(numGenes, lenGene)))
                    .stream()
                    .map(i -> {
                        Vector<Integer> newV = new Vector<>();
                        newV.setSize(i);
                        return newV.stream().map(v -> rng.nextInt(2)).collect(Collectors.toCollection(Vector::new));
                    }).collect(Collectors.toList());

            GeneticAlgorithm<Vector<Integer>, Float> ga = new GeneticAlgorithm<>(fitFunc, mutFunc, onePointCrs, selFunc, optFunc);
            System.out.println(ga.loop(100, init).get(ga.getBest()).toString());
        }
        {
            // Static amount of iterations to 10 for my experiments
            int iters = 100; // Attempt count

            // Changing number of genes
            int numGenes = 4; // Number of genes
            int k = 32; // Tournament Size

            // Read data
            Vector<Map.Entry<String, Long>> applianceData = readApplianceData();

            // Fitness for appliances
            Function<Vector<Integer>, Float> appFitFunc = a -> {
                long sum = 0;
                for (int i = 0; i < a.size(); i++) {
                    sum += (a.get(i) * applianceData.get(i).getValue());
                }
                return valZ / (valZ + (long) Math.abs(valZ - sum));
            };

            // Generate an intitial pop
            List<Vector<Integer>> currPop = (new ArrayList<>(
                    Collections.nCopies(numGenes, applianceData.size()))).stream().map(x -> {
                Vector<Integer> solution = new Vector<>();
                solution.setSize(applianceData.size());
                return solution.stream().map(e -> rng.nextInt(2)).collect(Collectors.toCollection(Vector::new));
            }).collect(Collectors.toCollection(Vector::new));

            // Create object describing compute
            GeneticAlgorithm<Vector<Integer>, Float> ga;
            ga = new GeneticAlgorithm<>(appFitFunc, mutFunc, onePointCrs, selFunc, optFunc);

            // Iterate over and print the best in the pop
            System.out.println("Generations,   Time,    Best Value, Fitness");
            for (int i = 0; i < iters; i++) ga.loop(k, new ArrayList<>(currPop)); //.get(ga.getBest()).print();
        }
    }
}
