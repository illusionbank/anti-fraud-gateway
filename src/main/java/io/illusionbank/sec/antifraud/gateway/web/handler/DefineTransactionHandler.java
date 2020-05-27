package io.illusionbank.sec.antifraud.gateway.web.handler;

import com.fasterxml.jackson.core.JsonParseException;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.netty.handler.codec.Headers;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefineTransactionHandler implements Handler<RoutingContext> {

  @Override
  public void handle(RoutingContext context) {
    String contentType = context.request().getHeader("Content-Type");
    boolean isApplicationJson = contentType != null && contentType.equalsIgnoreCase("application/json");
    if(isApplicationJson) {
      context.request().bodyHandler(buffer-> {
        log.info("DefineTransactionHandler");
        try {
          context.put(AnalyzeRequest.class.getSimpleName(), buffer.toJsonObject().mapTo(AnalyzeRequest.class));
          context.next();
        } catch (IllegalArgumentException iag) {
          context.response().setStatusCode(400);
          context.response().end();
        } catch (Exception e) {
          context.response().setStatusCode(500);
          context.response().end();
        }
      });
    } else {
      context.response().setStatusCode(400);
      context.response().end();
    }
  }
}
