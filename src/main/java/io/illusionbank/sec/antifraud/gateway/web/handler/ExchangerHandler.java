package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExchangerHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    log.info("ExchangerHandler");
    context.next();
  }

}