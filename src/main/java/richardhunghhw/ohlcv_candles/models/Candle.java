package richardhunghhw.ohlcv_candles.models;

public class Candle {
    private final long timestamp; // Epoch timestamp
    private double open; // Open price
    private double high; // High price
    private double low; // Low price
    private double close; // Close price
    private int ticks; // Ticks

    public Candle(long timestamp, double open, double high, double low, double close, int ticks) {
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.ticks = ticks;
    }

    public Candle(long timestamp){
        this(timestamp, 0.0, 0.0, 0.0, 0.0, 0);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    public int getTicks() {
        return ticks;
    }

    /**
     * Adds a price to the candle.
     * Updates open, high, low, close, and ticks based on the new price.
     * @param price
     */
    public void addPrice(double price) {
        if (ticks == 0) {
            open = price;
            high = price;
            low = price;
            close = price;
        } else {
            close = price;
            if (price > high) {
                high = price;
            }
            if (price < low) {
                low = price;
            }
        }
        this.ticks++;
    }

    @Override
    public String toString() {
        return "Candle{" +
                "timestamp=" + timestamp +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", ticks=" + ticks +
                '}';
    }
}
