package richardhunghhw.ohlcv_candles.services;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import richardhunghhw.ohlcv_candles.models.Candle;

public class KafkaCandleDispatcher implements CandleListenerService {
    private final String kafka_bootstrap_servers = "host.docker.internal:9092";
    private final String kafka_topic = "candles";

    private final Producer<String, String> producer;
    private final ObjectMapper om = new ObjectMapper();

    public KafkaCandleDispatcher() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_bootstrap_servers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Create the producer
        this.producer = new KafkaProducer<>(props);
    }

    /**
     * Adds a candle to the store. If the store size exceeds STORE_SIZE candles, the oldest candles are removed.
     * @param candle
     */
    @Override
    public void onCandle(Candle candle) {
        System.out.println("KafkaCandleDispatcher.onCandle: " + candle.toString());

        // Serialize the candle to JSON and publish it to the topic
        try {
            publish(om.writeValueAsString(candle));
        } catch (JsonProcessingException e) {
            // TODO error handling, ignore for now
            System.err.println("KafkaCandleDispatcher.onCandle: Failed to serialize candle to JSON: " + e.getMessage());
        }
    }

    // Create and publish a message to the topic
    public void publish(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(kafka_topic, "key", message);
        this.producer.send(record);
    }
}
