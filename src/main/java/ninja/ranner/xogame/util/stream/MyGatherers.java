package ninja.ranner.xogame.util.stream;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Gatherer;

public class MyGatherers {

    public static <T> Gatherer<T, Map<T, Long>, Map.Entry<T, Long>> count() {
        return new CountGatherer<>();
    }

    public static <T> Gatherer<Optional<T>, Optional<T>, T> filterPresent() {
        return new FilterPresent<>();
    }

    static class CountGatherer<T> implements Gatherer<T, Map<T, Long>, Map.Entry<T, Long>> {

        @Override
        public Supplier<Map<T, Long>> initializer() {
            return HashMap::new;
        }

        @Override
        public Integrator<Map<T, Long>, T, Map.Entry<T, Long>> integrator() {
            return Integrator.ofGreedy((state, item, _) -> {
                if (item == null) {
                    return true;
                }
                state.compute(item, (_, oldValue) -> oldValue == null ? 1L : oldValue + 1);
                return true;
            });
        }

        @Override
        public BiConsumer<Map<T, Long>, Downstream<? super Map.Entry<T, Long>>> finisher() {
            return (state, downstream) -> state.entrySet().forEach(downstream::push);
        }

    }

    static class FilterPresent<T> implements Gatherer<Optional<T>, Optional<T>, T> {
        @Override
        public Integrator<Optional<T>, Optional<T>, T> integrator() {
            return Integrator.ofGreedy((_, item, downstream) -> {
                item.ifPresent(downstream::push);
                return true;
            });
        }
    }
}
