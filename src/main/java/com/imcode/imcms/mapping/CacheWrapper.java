package com.imcode.imcms.mapping;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import java.util.function.Supplier;

public class CacheWrapper<K, V> {

    private final Ehcache cache;

    public CacheWrapper(Ehcache cache) {
        this.cache = cache;
    }

    public static <K, V> CacheWrapper<K, V> of(CacheConfiguration cacheConfiguration) {
        return new CacheWrapper<>(new Cache(cacheConfiguration));
    }

    public V get(K key) {
        Element element = cache.get(key);
        return element == null ? null : (V) element.getObjectValue();
    }

    public void put(K key, V value) {
        cache.put(new Element(key, value));
    }

    public boolean remove(K key) {
        return cache.remove(key);
    }

    public V getOrPut(K key, Supplier<V> valueSupplier) {
        V value = get(key);
        if (value == null) {
            value = valueSupplier.get();
            put(key, value);
        }

        return value;
    }

    public Ehcache cache() {
        return cache;
    }
}
