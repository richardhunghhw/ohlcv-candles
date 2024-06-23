package richardhunghhw.ohlcv_candles.models;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class BookDataTest {

    @Test
    public void testGetMidPrice() {
        LocalDateTime timestamp = LocalDateTime.now();
        Double highestBid = 10.0;
        Double lowestAsk = 5.0;
        BookData bookData = new BookData(timestamp, highestBid, lowestAsk);

        Double expectedMidPrice = 7.5;
        Double actualMidPrice = bookData.getMidPrice();

        assertEquals(expectedMidPrice, actualMidPrice);
    }

    @Test
    public void testGetMidPriceWithZeroValues() {
        LocalDateTime timestamp = LocalDateTime.now();
        Double highestBid = 0.0;
        Double lowestAsk = 0.0;
        BookData bookData = new BookData(timestamp, highestBid, lowestAsk);

        Double expectedMidPrice = null;
        Double actualMidPrice = bookData.getMidPrice();

        assertEquals(expectedMidPrice, actualMidPrice);
    }

    @Test
    public void testSetHighestBid() {
        LocalDateTime timestamp = LocalDateTime.now();
        Double highestBid = 10.0;
        Double lowestAsk = 5.0;
        BookData bookData = new BookData(timestamp, highestBid, lowestAsk);

        Double newHighestBid = 15.0;
        bookData.setHighestBid(newHighestBid);

        assertEquals(newHighestBid, bookData.getHighestBid());
    }

    @Test
    public void testSetLowestAsk() {
        LocalDateTime timestamp = LocalDateTime.now();
        Double highestBid = 10.0;
        Double lowestAsk = 5.0;
        BookData bookData = new BookData(timestamp, highestBid, lowestAsk);

        Double newLowestAsk = 3.0;
        bookData.setLowestAsk(newLowestAsk);

        assertEquals(newLowestAsk, bookData.getLowestAsk());
    }

    @Test
    public void testEquals() {
        LocalDateTime timestamp = LocalDateTime.now();
        Double highestBid = 10.0;
        Double lowestAsk = 5.0;
        BookData bookData1 = new BookData(timestamp, highestBid, lowestAsk);
        BookData bookData2 = new BookData(timestamp, highestBid, lowestAsk);

        assertTrue(bookData1.equals(bookData2));
    }

    @Test
    public void testNotEquals() {
        LocalDateTime timestamp1 = LocalDateTime.now();
        LocalDateTime timestamp2 = LocalDateTime.now().plusMinutes(1);
        Double highestBid = 10.0;
        Double lowestAsk = 5.0;
        BookData bookData1 = new BookData(timestamp1, highestBid, lowestAsk);
        BookData bookData2 = new BookData(timestamp2, highestBid, lowestAsk);

        assertFalse(bookData1.equals(bookData2));
    }
}