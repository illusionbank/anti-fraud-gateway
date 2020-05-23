package io.illusionbank.sec.antifraud.gateway.verticle;

import io.illusionbank.sec.antifraud.gateway.web.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebServerVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var router = Router.router(vertx);
    router.route().handler(new AuthorizerHandler());
    router.route().handler(new ChannelHandler());
    router.route().handler(new RateLimitHandler());
    router.route().handler(new TransactionHandler());
    router.route().handler(new ExchangerHandler());

    var httpServer = vertx.createHttpServer();
    httpServer.requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
        log.error("HTTP server error");
      }
    });
  }

}
