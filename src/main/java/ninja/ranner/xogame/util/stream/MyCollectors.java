package ninja.ranner.xogame.util.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class MyCollectors {
    public static <T> Collector<T, List<T>, Optional<T>> findFirstBy(Predicate<T> predicate) {
        return new FindFirstBy<>(predicate);
    }

    static class FindFirstBy<T> implements Collector<T, List<T>, Optional<T>> {

        private final Predicate<T> predicate;

        public FindFirstBy(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public Supplier<List<T>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<T>, T> accumulator() {
            return (list, item) -> {
                if (predicate.test(item)) {
                    list.add(item);
                }
            };
        }

        @Override
        public BinaryOperator<List<T>> combiner() {
            return (a, b) -> {
                ArrayList<T> c = new ArrayList<>();
                c.addAll(a);
                c.addAll(b);
                return c;
            };
        }

        @Override
        public Function<List<T>, Optional<T>> finisher() {
            return (list) -> list.stream().findFirst();
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Set.of();
        }
    }
}
