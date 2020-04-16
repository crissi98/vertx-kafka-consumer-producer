package org.acme.demo.kafka.price;

import io.quarkus.runtime.Startup;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.acme.demo.kafka.config.KafkaConfiguration;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Startup
@ApplicationScoped
public class PriceConverter {

    private static final Logger LOG = LoggerFactory.getLogger(PriceConverter.class);

    @Inject
    KafkaConfiguration kafkaConfig;

    @Inject
    @Channel("price-stream")
    Emitter<Double> priceEmitter;

    private static final double CONVERSION_RATE = .88;

    private boolean started = false;

    private KafkaConsumer<String, Integer> priceConsumer;

    @PostConstruct
    void init() {
        LOG.info("Initializing PriceConverter");
        priceConsumer = kafkaConfig.getConsumer();
        priceConsumer.subscribe(kafkaConfig.getTopic(), ar -> {
            if (ar.succeeded()) {
                LOG.info("Successfully subsribed to topic {}", kafkaConfig.getTopic());
            } else {
                LOG.error("Could not subscribe {}", ar.cause().getMessage());
            }
        }).handler(record -> {
            int inputPrice = record.value();
            LOG.info("Read price {} from Kafka", inputPrice);
            if (started) {
                double outputPrice = inputPrice * CONVERSION_RATE;
                LOG.info("Writing price {} to price-stream", outputPrice);
                priceEmitter.send(outputPrice);
            }
        });
    }


    //To indicate that the prices read from Kafka should be written to the outgoing stream.
    //If the prices are added right at start time, the application crashes because the stream is not created yet.
    public void start() {
        started = true;
    }

}