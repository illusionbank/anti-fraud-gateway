package io.illusionbank.sec.antifraud.gateway.driver;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Driver {
    @NonNull
    protected Vertx vertx;

    @NonNull
    protected AnalyzeRequest request;

    @NonNull
    protected Transaction currentTransaction;

    public abstract Promise<JsonObject> call();
}
