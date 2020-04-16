# vertx-kafka-consumer-producer project
This project illustrates how you can interact with Apache Kafka using vert.x

##Tutorial used for vert.x Cleint
https://vertx.io/docs/vertx-kafka-client/java/ 

##Steps
1. Download Kafka: https://kafka.apache.org/downloads
2. Start zookeeper ./bin/zookeeper-server-start.sh config/zookeeper.properties
3. Start kafka ./bin/kafka-server-start.sh config/server.properties
4. Create Topic ./bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic prices
5. Start the Application (quarkus:dev recommended since this is only a demo project)
6. Open the prices.html. The site should show changing prices.

## Anatomy

In addition to the `prices.html` page, the application is composed by 4 components:

* `PriceGenerator` - a bean generating random price. They are sent to a Kafka topic
* `PriceConverter` - on the consuming side, the `PriceConverter` receives the Kafka message and convert the price.
The result is sent to an in-memory stream of data
* `PriceResource`  - the `PriceResource` retrieves the in-memory stream of data in which the converted prices are sent and send these prices to the browser using Server-Sent Events.
* `KafkaConfiguration`  - used to configure and initialize Kafka

The interaction with Kafka is managed by MicroProfile Reactive Messaging.
The configuration is located in the application configuration.


### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

### Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `vertx-kafka-consumer-producer-1.0-SNAPSHOT-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/vertx-kafka-consumer-producer-1.0-SNAPSHOT-runner.jar`.

### Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/vertx-kafka-consumer-producer-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.

## About Quarkus
This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

### Disclaimer
This is just a demo project. There are some errors left in this project. For example, when you close the prices.html`
you will not see the changing prices when opening it again. You have to restart the Quarkus-Application first before
reopening.
