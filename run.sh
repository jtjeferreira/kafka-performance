for i in {1,10,100,1000,10000,100000}; do
  sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerMain $i"
  sleep 1
  sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerMain $i"
  sleep 1

  sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerProducerMain $i"
  sleep 1
  sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerProducerMain $i"
  sleep 1
done
