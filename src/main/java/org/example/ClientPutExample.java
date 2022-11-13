package org.example;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

import java.util.Properties;

public class ClientPutExample {
    public static void main(String[] args) throws InterruptedException {
        ClientCache clientCache = new ClientCacheFactory(gemfireProperties()).set("cache-xml-file", "client-cache.xml").create();
        Region<Long, Long> squareRoots = clientCache.getRegion("Numbers");

        Thread thread = new Thread(new Runnable() {
            long counter = 100;

            @Override
            public void run() {
                while (true) {

                    try {
                        squareRoots.put(counter, counter);
                        System.out.println("Added : " + counter);
                        Thread.sleep(300);
                    } catch (Exception e) {
                        System.out.println(e);
                    }

                    counter++;

                    if (counter > 500) {
                        break;
                    }
                }
            }
        });

        thread.start();
        thread.join();


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
