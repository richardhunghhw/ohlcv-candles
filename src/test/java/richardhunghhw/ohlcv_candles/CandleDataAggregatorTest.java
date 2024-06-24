package richardhunghhw.ohlcv_candles;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import richardhunghhw.ohlcv_candles.models.BookData;
import richardhunghhw.ohlcv_candles.models.Candle;
import richardhunghhw.ohlcv_candles.models.TimeProvider;
import richardhunghhw.ohlcv_candles.services.CandleListenerService;

public class CandleDataAggregatorTest {
    private TimeProvider timeProvider;
    private CandleDataAggregator aggregator;
    private CandleListenerService listener;

    @Before
    public void setUp() {
        timeProvider = mock(TimeProvider.class);
        aggregator = new CandleDataAggregator(timeProvider);
        listener = mock(CandleListenerService.class);
        aggregator.addListener(listener);
    }

    @Test
    public void testAggregateAndNotify() {
        CandleDataAggregator.period = 10000; // 10 seconds
        long time = 1719180000000L;

        when(timeProvider.currentTimeMillis()).thenReturn(time); 
        aggregator.aggregateAndNotify();

        time += 10000; // 10 seconds later
        LocalDateTime now = LocalDateTime.ofEpochSecond(time / 1000, 0, ZoneOffset.UTC);
        
        BookData bookData1 = new BookData(now.plus(1, ChronoUnit.SECONDS), 1.0, 2.0);
        aggregator.addBookData(bookData1);

        BookData bookData2 = new BookData(now.plus(2, ChronoUnit.SECONDS), 1.5, 2.5);
        aggregator.addBookData(bookData2);

        BookData bookData3 = new BookData(now.plus(3, ChronoUnit.SECONDS), 2.0, 3.0);
        aggregator.addBookData(bookData3);

        // 10 seconds later
        time += 10000;
        when(timeProvider.currentTimeMillis()).thenReturn(time); // 10 seconds later
        aggregator.aggregateAndNotify();

        verify(listener, times(1)).onCandle(any(Candle.class));
    }
}