package at.pcgamingfreaks.mkvaudiosubtitlechanger.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<Key, Value> {
    private final Map<Key, Value> cache = new HashMap<>();

    /**
     * Retrieve {@link Value} from Cache or run creationFunction and return its value.
     * @param key key of cache map
     * @param creationFunction function to create missing values
     * @return {@link  Value} from Cache, or if missing result from creationFunction.
     */
    public synchronized Value retrieve(Key key, Function<Key, Value> creationFunction) {
        return cache.computeIfAbsent(key, creationFunction::apply);
    }
}
