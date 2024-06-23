package richardhunghhw.ohlcv_candles.exchanges;

import java.nio.file.Files;
import java.nio.file.Paths;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import richardhunghhw.ohlcv_candles.CandleDataAggregator;
import richardhunghhw.ohlcv_candles.models.BookData;

public class KrakenTest {

    private CandleDataAggregator aggregator;
    private Kraken kraken;

    @Before
    public void setUp() {
        aggregator = mock(CandleDataAggregator.class);
        kraken = new Kraken(aggregator);
    }

    @Test
    public void testOnOpen() {
        RemoteEndpoint.Async async = mock(RemoteEndpoint.Async.class);
        when(async.sendText(anyString())).thenReturn(null);
    
        Session session = mock(Session.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getAsyncRemote()).thenReturn(async);
    
        kraken.onOpen(session);
        verify(session.getAsyncRemote()).sendText(anyString());
    }

    // Useless test
    // @Test
    // public void testOnClose() throws IOException {
    //     Session session = mock(Session.class);

    //     CloseReason closeReason = mock(CloseReason.class);
    //     when(closeReason.getReasonPhrase()).thenReturn("Test");

    //     kraken.onClose(session, closeReason);
    // }

    // Useless test
    // @Test
    // public void testOnError() {
    //     Session session = mock(Session.class);
    //     Throwable throwable = mock(Throwable.class);
    //     kraken.onError(session, throwable);
    // }

    @Test
    public void testOnMessage_Snapshot() throws Exception {
        String snapshotFilePath = "src/test/resources/models/KrakenBookPayloadBookSnapshot.json";
        String snapshotFile = new String(Files.readAllBytes(Paths.get(snapshotFilePath)));

        kraken.onMessage(snapshotFile);

        verify(aggregator).addBookData(any(BookData.class));
    }

    @Test
    public void testOnMessage_UnrecognizedPropertyException() throws Exception {
        String message = "{\"channel\":\"book\",\"data\":[{\"asks\":[{\"price\":\"51000.0\",\"amount\":\"0.5\",\"timestamp\":\"2022-01-01T00:00:00.000Z\"}],\"bids\":[{\"price\":\"49000.0\",\"amount\":\"0.5\",\"timestamp\":\"2022-01-01T00:00:00.000Z\"}],\"timestamp\":\"2022-01-01T00:00:00.000Z\",\"type\":\"update\"}],\"event\":\"subscribe\",\"pair\":\"BTC/USD\"}";

        kraken.onMessage(message);

        verify(aggregator, never()).addBookData(any());
    }
}