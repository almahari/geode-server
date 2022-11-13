package org.example;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

public class PutAllExample {
    private final Region<Integer, String> region;

    public PutAllExample(Region<Integer, String> region) {
        this.region = region;
    }

    public static void main(String[] args) {
        // connect to the locator using default port 10334
        ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
                .set("log-level", "WARN").create();

        // create a local region that matches the server region
        Region<Integer, String> region =
                cache.<Integer, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
                        .create("example-region");

        PutAllExample example = new PutAllExample(region);
        example.insertValues(10);
        example.printValues(example.getValues());

        cache.close();
    }

    Set<Integer> getValues() {
        return new HashSet<>(region.keySetOnServer());
    }

    void insertValues(int upperLimit) {
        Map values = new HashMap<Integer, String>();
        IntStream.rangeClosed(1, upperLimit).forEach(i -> values.put(i, "value" + i));
        region.putAll(values);
    }

    void printValues(Set<Integer> values) {
        values.forEach(key -> System.out.println(String.format("%d:%s", key, region.get(key))));
    }
}