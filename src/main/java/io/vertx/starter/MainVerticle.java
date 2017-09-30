package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  @Override
  public void start(Future<Void> fut) {

    vertx.deployVerticle(new HttpServerVerticle());  // <2>

    vertx.deployVerticle(
      "io.vertx.starter.KafkaConsumerVerticle",  // <4>
        new DeploymentOptions().setWorker(true).setInstances(5));

    vertx.deployVerticle(
      "io.vertx.starter.KafkaProducerVerticle",  // <4>
      new DeploymentOptions().setWorker(true).setInstances(5));

  }


  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
