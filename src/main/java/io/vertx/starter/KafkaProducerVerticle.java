/*
 *  Copyright (c) 2017 Red Hat, Inc. and/or its affiliates.
 *  Copyright (c) 2017 INSA Lyon, CITI Laboratory.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author <a href="https://julien.ponge.org/">Julien Ponge</a>
 */
// tag::preamble[]
public class KafkaProducerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerVerticle.class);

  public static final String CONFIG_WIKIDB_QUEUE = "wikidb.queue";

  private KafkaProducer<String, String> producer;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    Properties config = new Properties();
    config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
    config.put(ProducerConfig.ACKS_CONFIG, "1");

    // use producer for interacting with Apache Kafka
    producer = KafkaProducer.create(vertx, config, String.class, String.class);

    vertx.eventBus().consumer(config().getString(CONFIG_WIKIDB_QUEUE, "wikidb.queue"), this::onMessage);  // <3>


  }
  // end::start[]

  // tag::onMessage[]

  public void onMessage(Message<String> message) {
    if (!message.headers().contains("action")) {
      LOGGER.error("No action header specified for message with headers {}",
        message.headers());
      message.fail(1, "No action header specified");
      return;
    }
    // only topic and message value are specified, round robin on destination partitions
    KafkaProducerRecord<String, String> record =
      KafkaProducerRecord.create("test", message.body());

    producer.write(record);
    LOGGER.info(message.body());
  }
  // end::onMessage[]

}
