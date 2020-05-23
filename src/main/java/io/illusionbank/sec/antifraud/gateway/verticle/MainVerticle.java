package io.illusionbank.sec.antifraud.gateway.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    vertx.deployVerticle(new WebServerVerticle(), handler->{
      log.info("WebServerVerticle - Deploy finalizado");
      startPromise.complete();
    });
  }
}
