# Candle Aggregator 

Builds m1 OHLCT (Open, High, Low, Close, Ticks) candles. 

## Functions

- Aggregates L2 (Book/Tick level, the individual price levels in the book with aggregated order quantities at each level) data in to m1 candles. 
- Accecpts a single Currency Pair. Only feeding data from Kraken 
- Results stored in: 
    - **In-Memory**, up to 200 candles
    - **Kafka**, published on each candle completion 

## Candle Formation 

Each m1 candle consists of the following: 
- Timestamp; timestamp at the start of the minute (usually in epoch)
- Open; mid price at the start of the minute
- High; highest mid price during the minute
- Low; lowest mid price during the minute
- Close; mid price at the end of the minute
- Ticks; total number of ticks observed during the minute interval

Mid price is be computed by “(highest bid + lowest ask) / 2”.

Upon successful subscription to Kraken, messages arrives as follows: 
1. `KrakenBookPayloadStatus.json`, this message is ignored and not parsed
2. `KrakenBookPayloadSubscriptionAck.json`, this message is ignored and not parsed
3. `KrakenBookPayloadBookSnapshot.json`, this message is used to build the inital state 
4. `KrakenBookPayloadBookUpdate.json`, this message will then continuously update each corresponding candle. Notice this message only provides price for one side bid/ask. Starting from the inital state, this message will provide a new mid price for each candle using the new price bid/ask in combination with the missing side ask/bid from the previous tick. 

## Setup

1. Adjust `KafkaCandleDispatcher.java` and `CandleDataAggregatorTest.java` with the appropiate Kafka broker settings or comment out the `kafkaDispatcher` object in `App.java`
