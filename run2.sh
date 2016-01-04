times=$1
watermark=$2

sbt "test:runMain com.miguno.kafkastorm.integration.KafkaConsumerProducerMain $times"
sleep 1
sbt "test:runMain com.miguno.kafkastorm.integration.ReactiveKafkaConsumerProducerMain $times $watermark"
sleep 1
