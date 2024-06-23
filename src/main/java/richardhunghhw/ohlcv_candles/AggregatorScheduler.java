package richardhunghhw.ohlcv_candles;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduler for the aggregator.
 * While the scheduler triggers the aggregator on a fixed rate, the aggregator will only publish 
 * candles when it has enough data (full range of data for the candle period)
 * So this scheduler does not have to run at the same rate as the candle period.
 */
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
        scheduler.scheduleAtFixedRate(aggregator::aggregateAndNotify, 0, 60, TimeUnit.SECONDS);
    }

    /** 
     * Stops the scheduler.
     */
    public void stop() {
        scheduler.shutdown();
    }
}