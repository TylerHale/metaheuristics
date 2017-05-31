import studio1.StochasticHillClimb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Studio1 {
    private final Random __rng = new Random();
    private final Function<Double, Double> __ranF = (a) -> __rng.nextDouble() * (a - -a) + -a;
    private final double __range = 5.0f;
    private final Function<Integer, List<Double>> __newF = (s) -> (new ArrayList<>(
            Collections.nCopies(s, __range))).stream().map(__ranF)
            .collect(Collectors.toList());

    public static void main(String[] args) {
        Studio1 m = new Studio1();
        m.sphere(100, 2);
        m.sphere(500, 2);
        m.sphere(1000, 2);
        m.rosenbrook(100, 3);
        m.rosenbrook(500, 3);
        m.rosenbrook(1000, 3);
        m.rosenbrook(100, 7);
        m.rosenbrook(500, 7);
        m.rosenbrook(1000, 7);
    }

    private float __euc(List<Double> x, List<Double> optima) {
        double sum = 0.f;
        for (int i = 0; i < x.size(); i++) {
            sum += Math.pow((x.get(i) - optima.get(i)), 2);
        }
        return (float) Math.sqrt(sum);
    }

    public void sphere(int iter, int n) {
        // Fitness Function
        Function<Double, Double> costSF = a -> Math.pow(a, 2);
        Function<List<Double>, Double> fitSF = i -> 1 / (1 + (i.stream().map(costSF).reduce((a, b) -> a + b).get()));
        // Mutate Function
        Function<List<Double>, List<Double>> mutSF = i -> i.stream().map(
                (Double a) -> (a + (float) (__rng.nextGaussian() * 0.025)) % __range)
                .collect(Collectors.toList());
        System.out.print("Sphere: " + n + "\n" + iter + "\n" + (new StochasticHillClimb<>(fitSF, __newF, mutSF)).climb(iter, n) + "\n");
//        List<Double> optima = new ArrayList<>(Collections.nCopies(n, 0.0d));
//        for (int i = 0; i < 100 ; i ++ ) {
//            List<Double> cur = (new ArrayList<>(Collections.nCopies(n, 2.0d)))
//                    .stream().map(ranF).collect(Collectors.toList());
//            System.out.print(fitF.apply(cur));
//            System.out.print(",");
//            System.out.println(euc(cur,optima));
//        }
    }

    public void rosenbrook(int iter, int n) {
        // Fitness Function
        Function<Double, Double> costRF = a -> 100 * Math.pow((a + 1 - Math.pow(a, 2)), 2) + Math.pow((a - 1), 2);
        Function<List<Double>, Double> fitRF = i -> 1 / (1 + (i.stream().limit(n - 1).map(costRF).reduce((a, b) -> a + b).get()));
        // Mutate Function
        Function<List<Double>, List<Double>> mutRF = i -> i.stream().map(
                (Double a) -> (a + (float) (__rng.nextGaussian() * 0.025)) % __range)
                .collect(Collectors.toList());
        System.out.print("Rosenbrook: " + n + "\n" + iter + "\n" + (new StochasticHillClimb<>(fitRF, __newF, mutRF)).climb(iter, n) + "\n");
    }
}
