package org.example;

import org.apache.geode.cache.Operation;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

public class CQExample {
    private ClientCache cache;
    private Region<Integer, Integer> region;
    private CqQuery randomTracker;

    private void init() throws CqException, RegionNotFoundException, CqExistsException {
        // init cache, region, and CQ

        // connect to the locator using default port 10334
        this.cache = connectToLocallyRunningGeode();


        // create a local region that matches the server region
        this.region = cache.<Integer, Integer>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create("example-region");

        this.randomTracker = this.startCQ(this.cache, this.region);
    }

    private void run() throws InterruptedException {

        this.startPuttingData(this.region);

    }

    private void close() throws CqException {

        // close the CQ and Cache
        this.randomTracker.close();
        this.cache.close();

    }


    public static void main(String[] args) throws Exception {

        CQExample mExample = new CQExample();

        mExample.init();

        mExample.run();

        mExample.close();

        System.out.println("\n---- So that is CQ's----\n");

    }

    private CqQuery startCQ(ClientCache cache, Region region)
            throws CqException, RegionNotFoundException, CqExistsException {
        // Get cache and queryService - refs to local cache and QueryService

        CqAttributesFactory cqf = new CqAttributesFactory();
        cqf.addCqListener(new RandomEventListener());
        CqAttributes cqa = cqf.create();

        String cqName = "randomTracker";

        String queryStr = "SELECT * FROM /example-region i where i > 70";

        QueryService queryService = region.getRegionService().getQueryService();
        CqQuery randomTracker = queryService.newCq(cqName, queryStr, cqa);
        randomTracker.execute();


        System.out.println("------- CQ is running\n");

        return randomTracker;
    }

    private void startPuttingData(Region region) throws InterruptedException {

        // Example will run for 20 second

        Stopwatch stopWatch = Stopwatch.createStarted();

        while (stopWatch.elapsed(TimeUnit.SECONDS) < 20) {

            // 500ms delay to make this easier to follow
            Thread.sleep(500);
            int randomKey = ThreadLocalRandom.current().nextInt(0, 99 + 1);
            int randomValue = ThreadLocalRandom.current().nextInt(0, 100 + 1);
            region.put(randomKey, randomValue);
            System.out.println("Key: " + randomKey + "     Value: " + randomValue);

        }

        stopWatch.stop();

    }

    private ClientCache connectToLocallyRunningGeode() {

        ClientCache cache = new ClientCacheFactory().addPoolLocator("127.0.0.1", 10334)
                .setPoolSubscriptionEnabled(true).set("log-level", "WARN").create();

        return cache;
    }


    class RandomEventListener implements CqListener {
        @Override
        public void onEvent(CqEvent cqEvent) {

            Operation queryOperation = cqEvent.getQueryOperation();


            if (queryOperation.isUpdate()) {
                System.out.print("-------Updated Value\n");
                System.out.println(cqEvent.getNewValue());
            } else if (queryOperation.isCreate()) {
                System.out.print("-------Value Created\n");
                System.out.println(cqEvent.getNewValue());
            }
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
