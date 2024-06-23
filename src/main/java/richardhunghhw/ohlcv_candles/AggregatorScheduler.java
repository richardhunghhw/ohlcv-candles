package richardhunghhw.ohlcv_candles;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AggregatorScheduler {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final CandleDataAggregator aggregator;

    public AggregatorScheduler(CandleDataAggregator aggregator) {
        this.aggregator = aggregator;
    }

    /**
     * Starts the scheduler to run the aggregator every minute.
     */
    public void start() {
        scheduler.scheduleAtFixedRate(aggregator::aggregateAndNotify, 0, 1, TimeUnit.MINUTES);
    }

    /** 
     * Stops the scheduler.
     */
    public void stop() {
        scheduler.shutdown();
    }
}