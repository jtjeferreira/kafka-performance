times=$1

sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerMain $times"
sleep 1
sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerMain $times"
sleep 1

sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerProducerMain $times"
sleep 1
sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerProducerMain $times"
sleep 1
