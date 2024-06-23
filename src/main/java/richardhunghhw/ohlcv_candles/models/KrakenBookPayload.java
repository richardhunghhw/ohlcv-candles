package richardhunghhw.ohlcv_candles.models;

import java.util.List;

public class KrakenBookPayload {
    private String channel;
    private String type;
    private List<KrakenBookPayloadData> data;

    public String getChannel() {
        return channel;
    }

    public String getType() {
        return type;
    }

    public List<KrakenBookPayloadData> getData() {
        return data;
    }
}