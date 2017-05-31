package studio2;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SimulatedAnnealing<X, S> {
    private Function<X, Float> costFunc;
    private Function<S, X> newFunc;
    private BiFunction<X, Float, X> mutateFunc;
    private BiFunction<Float, Float, Float> scheduleFunc;

    public SimulatedAnnealing(Function<X, Float> costFunc_,
                              Function<S, X> newFunc_,
                              BiFunction<X, Float, X> mutateFunc_,
                              BiFunction<Float, Float, Float> scheduleFunc_) {
        costFunc = costFunc_;
        newFunc = newFunc_;
        mutateFunc = mutateFunc_;
        scheduleFunc = scheduleFunc_;
    }

    public X search(S startSeed_,
                    int nrepsPerTemp_,
                    Float minTemp_,
                    Float mutationRate_,
                    Float cRate_) {

        X currX = newFunc.apply(startSeed_);
        Float currC = costFunc.apply(currX);
        Float temp = 1.0f;
        while (temp > minTemp_) {
            for (int i = 0; i < nrepsPerTemp_; i++) {
                X newX = mutateFunc.apply(currX, mutationRate_);
                Float newC = costFunc.apply(newX);
                if (newC < currC || Math.exp(-(newC - currC) / temp) > Math.random()) {
//                    System.out.println(newC);
                    currX = newX;
                    currC = newC;
                }
            }
            temp = scheduleFunc.apply(temp, cRate_);
        }
        return currX;
    }
}
