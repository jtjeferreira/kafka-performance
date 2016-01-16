package com.miguno.kafkastorm.integration

import java.util.concurrent.TimeUnit
import com.codahale.metrics._
import com.codahale.metrics.MetricRegistry._
import com.miguno.kafkastorm.testing._
import java.util.Properties
import com.twitter.bijection.Injection
import com.miguno.avro.Tweet
import scala.concurrent.duration._
import com.twitter.bijection.avro.SpecificAvroCodecs
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import com.softwaremill.react.kafka.ProducerMessage

trait KafkaConsumerProducerTest extends Metrics {
  def createAndStartConsumerProducer(topicIn: String, topicOut: String): Unit
  
  val topicIn = new KafkaTopic("topicIn")
  val topicOut = new KafkaTopic("topicOut")
  val cluster = new EmbeddedKafkaZooKeeperCluster(topics=Seq(topicIn, topicOut))
  
  implicit val specificAvroBinaryInjectionForTweet = SpecificAvroCodecs.toBinary[Tweet]
  
  def run(times: Int) = {
    cluster.start()
    createAndStartConsumerProducer(topicIn.name, topicOut.name)
    
    val consumer = cluster.createAndStartConsumer(topicOut.name, (m,c) => {
      requests.mark
      val tweet = Injection.invert[Tweet, Array[Byte]](m.message()).get
      if(tweet.getText.toInt >= times) exit()
    })
    
    val producer = cluster.createProducer(topicIn.name, new Properties()).get
      
    for {
      i <- 1 to times
      tweet = new Tweet("ANY_USER_1", i.toString, System.currentTimeMillis().millis.toSeconds)
      bytes = Injection[Tweet, Array[Byte]](tweet)
    } yield producer.send(bytes)
  }
  
  def exit() = {
    cluster.stop()
    reporter.report()
  }
}

object KafkaConsumerProducerMain extends App with KafkaConsumerProducerTest {
  run(args(0).toInt)
  def createAndStartConsumerProducer(topicIn: String, topicOut: String) = {
    val producer = cluster.createProducer(topicOut, new Properties()).get
    cluster.createAndStartConsumer(topicIn, { (m,c) =>
      producer.send(m.key, m.message())
    })
  }
  
}

object ReactiveKafkaConsumerProducerMain extends App with KafkaConsumerProducerTest {
  lazy implicit val actorSystem = ActorSystem("ReactiveKafka")
  lazy implicit val materializer = ActorMaterializer()
  
  run(args(0).toInt)
  
  def createAndStartConsumerProducer(topicIn: String, topicOut: String) = {
    val publisher = cluster.createReactiveConsumer(topicIn)
    val subscriber = cluster.createReactiveProducer(topicOut, args(1).toInt)
    Source.fromPublisher(publisher).map(m=>ProducerMessage(m)).to(Sink.fromSubscriber(subscriber)).run()
  }
  
  override def exit() = {
    super.exit()
    System.exit(0)
  }
}

