package io.illusionbank.sec.antifraud.gateway.driver;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


@Slf4j
public class RestDriver extends Driver {

    private WebClient webClient;

    public RestDriver(Vertx vertx, AnalyzeRequest request, Transaction transaction) {
        super(vertx, request, transaction);
    }

    @Override
    public Promise<JsonObject> call() {
        Promise promise = Promise.promise();
        String host = currentTransaction.getMigration().getHost();
        Integer port = currentTransaction.getMigration().getPort();

        HttpRequest<Buffer> request = createOrGetWebClient().request(HttpMethod.POST, port, host,"/");

        request.sendBuffer(Buffer.buffer(Json.encode(request)), handler-> {
            if(handler.failed()) {
                promise.fail("Falha ao chamar o serviço");
            } else {
                promise.complete(handler.result().body());
            }
        });

        return promise;
    }


    private WebClient createOrGetWebClient() {
        if(Objects.isNull(webClient)) {
            WebClientOptions webClientOptions = new WebClientOptions();
            webClientOptions.setTcpFastOpen(true)
                    .setTcpNoDelay(true)
                    .setTcpQuickAck(true)
                    .setTrustAll(true)
                    .setReusePort(true);

            webClient = WebClient.create(vertx, webClientOptions);
        }
        return  webClient;
    }
}
