package richardhunghhw.ohlcv_candles.services;

import richardhunghhw.ohlcv_candles.models.Candle;

@FunctionalInterface
public interface CandleListenerService {
    /**
     * An interface for a service that listens to candles. 
     * @param candle
     */
    void onCandle(Candle candle);
}
