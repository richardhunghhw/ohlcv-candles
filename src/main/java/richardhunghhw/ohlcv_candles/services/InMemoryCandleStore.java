package richardhunghhw.ohlcv_candles.services;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import richardhunghhw.ohlcv_candles.models.Candle;

/**
 * A store that holds the last STORE_SIZE candles.
 * The store is implemented as a deque, where the oldest candles are removed when the store size exceeds STORE_SIZE.
 */
public class InMemoryCandleStore implements CandleListenerService {
    public static int STORE_SIZE = 200;

    private final Deque<Candle> store;

    public InMemoryCandleStore() {
        store = new ArrayDeque<>();
    }
    
    /**
     * Adds a candle to the store. If the store size exceeds STORE_SIZE candles, the oldest candles are removed.
     * @param candle
     */
    @Override
    public void onCandle(Candle candle) {
        if (candle == null) {
            return;
        }

        store.addLast(candle);
        removeOldCandles();
        
        System.out.println("InMemoryCandleStore.onCandle: " + candle.toString() + ", store size: " + store.size());
    }

    /**
     * Removes candles from the store that are older than the last STORE_SIZE candles.
     */
    private void removeOldCandles() {
        while (store.size() > STORE_SIZE) {
            store.removeFirst();
        }
    }

    /**
     * Returns a list of candles in the store.
     * @return
     */
    public List<Candle> getCandles() {
        return List.copyOf(store);
    }
}
