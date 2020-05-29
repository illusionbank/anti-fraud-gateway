package io.illusionbank.sec.antifraud.gateway.verticle;

import io.illusionbank.sec.antifraud.gateway.data.model.Agency;
import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.web.handler.DefineTargetHandler;
import io.illusionbank.sec.antifraud.gateway.web.handler.DefineClientInfoHandler;
import io.illusionbank.sec.antifraud.gateway.web.handler.DefineRequestHandler;
import io.illusionbank.sec.antifraud.gateway.web.handler.ExchangerHandler;
import io.illusionbank.sec.antifraud.gateway.web.validator.ChagerBackValidatorHandler;
import io.illusionbank.sec.antifraud.gateway.web.validator.PaymentValidatorHandler;
import io.illusionbank.sec.antifraud.gateway.web.validator.TransferValidatorHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class WebServerVerticle extends AbstractVerticle {
  private List<Agency> agencies;
  private List<Transaction> transactions;

  public WebServerVerticle(List<Agency> agencies, List<Transaction> transactions) {
    this.agencies = agencies;
    this.transactions = transactions;
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    var router = Router.router(vertx);

    router.route().handler(new DefineRequestHandler());

    router.route("/transfer").handler(new TransferValidatorHandler());
    router.route("/payment").handler(new PaymentValidatorHandler());
    router.route("/chargeback").handler(new ChagerBackValidatorHandler());

    router.route().handler(new DefineClientInfoHandler(agencies));
    router.route().handler(new DefineTargetHandler(transactions));
    router.route().handler(new ExchangerHandler());

    vertx.createHttpServer().requestHandler(router).listen(8888, httpServerResult -> {
      if (httpServerResult.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port {}", httpServerResult.result().actualPort());
      } else {
        startPromise.fail(httpServerResult.cause());
        log.error("HTTP server error");
      }
    });
  }

}
