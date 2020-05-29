package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.illusionbank.sec.antifraud.gateway.driver.Driver;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangerHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    log.info("ExchangerHandler");
    Driver driver = context.get(Driver.class.getSimpleName());
    Promise<JsonObject> response = driver.call();
    response.future().onComplete(handler-> {
      if(handler.succeeded()) {
        context.response().setStatusCode(200);
        context.response().end("aa");
      } else {
        context.response().setStatusCode(500);
        context.response().end("Erro");
      }
    });
  }

}
