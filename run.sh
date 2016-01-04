times=$1

sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerMain $times"
sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerMain $times"

sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerProducerMain $times"
sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerProducerMain $times"
