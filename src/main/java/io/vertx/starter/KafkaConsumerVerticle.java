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

import com.stumbleupon.async.Callback;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.hbase.async.AppendRequest;
import org.hbase.async.HBaseClient;
import org.hbase.async.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author <a href="https://julien.ponge.org/">Julien Ponge</a>
 */
// tag::preamble[]
public class KafkaConsumerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerVerticle.class);

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    HBaseClient client = new HBaseClient("localhost:2182");

    Properties config = new Properties();
    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    config.put(ConsumerConfig.GROUP_ID_CONFIG, "my_group");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

    // use consumer for interacting with Apache Kafka
    KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, config);

    client.ensureTableExists("test").addErrback(new Callback<Object, Object>() {
      @Override
      public Object call(Object arg) throws Exception {
        return null;
      }
    });
    // Aggregates metrics in the dashboard
    consumer.handler(record -> {
      LOGGER.warn(record.value());
      PutRequest appendRequest = new PutRequest("test","key",
        "cf","dd",record.value());
      client.put(appendRequest);
    });

    consumer.subscribe("test");

  }

}
