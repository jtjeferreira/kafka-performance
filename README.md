# kafka-performance-tests

Performance tests comparing [reactive-kafka](https://github.com/softwaremill/reactive-kafka) and vanilla kafka High-level-api

This is a fork of [kafka-storm-starter](https://github.com/miguno/kafka-storm-starter) because it contains nice utilities (kafka embedded, zookeeper embedded), etc

---

# Test Cases
The tests use kafka 0.8.2.2 and reactive-kafka 0.8.4 (which uses akka-streams 2)

## Consumer

    $ src/test/scala/com/miguno/kafkastorm/integration/KafkaConsumerMain.scala

This test case creates a consumer on topic "topic" and tests it by creating a producer that sends N messages to that topic. KafkaConsumerMain uses the vanilla API, and ReactiveKafkaConsumerMain uses the reactive-kafka API. We measure the consumer throughput by recording the messages received by the consumer.

## ConsumerProducer

    $ src/test/scala/com/miguno/kafkastorm/integration/KafkaConsumerProducerMain.scala

This test case creates a consumer on topic "topicIn" and a producer on topic "topicOut"; whenever a message is received by the consumer in "topicIn" it publishes the same message on "topicOut".

This "flow" is tested by creating a producer that sends N messages to "topicIn" and a consumer on "topicOut". We measure the flow throughput by recording the messages received by the "topicOut" consumer.

# Results

## Consumer

Nr of messages  | vanilla api (rps) | reactive-kafka api (rps) |
--------------- | -----------------:| ------------------------:|
10     | 3.39     | 3.54
100    | 32.17    | 31.51
1000   | 265.93   | 257.58
10000  | 1,516.64 | 1,588.79
100000 | 5,821.53 | 5,841.08

![Consumer results](images/Consumer Vanilla vs Reactive.png)

## ConsumerProducer

Nr of messages  | vanilla api (rps) | reactive-kafka api (rps) |
--------------- | -----------------:| ------------------------:|
10     | 2.72     | 2.71
100    | 22.93    | 24.58
1000   | 176.59   | 173.61
10000  | 1,053.37 | 854.11
100000 | 4,531.67 | 2,398.79

![ConsumerProducer results](images/ConsumerProducer Vanilla vs Reactive.png)
