package richardhunghhw.ohlcv_candles.services;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import richardhunghhw.ohlcv_candles.models.Candle;

public class InMemoryCandleStoreTest {
    @Test
    public void testAddCandle() {
        Candle candle = new Candle(1, 1.0, 2.0, 0.5, 1.5, 10);

        InMemoryCandleStore store = new InMemoryCandleStore();
        
        store.onCandle(candle);

        assertEquals(1, store.getCandles().size());
    }

    @Test
    public void testAddCandleOverflow() {
        Candle candle = new Candle(1, 1.0, 2.0, 0.5, 1.5, 10);

        InMemoryCandleStore.STORE_SIZE = 2;
        InMemoryCandleStore store = new InMemoryCandleStore();

        store.onCandle(candle);
        store.onCandle(candle);
        assertEquals(2, store.getCandles().size());

        store.onCandle(candle);
        assertEquals(2, store.getCandles().size());
    }
}
