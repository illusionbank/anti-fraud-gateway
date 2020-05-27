package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.illusionbank.sec.antifraud.gateway.data.model.ClientInfo;
import io.illusionbank.sec.antifraud.gateway.data.model.Migration;
import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.proxy.HttpProxy;
import io.illusionbank.sec.antifraud.gateway.proxy.MainFrameProxy;
import io.illusionbank.sec.antifraud.gateway.proxy.Proxy;
import io.illusionbank.sec.antifraud.gateway.proxy.SoapProxy;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class CoexistenceHandler implements Handler<RoutingContext> {
    private List<Transaction> transactions;

    public CoexistenceHandler(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("CoexistenceHandler");

        AnalyzeRequest request = context.get(AnalyzeRequest.class.getSimpleName());

        Optional<Transaction> transactionOptional = transactions.parallelStream()
                .filter(t->t.getName().equals(request.getTransaction()))
                .findFirst();

        if(!transactionOptional.isPresent()) {
            context.response().setStatusCode(500);
            context.response().end();

        } else {
            Transaction currentTransaction = transactionOptional.get();
            if(currentTransaction.hasMigration()) {
                ClientInfo clientInfo = context.get(ClientInfo.class.getSimpleName());
                Migration migration = currentTransaction.getMigration();
                if(migration.matchWithClientAndRequest(clientInfo,request)) {
                    HttpProxy httpProxy = new HttpProxy(request, currentTransaction);
                    context.put(Proxy.class.getSimpleName(), httpProxy);
                    context.next();
                } else {
                    createProxyLegadyAndFollow(context, request, currentTransaction);
                }
            } else {
                createProxyLegadyAndFollow(context, request, currentTransaction);
            }
        }
    }

    private void createProxyLegadyAndFollow(RoutingContext context, AnalyzeRequest request, Transaction currentTransaction) {
        if(currentTransaction.getFormat().equalsIgnoreCase("SOAP")) {
            SoapProxy soapProxy = new SoapProxy(request, currentTransaction);
            context.put(Proxy.class.getSimpleName(), soapProxy);

        } else {
            MainFrameProxy mainFrameProxy =  new MainFrameProxy(request, currentTransaction);
            context.put(Proxy.class.getSimpleName(), mainFrameProxy);

        }
        context.next();
    }

}
