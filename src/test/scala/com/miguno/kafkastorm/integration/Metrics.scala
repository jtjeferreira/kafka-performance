package com.miguno.kafkastorm.integration

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.ConsoleReporter
import java.util.concurrent.TimeUnit

trait Metrics {
  val metrics = new MetricRegistry()
  val requests = metrics.meter("requests");
  val reporter = ConsoleReporter.forRegistry(metrics)
    .convertRatesTo(TimeUnit.SECONDS)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .build();
}