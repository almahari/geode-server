package org.example;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;

import java.util.*;
import java.util.stream.IntStream;

public class ClientExample {

    public static void main(String[] args) {
        ClientCache  clientCache = new ClientCacheFactory(gemfireProperties()).set("cache-xml-file", "client-cache.xml").create();
        Region<Long, Long>  squareRoots = clientCache.getRegion("SquareRoots");


        System.out.println(squareRoots.get(100L));
        System.out.println(squareRoots.get(81L));


        if (clientCache != null) {
            clientCache.close(false);
        }
    }

    private static Properties gemfireProperties() {

        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"));
        return gemfireProperties;
    }
}
