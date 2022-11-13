package org.example;

import com.google.common.base.Stopwatch;
import org.apache.geode.cache.Operation;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.query.*;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ClientContReadExample {
    public static void main(String[] args) {

        ClientContReadExample o = new ClientContReadExample();
        try {
            o.init();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void init() throws CqException, CqExistsException, RegionNotFoundException, InterruptedException {
        ClientCache clientCache = new ClientCacheFactory(gemfireProperties()).set("cache-xml-file", "client-cache.xml").create();
        Region<Long, Long> region = clientCache.getRegion("Numbers");

        CqAttributesFactory cqf = new CqAttributesFactory();
        cqf.addCqListener(new ClientContReadExample.RandomEventListener());
        CqAttributes cqa = cqf.create();

        String cqName = "randomTracker";

        String queryStr = "SELECT * FROM /Numbers i where i > 100";

        QueryService queryService = region.getRegionService().getQueryService();
        CqQuery randomTracker = queryService.newCq(cqName, queryStr, cqa);
        randomTracker.execute();


        wait10();
    }

    private void wait10() {
        Stopwatch stopWatch = Stopwatch.createStarted();
        while (stopWatch.elapsed(TimeUnit.MINUTES) < 10) {}
        stopWatch.stop();
    }

    private static Properties gemfireProperties() {

        Properties gemfireProperties = new Properties();
        gemfireProperties.setProperty("log-level", System.getProperty("gemfire.log.level", "config"));
        return gemfireProperties;
    }

    class RandomEventListener implements CqListener {
        @Override
        public void onEvent(CqEvent cqEvent) {

            Operation queryOperation = cqEvent.getQueryOperation();

            System.out.println("Found : " + cqEvent.getNewValue());
        }

        @Override
        public void onError(CqEvent cqEvent) {
            System.out.print("**Something bad happened**");
        }

        @Override
        public void close() {

        }
    }
}
