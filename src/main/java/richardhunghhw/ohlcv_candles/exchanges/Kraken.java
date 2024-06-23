package richardhunghhw.ohlcv_candles.exchanges;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

import richardhunghhw.ohlcv_candles.CandleDataAggregator;
import richardhunghhw.ohlcv_candles.models.BookData;
import richardhunghhw.ohlcv_candles.models.KrakenBookPayload;
import richardhunghhw.ohlcv_candles.models.KrakenBookPayloadData;

/**
 * Kraken WebSocket client
 * https://docs.kraken.com/api/docs/websocket-v2/book
 */
@ClientEndpoint
public class Kraken implements Exchange {
    private final String uri = "wss://ws.kraken.com/v2";

    private final CandleDataAggregator aggregator;
    private final ObjectMapper mapper;
    private Session session;
    private BookData prevBookData;

    public Kraken(CandleDataAggregator aggregator) {
        this.aggregator = aggregator;

        this.mapper = new ObjectMapper();
    }

    /**
     * Connect to the Kraken WebSocket server
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws DeploymentException 
     */
    public void connect() throws DeploymentException, IOException, URISyntaxException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.connectToServer(this, new URI(uri));
    }

    /**
     * On open, subscribe to book data
     * @param session
     */
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("WebSocket opened for Kraken, subscribing to book data");
        this.session = session;
        sendMessage("{\"method\": \"subscribe\", \"params\": { \"channel\": \"book\", \"symbol\": [\"BTC/USD\"]}}");
    }

    /**
     * On message, parse book data and add to aggregator
     * Book data comes as a form of an snapshot or update from an exchange.
     * In snapshot, the exchange sends the entire order book, BookData contains the highest bid and lowest ask
     * In update, the exchange sends only the changes to the order book which contains only updates to one side bid/ask
     * We maintain the previous BookData so that we can replace the updated bid/ask and use the previous bid/ask
     * We also assume snapshot is always the first message received from the exchange
     * @param message
     * @throws Exception 
     */
    @OnMessage
    public void onMessage(String message) throws Exception {
        // System.out.println("WebSocket message received: " + message);
        BookData bookData = parseBookData(message);
        if (bookData != null) {
            if (prevBookData != null) {
                // Update the highest bid and lowest ask if they are not null
                if (bookData.getHighestBid() != null) {
                    bookData.setLowestAsk(prevBookData.getLowestAsk());
                }
                if (bookData.getLowestAsk() != null) {
                    bookData.setHighestBid(prevBookData.getHighestBid());
                }
            }
            
            prevBookData = bookData;
            aggregator.addBookData(bookData);
        }
    }

    /**
     * On close
     * @param session
     * @param closeReason
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("WebSocket closed for URI: " + uri + " Reason: " + closeReason.getReasonPhrase());
        this.session = null;
    }

    /**
     * On error
     * @param session
     * @param throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        // TODO Exit the program if there is an error
        throwable.printStackTrace();
    }

    /**
     * Send message to WebSocket server
     * @param message
     */
    private void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText(message);
        }
    }

    /**
     * Parse book data from message to get the timestamp, highest bid, and lowest ask
     * @param data
     * @return
     */
    private BookData parseBookData(String data) throws RuntimeException {
        KrakenBookPayload payload; 
        try {
            payload = mapper.readValue(data, KrakenBookPayload.class);
        } catch (UnrecognizedPropertyException e) {
            return null; // Ignore Status / Acknowledgement messages
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing book data: " + data + ", error:" + e.getMessage());
        } 

        // Channel should be "book", type should be "snapshot" or "update"
        if (!payload.getChannel().equals("book") || (!payload.getType().equals("snapshot") && !payload.getType().equals("update"))){
            return null;
        }

        // There should be only one data object in the payload
        if (payload.getData() == null || payload.getData().size() != 1) {
            throw new RuntimeException("Unexpected book data payload: " + data);
        } 

        KrakenBookPayloadData bookPayloadData = payload.getData().get(0);

        // Parse timestamp 2024-06-21T17:00:31.924407Z
        LocalDateTime timestamp; 
        if (payload.getType().equals("snapshot")) {
            timestamp = LocalDateTime.now();
        } else {
            timestamp = parseTimestamp(bookPayloadData);
        }

        // Parse highest bid and lowest ask
        Double highestBid = bookPayloadData.getBids().isEmpty() ? null : bookPayloadData.getBids().get(0).getPrice();
        Double lowestAsk = bookPayloadData.getAsks().isEmpty() ? null : bookPayloadData.getAsks().get(0).getPrice();
        
        return new BookData(timestamp, highestBid, lowestAsk);
    }

    private LocalDateTime parseTimestamp(KrakenBookPayloadData bookPayloadData) {
        String timestamp = bookPayloadData.getTimestamp();
        return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
    }
}