package StochasticHillClimb;

import java.util.function.Function;

public class StochasticHillClimb<X, F extends Comparable, S> {
    private Function<X, F> fitnessFunc;
    private Function<S, X> newFunc;
    private Function<X, X> mutateFunc;

    public StochasticHillClimb(Function<X, F> fitnessFunc_,
                               Function<S, X> newFunc_,
                               Function<X, X> mutateFunc_) {
        fitnessFunc = fitnessFunc_;
        newFunc = newFunc_;
        mutateFunc = mutateFunc_;
    }

    public X climb(int iter, S s) {
        X bestX = newFunc.apply(s);
        F bestF = fitnessFunc.apply(bestX);
        for (int i = 0; i < iter; i++) {
            X tempX = mutateFunc.apply(bestX);
            F tempF = fitnessFunc.apply(tempX);
            if (tempF.compareTo(bestF) >= 0) {
                bestX = tempX;
                bestF = tempF;
            }
        }
        return bestX;
    }
}
