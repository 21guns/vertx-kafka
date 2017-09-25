package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  private Map<String, JsonObject> products = new HashMap<>();

  @Override
  public void start(Future<Void> fut) {
    LOGGER.info("init");
    DeploymentOptions options = new DeploymentOptions().setWorker(true);
    options.setInstances(5);

    Future<String> dbVerticleDeployment = Future.future();  // <1>
    vertx.deployVerticle(new HttpServerVerticle(), dbVerticleDeployment.completer());  // <2>
    vertx.deployVerticle("io.vertx.starter.KafkaVerticle", options, dbVerticleDeployment.completer());  // <2>
  }




  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
}
