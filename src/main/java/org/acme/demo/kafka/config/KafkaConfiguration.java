package org.acme.demo.kafka.config;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class KafkaConfiguration {

    @Inject
    @ConfigProperty(name = "kafka.bootstrap.server")
    String bootstrapServer;

    @Inject
    Vertx vertx;

    @Inject
    @ConfigProperty(name = "kafka.topic")
    String topic;

    private KafkaConsumer<String, Integer> consumer;
    private KafkaProducer<String, Integer> producer;

    @PostConstruct
    void init() {
        consumer = KafkaConsumer.create(vertx, consumerConfig());
        producer = KafkaProducer.create(vertx, producerConfig());
    }

    public KafkaConsumer<String, Integer> getConsumer() {
        return consumer;
    }

    public KafkaProducer<String, Integer> getProducer() {
        return producer;
    }

    public String getTopic() {
        return topic;
    }

    private Map<String, String> producerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServer);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
        return config;
    }

    private Map<String, String> consumerConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServer);
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        config.put("group.id", "vertx_consumer");
        return config;
    }

    @PreDestroy
    void close() {
        consumer.close();
        producer.close();
    }

}
