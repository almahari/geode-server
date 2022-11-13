package org.example;

import org.apache.geode.cache.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ServerExample {

    public static final String DEFAULT_MAX_CONNECTIONS = "100";
    public static final String DEFAULT_MAX_TIME_BETWEEN_PINGS = "60000";

    static {
        System.setProperty("BIND_ADDRESS", systemProperty("GEODE_BIND_ADDRESS", "localhost"));
        System.setProperty("HOSTNAME_FOR_CLIENTS", systemProperty("GEODE_HOSTNAME_FOR_CLIENTS", "localhost"));
        System.setProperty("PORT", systemProperty("GEODE_PORT", "12480"));
        System.setProperty("MAX_CONNECTIONS", DEFAULT_MAX_CONNECTIONS);
        System.setProperty("MAX_TIME_BETWEEN_PINGS", DEFAULT_MAX_TIME_BETWEEN_PINGS);
        System.setProperty("REMOTE_DISTRIBUTED_SYSTEM_ID", systemProperty("REMOTE_DISTRIBUTED_SYSTEM_ID", "2"));
        System.setProperty("GATEWAY_RECEIVER_START_PORT", systemProperty("GATEWAY_RECEIVER_START_PORT", "41000"));
        System.setProperty("GATEWAY_RECEIVER_END_PORT", systemProperty("GATEWAY_RECEIVER_END_PORT", "41005"));
        System.setProperty("HOSTNAME_FOR_SENDERS", systemProperty("HOSTNAME_FOR_SENDERS", "localhost"));
    }

    public static void main(String[] args) {
        registerShutdownHook(gemfireCache(gemfireProperties()));
    }

    static Properties gemfireProperties() {
        Properties gemfireProperties = new Properties();

        gemfireProperties.setProperty("name", systemProperty("GEODE_LOCATOR_NAME", "locator1"));
        gemfireProperties.setProperty("mcast-port", "0");
        gemfireProperties.setProperty("log-level", "config");
//        gemfireProperties.setProperty("locators", systemProperty("GEODE_PORT_LOCATORS", "localhost[11235]"));
        gemfireProperties.setProperty("start-locator", systemProperty("GEODE_START_LOCATORS", "localhost[11235]"));
        gemfireProperties.setProperty("jmx-manager", "true");
        gemfireProperties.setProperty("jmx-manager-port", systemProperty("JMX_MANAGER_PORT", "1199"));
        gemfireProperties.setProperty("jmx-manager-hostname-for-clients", systemProperty("JMX_MANAGER_HOSTNAME_FOR_CLIENTS", "localhost"));
        gemfireProperties.setProperty("jmx-manager-start", "true");
        gemfireProperties.setProperty("enable-cluster-configuration", systemProperty("ENABLE_CLUSTER_CONFIGURATION", "false"));
        gemfireProperties.setProperty("remote-locators", systemProperty("REMOTE_LOCATORS", "localhost[11235]"));
        gemfireProperties.setProperty("distributed-system-id", systemProperty("DISTRIBUTED_SYSTEM_ID", "1"));


        return gemfireProperties;
    }

    static String systemProperty(String propertyName, String defaultValue) {
        String propertyValue = System.getProperty(propertyName, System.getenv(propertyName));
        System.out.println("Prop: [" + propertyName + "]: " + propertyValue);
        return (StringUtils.hasText(propertyValue) ? propertyValue : defaultValue);
    }

    static Cache gemfireCache(Properties gemfireProperties) {
        return new CacheFactory(gemfireProperties).set("cache-xml-file", "server-cache.xml").create();
    }

    static void registerShutdownHook(Cache gemfireCache) {
        Runtime.getRuntime().addShutdownHook(new Thread(gemfireCache::close, "GemFire Server Shutdown Thread"));
    }

    public static class SquareRootsCacheLoader implements CacheLoader<Long, Long>, Declarable {

        public Long load(final LoaderHelper<Long, Long> helper) throws CacheLoaderException {
            long number = helper.getKey();
            return new Double(Math.sqrt(number)).longValue();
        }

        public void init(final Properties props) {
        }

        public void close() {
        }
    }

    public static class ExampleCacheWriter implements CacheWriter<Long, Long> {

        @Override
        public void beforeUpdate(EntryEvent<Long, Long> event) throws CacheWriterException {
            if (event.getKey() % 2 == 1) {
                throw new CacheWriterException("Invalid number");
            }
        }

        @Override
        public void beforeCreate(EntryEvent<Long, Long> event) throws CacheWriterException {
            if (event.getKey() % 2 == 1) {
                throw new CacheWriterException("Invalid number");
            }
        }

        @Override
        public void beforeDestroy(EntryEvent<Long, Long> event) throws CacheWriterException {
            // N/A
        }

        @Override
        public void beforeRegionDestroy(RegionEvent<Long, Long> event) throws CacheWriterException {
            // N/A
        }

        @Override
        public void beforeRegionClear(RegionEvent<Long, Long> event) throws CacheWriterException {
            // N/A
        }

        @Override
        public void close() {
            // N/A
        }
    }

    public static class NumbersLoader implements CacheLoader<Long, Long> {

        private final Map<Long, Long> numbers;

        public NumbersLoader() {
            numbers = new HashMap<>();
            for (long i = 0; i < 100; i++) {
                numbers.put(i, i);
            }
        }

        @Override
        public void initialize(Cache cache, Properties properties) {
            System.out.println("initialize " + cache.getName());
        }

        @Override
        public Long load(LoaderHelper<Long, Long> loaderHelper) throws CacheLoaderException {
            System.out.println("Load Data for {" + loaderHelper.getKey() + "} ...");

            return numbers.containsKey(loaderHelper.getKey()) ? numbers.get(loaderHelper.getKey()) : null;
        }
    }
}
