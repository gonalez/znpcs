package io.github.znetworkw.znpcservers.utility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.function.Function;
import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toMap;

/**
 * Add missing collectors for guava library
 * for versions 1.8 & 1.9 since these are out date.
 */
public final class GuavaCollectors {
    public static <E> Collector<E, ?, ImmutableList<E>> toImmutableList() {
        return collectingAndThen(toList(), ImmutableList::copyOf);
    }

    public static <E> Collector<E, ?, ImmutableSet<E>> toImmutableSet() {
        return collectingAndThen(toSet(), ImmutableSet::copyOf);
    }

    public static <T, K, V> Collector<T, ?, ImmutableMap<K, V>> toImmutableMap(
        Function<? super T, ? extends K> keyFunction,
        Function<? super T, ? extends V> valueFunction) {
        return collectingAndThen(toMap(keyFunction, valueFunction), ImmutableMap::copyOf);
    }

    private GuavaCollectors() {}
}
