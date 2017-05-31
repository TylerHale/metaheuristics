package GeneticAlgorithm;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GeneticAlgorithm<X, F extends Comparable> {
    private Function<X, F> fitFunc;
    private Function<X, X> mutFunc;
    private BiFunction<X, X, X> crsFunc;
    private BiFunction<List<PopEle<X, F>>, Integer, X> selFunc;
    private BiFunction<List<PopEle<X, F>>, Integer, Integer> chkFunc;
    private Random rng = new Random();
    private Integer bestIdx;

    public GeneticAlgorithm(Function<X, F> fitnessFunc_,
                            Function<X, X> mutateFunc_,
                            BiFunction<X, X, X> crsFunc_,
                            BiFunction<List<PopEle<X, F>>, Integer, X> selFunc_,
                            BiFunction<List<PopEle<X, F>>, Integer, Integer> chkFunc_) {
        fitFunc = fitnessFunc_;
        mutFunc = mutateFunc_;
        crsFunc = crsFunc_;
        selFunc = selFunc_;
        chkFunc = chkFunc_;
    }

    public Integer getBest(){
        return bestIdx;
    }

    public List<PopEle<X, F>> loop(Integer k, List<X> initial) {
        List<PopEle<X, F>> currXF = new ArrayList<>();
        for (X x : initial) {
            PopEle<X, F> xfPopEle = new PopEle<>(x, fitFunc.apply(x));
            currXF.add(xfPopEle);
        }
        Integer i = 0;
        long start_time = System.currentTimeMillis();
        while ((bestIdx = chkFunc.apply(currXF, i)) == -1) {
            List<X> p = (new ArrayList<>(Collections.nCopies(currXF.size(), 1))).parallelStream()
                    .map(a -> selFunc.apply(currXF, k))
                    .collect(Collectors.toList());
            for (int n = 0; n < currXF.size(); n++) {
                X a = p.get(rng.nextInt(currXF.size()));
                X b = p.get(rng.nextInt(currXF.size()));
                X newX = mutFunc.apply(crsFunc.apply(a, b));
                currXF.set(n, new PopEle<>(newX, fitFunc.apply(newX)));
            }
            i++;
        }
        long stop_time = System.currentTimeMillis();
        System.out.println(i + ",    " + (stop_time - start_time) + ",  " + currXF.get(bestIdx).toString());
        return currXF;
    }
}
