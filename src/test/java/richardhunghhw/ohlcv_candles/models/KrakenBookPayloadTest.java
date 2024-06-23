package richardhunghhw.ohlcv_candles.models;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;


public class KrakenBookPayloadTest {
    ObjectMapper mapper;
    @Before
    public void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    public void testParseBookData() throws IOException {
        // Test Snapshot response
        String snapshotFilePath = "src/test/resources/models/KrakenBookPayloadBookSnapshot.json";
        String snapshotFile = new String(Files.readAllBytes(Paths.get(snapshotFilePath)));

        KrakenBookPayload snapshotJson = mapper.readValue(snapshotFile, KrakenBookPayload.class);
        assertEquals("book", snapshotJson.getChannel());
        assertEquals("snapshot", snapshotJson.getType());
        
        // Test Update response
        String updateFilePath = "src/test/resources/models/KrakenBookPayloadBookUpdate.json";
        String updateFile = new String(Files.readAllBytes(Paths.get(updateFilePath)));

        KrakenBookPayload updateJson = mapper.readValue(updateFile, KrakenBookPayload.class);
        assertEquals("book", updateJson.getChannel());
        assertEquals("update", updateJson.getType());
    }

    @Test(expected = com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class)
    public void testParseStatus() throws IOException {
        // Test Subscription Status, this is ignored
        String statusFilePath = "src/test/resources/models/KrakenBookPayloadStatus.json";
        String statusFile = new String(Files.readAllBytes(Paths.get(statusFilePath))); 

        KrakenBookPayload statusJson = mapper.readValue(statusFile, KrakenBookPayload.class);
    }


    @Test(expected = com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class)
    public void testParseSubscriptionAck() throws IOException {
        // Test Subscription Acknowledgement, this is ignored
        String subscriptionAckFilePath = "src/test/resources/models/KrakenBookPayloadSubscriptionAck.json";
        String subscriptionAckFile = new String(Files.readAllBytes(Paths.get(subscriptionAckFilePath)));

        KrakenBookPayload subscriptionAckJson = mapper.readValue(subscriptionAckFile, KrakenBookPayload.class);
    }
}