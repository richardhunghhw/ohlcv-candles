package richardhunghhw.ohlcv_candles;

import richardhunghhw.ohlcv_candles.exchanges.Kraken;
import richardhunghhw.ohlcv_candles.models.TimeProvider;
import richardhunghhw.ohlcv_candles.services.InMemoryCandleStore;
import richardhunghhw.ohlcv_candles.services.KafkaCandleDispatcher;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Starting OHLCV Candles App" );

        // Setup Candle Event Services, these are the listeners that will receive the candles from the aggregator
        InMemoryCandleStore memoryStore = new InMemoryCandleStore();
        KafkaCandleDispatcher kafkaDispatcher = new KafkaCandleDispatcher();

        // Create a new PairCandleAggregator and attach the listeners, the aggregator will notify the listeners when a new candle is ready
        TimeProvider timeProvider = new TimeProvider();
        CandleDataAggregator aggregator = new CandleDataAggregator(timeProvider);
        aggregator.addListener(memoryStore);
        aggregator.addListener(kafkaDispatcher);

        // Create a new Kraken exchange and connect to it, providing the aggregator which will receive the book data
        Kraken kraken = new Kraken(aggregator);
        kraken.connect();

        // Start the scheduler which will trigger the aggregateAndNotify function on the aggregator on an interval
        AggregatorScheduler scheduler = new AggregatorScheduler(aggregator);
        scheduler.start();

        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(scheduler::stop));
    }
}
