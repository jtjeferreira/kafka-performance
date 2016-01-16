package com.miguno.kafkastorm.integration

import java.util.concurrent.TimeUnit
import com.codahale.metrics._
import com.miguno.kafkastorm.testing._
import java.util.Properties
import com.twitter.bijection.Injection
import com.miguno.avro.Tweet
import scala.concurrent.duration._
import com.twitter.bijection.avro.SpecificAvroCodecs
import akka.stream.scaladsl._
import akka.stream.ActorMaterializer
import akka.actor.ActorSystem

trait KafkaConsumerTest extends Metrics {

  def createAndStartConsumer(f: (Array[Byte]) => Unit): Unit
  
  val topic = new KafkaTopic("topic")
  val cluster = new EmbeddedKafkaZooKeeperCluster(topics=Seq(topic))
  
  implicit val specificAvroBinaryInjectionForTweet = SpecificAvroCodecs.toBinary[Tweet]
  
  def run(times: Int) = {
    cluster.start()
    createAndStartConsumer{ m =>
      requests.mark
      val tweet = Injection.invert[Tweet, Array[Byte]](m).get
      if(tweet.getText.toInt >= times) exit()
    }
    
    val producer = cluster.createProducer(topic.name, new Properties()).get
      
    for {
      i <- 1 to times
      tweet = new Tweet("ANY_USER_1", i.toString, System.currentTimeMillis().millis.toSeconds)
      bytes = Injection[Tweet, Array[Byte]](tweet)
    } yield producer.send(bytes)
    
  }
  
  def exit() = {
    println("exiting")
    cluster.stop()
    reporter.report()
  }
}

object KafkaConsumerMain extends App with KafkaConsumerTest {
  run(args(0).toInt)
  def createAndStartConsumer(f: (Array[Byte]) => Unit) = {
    cluster.createAndStartConsumer(topic.name, { (m,c) =>
      f(m.message())
    })
  }
  
}

object ReactiveKafkaConsumerMain extends App with KafkaConsumerTest {
  lazy implicit val actorSystem = ActorSystem("ReactiveKafka")
  lazy implicit val materializer = ActorMaterializer()
  run(args(0).toInt)
  
  def createAndStartConsumer(f: (Array[Byte]) => Unit) = {
    val publisher = cluster.createReactiveConsumer(topic.name)
    Source.fromPublisher(publisher).map(m => f(m.value())).to(Sink.ignore).run()
  }
  
  override def exit() = {
    super.exit()
    System.exit(0)
  }
}

