package richardhunghhw.ohlcv_candles.models;

import java.time.LocalDateTime;

public class BookData {
    private final LocalDateTime timestamp;
    private Double highestBid;
    private Double lowestAsk;

    public BookData(LocalDateTime timestamp, Double highestBid, Double lowestAsk) {
        this.timestamp = timestamp;
        this.highestBid = highestBid;
        this.lowestAsk = lowestAsk;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Double getHighestBid() {
        return highestBid;
    }   

    public void setHighestBid(Double highestBid) {
        this.highestBid = highestBid;
    }

    public Double getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(Double lowestAsk) {
        this.lowestAsk = lowestAsk;
    }
    
    public Double getMidPrice() {
        if (highestBid == 0 || lowestAsk == 0) {
            return null;
        }
        return (highestBid + lowestAsk) / 2;
    }

    @Override
    public String toString() {
        return "BookData{" +
                "timestamp=" + timestamp +
                ", highestBid=" + highestBid +
                ", lowestAsk=" + lowestAsk +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BookData bookData = (BookData) o;

        if (!timestamp.equals(bookData.timestamp)) return false;
        if (!highestBid.equals(bookData.highestBid)) return false;
        return lowestAsk.equals(bookData.lowestAsk);
    }

    @Override
    public int hashCode() {
        int result = timestamp.hashCode();
        result = 31 * result + highestBid.hashCode();
        result = 31 * result + lowestAsk.hashCode();
        return result;
    }
}