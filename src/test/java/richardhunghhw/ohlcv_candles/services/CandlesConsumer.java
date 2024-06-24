package richardhunghhw.ohlcv_candles.services;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class CandlesConsumer {
    final String kafka_bootstrap_servers = "host.docker.internal:9092";
    final String kafka_topic = "candles";

    private final KafkaConsumer<String, String> consumer;

    public CandlesConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka_bootstrap_servers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "candles");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Create the consumer
        this.consumer = new KafkaConsumer<>(props);
    }

    public void consume() {
        this.consumer.subscribe(Collections.singletonList(kafka_topic));

        while (true) {
            ConsumerRecords<String, String> records = this.consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                System.out.println("CandlesConsumer.consume: " + record.value());
            }
        }
    }

    public static void main(String[] args) {
        CandlesConsumer consumer = new CandlesConsumer();
        consumer.consume();
    }
}
