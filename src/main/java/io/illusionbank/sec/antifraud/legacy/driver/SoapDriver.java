package io.illusionbank.sec.antifraud.legacy.driver;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.driver.Driver;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoapDriver extends Driver {

    public SoapDriver(Vertx vertx, AnalyzeRequest request, Transaction currentTransaction) {
        super(vertx, request, currentTransaction);
    }

    @Override
    public Promise<JsonObject> call() {
        Promise promise = Promise.promise();
        return promise;
    }
}
