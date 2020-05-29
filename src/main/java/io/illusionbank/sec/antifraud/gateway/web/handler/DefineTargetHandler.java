package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.illusionbank.sec.antifraud.gateway.data.model.ClientInfo;
import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.driver.RestDriver;
import io.illusionbank.sec.antifraud.legacy.driver.MainFrameDriver;
import io.illusionbank.sec.antifraud.gateway.driver.Driver;
import io.illusionbank.sec.antifraud.legacy.driver.SoapDriver;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DefineTargetHandler implements Handler<RoutingContext> {
    private List<Transaction> transactions;
    private Vertx vertx;

    public DefineTargetHandler(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("CoexistenceHandler");

        AnalyzeRequest request = context.get(AnalyzeRequest.class.getSimpleName());

        Optional<Transaction> transactionOptional = getTransactionByTransactionName(request.getTransaction());

        if(transactionOptional.isEmpty()) {
            context.response().setStatusCode(500);
            context.response().end();

        } else {
            Transaction transaction = transactionOptional.get();
            if(transaction.hasMigration()) {
                checkCriteriaInMigration(context, transaction);

            } else {
                createLegacyDriverAndFollow(context, request, transaction);

            }
        }
    }

    private Optional<Transaction> getTransactionByTransactionName(String name) {
        return transactions.parallelStream()
                .filter(t->t.getName().equals(name))
                .findFirst();
    }

    private void checkCriteriaInMigration(RoutingContext context, Transaction transaction) {
        ClientInfo clientInfo = context.get(ClientInfo.class.getSimpleName());
        AnalyzeRequest request = context.get(AnalyzeRequest.class.getSimpleName());
        if(transaction.getMigration().matchWithClientAndRequest(clientInfo, request)) {
            RestDriver restDriver = new RestDriver(vertx, request, transaction);
            context.put(Driver.class.getSimpleName(), restDriver);
            context.next();

        } else {
            createLegacyDriverAndFollow(context, request, transaction);

        }
    }

    private void createLegacyDriverAndFollow(RoutingContext context, AnalyzeRequest request, Transaction currentTransaction) {
        if(currentTransaction.getFormat().equalsIgnoreCase("SOAP")) {
            SoapDriver soapDriver = new SoapDriver(vertx, request, currentTransaction);
            context.put(Driver.class.getSimpleName(), soapDriver);

        } else {
            MainFrameDriver mainFrameDriver =  new MainFrameDriver(vertx, request, currentTransaction);
            context.put(Driver.class.getSimpleName(), mainFrameDriver);

        }
        context.next();
    }

}
