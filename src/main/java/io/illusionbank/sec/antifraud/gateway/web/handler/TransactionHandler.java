package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    log.info("TransactionHandler");
    context.next();
  }

}
