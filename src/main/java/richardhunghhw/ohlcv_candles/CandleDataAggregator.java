package richardhunghhw.ohlcv_candles;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import richardhunghhw.ohlcv_candles.models.BookData;
import richardhunghhw.ohlcv_candles.models.Candle;
import richardhunghhw.ohlcv_candles.services.CandleListenerService;

/**
 * Aggregates book data and notifies listeners with aggregated candle data.
 * Triggered by the AggregatorScheduler, which runs the aggregateAndNotify function on an interval.
 * Specifically, every minute.
 */
public class CandleDataAggregator {
    private final List<CandleListenerService> listeners; // List of listeners to notify
    private final Queue<BookData> data; // Book data to be aggregated

    private long prevCandleTimestamp = 0; // Tracks the timestamp of the latest published candle
    private final long period = 60000; // 1 minute in milliseconds

    public CandleDataAggregator() {
        this.listeners = new ArrayList<>();
        this.data = new ConcurrentLinkedQueue<>();
    }
    
    /**
     * Adds book data to the aggregator.
     * @param bookData
     */
    public void addBookData(BookData bookData) {
        // System.out.println("CandleDataAggregator.addBookData: " + bookData.toString());
        this.data.add(bookData);
    }

    public void addListener(CandleListenerService listener) {
        listeners.add(listener);
    }

    public void removeListener(CandleListenerService listener) {
        listeners.remove(listener);
    }

    /**
     * Aggregates book data and notifies listeners with aggregated candle data.
     */
    public void aggregateAndNotify() {
        if (this.prevCandleTimestamp == 0) {
            this.data.clear();

            // Set prevCandleTimestamp to the current time rounded down to the nearest period
            // The first candle will be prevCandleTimestamp + period, the next one
            this.prevCandleTimestamp = System.currentTimeMillis() / period * period;

            // System.out.println("CandleDataAggregator.aggregateAndNotify: Discarded first candle, prevCandleTimestamp: " + this.prevCandleTimestamp);
            return;
        }

        // The next candle starts at the end of the previous candle
        long start = this.prevCandleTimestamp + period;
        long end = start + period;

        // The last candle that can be pulished is before the current time rounded down to the nearest period
        long lastCandle = System.currentTimeMillis() / period * period;

        // System.out.println("CandleDataAggregator.aggregateAndNotify: Aggregating candles till " + lastCandle + " with " + this.data.size() + " book data");
        aggregateCandles(start, end, lastCandle);
    }

    private void aggregateCandles(long start, long end, long lastCandle) {
        // Aggregate book data, creates candles from the preivousCandleTimestamp to the lastCandle
        // System.out.println("CandleDataAggregator.aggregateCandles: Aggregating candles from " + start + " to " + end);
        Candle candle = new Candle(start);
        BookData bookData = this.data.peek();
        while (!data.isEmpty()) {
            // covert localdatetime to epoch timestamp
            long timestamp = Timestamp.valueOf(bookData.getTimestamp()).getTime();

            // This book data is for a future candle not to be computed, stop
            if (timestamp >= lastCandle) {
                // System.out.println("CandleDataAggregator.aggregateCandles: Book data is for a future candle. timestamp: " + timestamp + ", lastCandle: " + lastCandle);
                break;
            }
            // This book data is not for a future candle
            this.data.poll(); 
            
            if (timestamp < start) {
                // The data is for a previous (already published) candle, skip
                // System.out.println("CandleDataAggregator.aggregateCandles: Skipped book data: " + bookData.toString() + ", start: " + start);
            } else {
                // System.out.println("CandleDataAggregator.aggregateCandles: Processing book data: " + bookData.toString());
                if (timestamp >= end) {
                    // The data is for the next candle, notify previous candle and start a new candle
                    notifyListeners(candle);
                    start = end;
                    end = start + period;
                    candle = new Candle(start);
                } else {
                    // Add the price to the candle
                    candle.addPrice(bookData.getMidPrice());
                }
            }
            bookData = this.data.peek();
        }

        // Notify the last candle
        notifyListeners(candle);
        this.prevCandleTimestamp = lastCandle - period;
    }

    private void notifyListeners(Candle candle) {
        if (candle.getTicks() == 0) {
            // System.err.println("CandleDataAggregator.notifyListeners: Candle has no ticks, not notifying listeners");
            return;
        }
        // System.out.println("CandleDataAggregator.notifyListeners: Notifying listeners with candle: " + candle.toString());
        for (CandleListenerService listener : listeners) {
            listener.onCandle(candle);
        }
    }
}
