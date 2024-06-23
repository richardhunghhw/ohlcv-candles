package richardhunghhw.ohlcv_candles.services;

import richardhunghhw.ohlcv_candles.models.Candle;

public class KafkaCandleDispatcher implements CandleListenerService {
    /**
     * Adds a candle to the store. If the store size exceeds STORE_SIZE candles, the oldest candles are removed.
     * @param candle
     */
    @Override
    public void onCandle(Candle candle) {
        System.out.println("KafkaCandleDispatcher.onCandle: " + candle.toString());
    }
}
