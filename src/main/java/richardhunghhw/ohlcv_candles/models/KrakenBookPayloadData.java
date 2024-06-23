package richardhunghhw.ohlcv_candles.models;

import java.util.List;

public class KrakenBookPayloadData {
    private String symbol;
    private List<KrakenBookPayloadPriceData> bids;
    private List<KrakenBookPayloadPriceData> asks;
    private String checksum;
    private String timestamp;

    public String getSymbol() {
        return symbol;
    }

    public List<KrakenBookPayloadPriceData> getBids() {
        return bids;
    }

    public List<KrakenBookPayloadPriceData> getAsks() {
        return asks;
    }

    public String getChecksum() {
        return checksum;
    }
    
    public String getTimestamp() {
        return timestamp;
    }

}
