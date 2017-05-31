import studio2.NineQueen;
import studio2.SimulatedAnnealing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Studio2 {

    public static void main(String[] args) {

        Random rng = new Random();

        // Helper Functions and settings
        Float A = 10.0f;
        Float range = 5.12f;
        Function<Float, Float> ranF = (a) -> rng.nextFloat() * (a - -a) + -a;
        Function<Integer, List<Float>> newF = (s) -> (new ArrayList<>(Collections.nCopies(s, range))).stream().map(ranF).collect(Collectors.toList());
        BiFunction<List<Float>, Float, List<Float>> mutF = (l, r) -> l.stream().map((Float a) -> (a + (float) (rng.nextGaussian() * r)) % range).collect(Collectors.toList());

        // scheduleFunc_ 's
        BiFunction<Float, Float, Float> geomeF = (t, a) -> a * t;
        BiFunction<Float, Float, Float> lundyF = (t, b) -> t / (1 + (t * b));

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        // RastriginF part
        Function<List<Float>, Float> rastriginF = x -> A * x.size() +
                new Float(x.stream()
                        .map(v -> Math.pow(v, 2) - (A * Math.cos(2 * Math.PI * v)))
                        .reduce((l, r) -> l + r).get());

        /* Notes
            Lundy takes longer but gets to a decent solution much faster
            Lundy bounces at the solution from my initial attempts
            My Mutate funtion was off for some period of time
            I iterated over input variables to get a relatively good mutation rate
            nrepsPerTemp were scaled to suitable execution times
            cRate heading towards 1 for geo and 0 for lundy
        */
        // Lun 2
        System.out.println((new SimulatedAnnealing<>(rastriginF, newF, mutF, lundyF))
                .search(2, 1000, 0.01f, 0.45f, 0.01f));
        // Geo 2
        System.out.println((new SimulatedAnnealing<>(rastriginF, newF, mutF, geomeF))
                .search(2, 1000, 0.01f, 0.45f, 0.9999f));
        // Lun 7
        System.out.println((new SimulatedAnnealing<>(rastriginF, newF, mutF, lundyF))
                .search(7, 50, 0.01f, 0.45f, 0.01f));
        // Geo 7
        System.out.println((new SimulatedAnnealing<>(rastriginF, newF, mutF, geomeF))
                .search(7, 50, 0.01f, 0.45f, 0.9999f));

        // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

        // 0 = No queens colliding in move paths, >0 Queens need corrections
        Function<NineQueen, Float> queensCost = (a) -> {
            List<Integer> queenPositions = a.posS;
            int cost = 0;
            int n = queenPositions.size();
            for (int i = 0; i < n; i++) {
                int cQueenPos = queenPositions.get(i);
                int cRow = cQueenPos / n;
                int cCol = cQueenPos % n;
                for (int j = 0; j < n; j++) {
                    if (i != j) {
                        int oQueenPos = queenPositions.get(j);
                        int oRow = oQueenPos / n;
                        int oCol = oQueenPos % n;
                        if (cRow == oRow) cost++;
                        if (cCol == oCol) cost++;
                        if (Math.abs(cRow - oRow) == Math.abs(cCol - oCol)) cost++;
                    }
                }
            }
            return new Float(cost);
        };

        // Grab NineQueen place somewhere that is free
        BiFunction<NineQueen, Float, NineQueen> queenMutate = (x, rate) -> {
            NineQueen mQueen = x.copy();
            int siz = mQueen.size();
            int queen = rng.nextInt(siz);
            int oPos = mQueen.posS.get(queen);
            int nPos;
            while (mQueen.board[nPos = rng.nextInt(siz * siz)] != 0) ;
            mQueen.board[oPos] = 0;
            mQueen.board[nPos] = 1;
            mQueen.posS.set(queen, nPos);
            return mQueen;
        };

        Function<Integer, NineQueen> newQueen = n -> new NineQueen(n);

        /*
            Initial implementation my cost was done using a stream but that had errors for 50x grid
            I use my generic SimulatedAnnealing class
            I ignore the mutation rate
            cRate and minTemp are minimised
         */
        ((new SimulatedAnnealing<>(queensCost, newQueen, queenMutate, lundyF))
                .search(10, 100, 0.01f, 0.f, 0.01f)).print();
    }
}
