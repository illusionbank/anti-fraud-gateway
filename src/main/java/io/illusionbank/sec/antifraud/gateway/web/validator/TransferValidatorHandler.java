package io.illusionbank.sec.antifraud.gateway.web.validator;

import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransferValidatorHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext context) {
        AnalyzeRequest request = context.get(AnalyzeRequest.class.getSimpleName());
        log.info("Request {}", request.getTransaction());
        log.info("Aqui devemos validar os campos da transação referente transferencia estão corretos");
        context.next();
    }

}
