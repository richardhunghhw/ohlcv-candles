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

    private boolean discardFirstCandle = true; // Discard the first candle after the socket opens
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
        // Add book data to map
        data.add(bookData);
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
        if (discardFirstCandle) {
            // Discard the first candle after the socket opens
            discardFirstCandle = false;
            data.clear();

            // Set prevCandleTimestamp to the current time rounded down to the nearest period
            prevCandleTimestamp = System.currentTimeMillis() / period * period;

            System.out.println("CandleDataAggregator.aggregateAndNotify: Discarded first candle, prevCandleTimestamp: " + prevCandleTimestamp);
            return;
        }

        // The next candle starts at the end of the previous candle
        long start = prevCandleTimestamp + period;
        long end = start + period;

        // The last candle that can be pulished is before the current time rounded down to the nearest period
        long lastCandle = System.currentTimeMillis() / period * period;

        System.out.println("CandleDataAggregator.aggregateAndNotify: Aggregating candles from " + start + ":" + end + " to " + lastCandle + " with " + data.size() + " book data");

        // Aggregate book data, creates candles from the preivousCandleTimestamp to the lastCandle
        Candle candle = new Candle(start);
        BookData bookData = data.peek();
        while (!data.isEmpty()) {
            // covert localdatetime to epoch timestamp
            long timestamp = Timestamp.valueOf(bookData.getTimestamp()).getTime();

            // This book data is for a future candle, stop
            if (timestamp >= lastCandle) {
                System.out.println("CandleDataAggregator.aggregateAndNotify: Book data is for a future candle. timestamp: " + timestamp + ", lastCandle: " + lastCandle);
                return;
            }
            // This book data is not for a future candle
            data.poll(); 
            
            if (timestamp < start) {
                // The data is for a previous (already published) candle, skip
                // System.err.println("CandleDataAggregator.aggregateAndNotify: Skipped book data: " + bookData.toString());
            } else {
                if (timestamp >= end) {
                    // The data is for the next candle, notify previous candle and start a new candle
                    notifyListeners(candle);
                    candle = new Candle(start);
                    start = end;
                    end = start + period;
                } else {
                    // Add the price to the candle
                    candle.addPrice(bookData.getMidPrice());
                }
            }
            bookData = data.peek();
        }

        // Notify the last candle
        notifyListeners(candle);
    }

    private void notifyListeners(Candle candle) {
        for (CandleListenerService listener : listeners) {
            listener.onCandle(candle);
        }
    }
}
