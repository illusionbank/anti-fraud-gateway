package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefineRequestHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    context.request().bodyHandler(buffer-> {
      log.info("DefineTransactionHandler");
      context.put(AnalyzeRequest.class.getSimpleName(), buffer.toJsonObject().mapTo(AnalyzeRequest.class));
      context.next();
    });
  }
}
