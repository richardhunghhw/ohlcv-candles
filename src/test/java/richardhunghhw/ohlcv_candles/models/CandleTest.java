package richardhunghhw.ohlcv_candles.models;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class CandleTest {

    private Candle candle;

    @Before
    public void setUp() {
        candle = new Candle(123456789L);
    }

    @Test
    public void testAddPrice_FirstPrice() {
        candle.addPrice(10.0);
        assertEquals(10.0, candle.getOpen(), 0.0);
        assertEquals(10.0, candle.getHigh(), 0.0);
        assertEquals(10.0, candle.getLow(), 0.0);
        assertEquals(10.0, candle.getClose(), 0.0);
        assertEquals(1, candle.getTicks());
    }

    @Test
    public void testAddPrice_HighPrice() {
        candle.addPrice(10.0);
        candle.addPrice(15.0);
        assertEquals(10.0, candle.getOpen(), 0.0);
        assertEquals(15.0, candle.getHigh(), 0.0);
        assertEquals(10.0, candle.getLow(), 0.0);
        assertEquals(15.0, candle.getClose(), 0.0);
        assertEquals(2, candle.getTicks());
    }

    @Test
    public void testAddPrice_LowPrice() {
        candle.addPrice(10.0);
        candle.addPrice(5.0);
        assertEquals(10.0, candle.getOpen(), 0.0);
        assertEquals(10.0, candle.getHigh(), 0.0);
        assertEquals(5.0, candle.getLow(), 0.0);
        assertEquals(5.0, candle.getClose(), 0.0);
        assertEquals(2, candle.getTicks());
    }

    @Test
    public void testAddPrice_MultiplePrices() {
        candle.addPrice(10.0);
        candle.addPrice(15.0);
        candle.addPrice(5.0);
        assertEquals(10.0, candle.getOpen(), 0.0);
        assertEquals(15.0, candle.getHigh(), 0.0);
        assertEquals(5.0, candle.getLow(), 0.0);
        assertEquals(5.0, candle.getClose(), 0.0);
        assertEquals(3, candle.getTicks());
    }
}