package io.illusionbank.sec.antifraud.gateway.web.handler;

import io.illusionbank.sec.antifraud.gateway.data.model.Agency;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.illusionbank.sec.antifraud.gateway.data.model.ClientInfo;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DefineClientInfoHandler implements Handler<RoutingContext> {
    private List<Agency> agencies;

    public DefineClientInfoHandler(List<Agency> agencies) {
        this.agencies = agencies;
    }

    @Override
    public void handle(RoutingContext context) {
        log.info("DefineClientInfoHandler");
        AnalyzeRequest request = context.get(AnalyzeRequest.class.getSimpleName());
        loadClintInfo(request, handler-> {
            if(handler.succeeded()) {
                context.put(ClientInfo.class.getSimpleName(), handler.result());
                context.next();
            } else {
                context.response().setStatusCode(500);
                context.response().setStatusMessage(handler.cause().getMessage());
                context.response().end();
            }
        });
    }

    private void loadClintInfo(AnalyzeRequest request, Handler<AsyncResult<ClientInfo>> handler) {
        // TODO melhorar a busca
        Optional<Agency> agencyOptional = this.agencies.parallelStream()
                .filter(agency->agency.getCode().equals(request.getAgency()))
                .findFirst();

        if(!agencyOptional.isPresent()) {
            handler.handle(Future.factory.failureFuture("Agência não encontrada no cache"));

        } else {
            ClientInfo clientInfo =  new ClientInfo();
            clientInfo.setAgency(agencyOptional.get());

            //TODO Obter de algum serviço, mas que tenha uma latencia baixa(Diretamente na base?)
            clientInfo.setEmployeer(false);
            clientInfo.setSegment("JEDI");

            handler.handle(Future.factory.succeededFuture(clientInfo));

        }
    }

}
