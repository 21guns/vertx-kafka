package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.rx.java.ObservableHandler;
import io.vertx.rx.java.RxHelper;
import org.hbase.async.HBaseClient;
import org.hbase.async.HBaseClientFactory;
import org.hbase.async.PutRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

  public static final String CONFIG_WIKIDB_QUEUE = "wikidb.queue";
  private String wikiDbQueue = "wikidb.queue";
  HBaseClient client;

  @Override
  public void start(Future<Void> fut) {

    wikiDbQueue = config().getString(CONFIG_WIKIDB_QUEUE, "wikidb.queue");  // <2>


    client = HBaseClientFactory.getHBaseClient("zookeeper-1");

    // Create a router object.
    Router router = Router.router(vertx);

    // Bind "/" to our hello message - so we are still compatible.
    router.route().handler(BodyHandler.create());
    router.post("/upload").handler(this::handleListProducts);

    // Create the HTTP server and pass the "accept" method to the request handler.
    vertx
      .createHttpServer()
      .requestHandler(router::accept)
      .listen(
        // Retrieve the port from the configuration,
        // default to 8080.
        config().getInteger("http.port", 8081),
        result -> {
          if (result.succeeded()) {
            fut.complete();
          } else {
            fut.fail(result.cause());
          }
        }
      );
  }
  ObservableHandler<Long> observable = RxHelper.observableHandler();

  private void handleListProducts(RoutingContext routingContext) {

    LOGGER.info(routingContext.currentRoute().getPath());
//    DeliveryOptions options = new DeliveryOptions().addHeader("action", "all-pages"); // <2>
//
//    vertx.eventBus().send(wikiDbQueue, routingContext.getBodyAsString(), options, reply -> {  // <1>
//      if (reply.succeeded()) {
//        JsonArray body = (JsonArray) reply.result().body();
//        routingContext.response().putHeader("content-type", "application/json").end(body.encodePrettily());
//      } else {
//        routingContext.fail(reply.cause());
//      }
//    });


    client.ensureTableExists("test");
    PutRequest appendRequest = new PutRequest("test","key",
      "cf","dd","value11");
    client.put(appendRequest);


  }
  public static void main(String[] args) {
//    Vertx vertx = Vertx.vertx();
//    vertx.deployVerticle(new HttpServerVerticle());

  }


}
