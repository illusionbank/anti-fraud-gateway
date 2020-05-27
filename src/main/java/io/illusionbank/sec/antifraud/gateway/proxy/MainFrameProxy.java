package io.illusionbank.sec.antifraud.gateway.proxy;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MainFrameProxy implements Proxy {
    private AnalyzeRequest request;
    private Transaction currentTransaction;

    @Override
    public Promise<JsonObject> call() {
        Promise promise = Promise.promise();
        return promise;
    }
}
