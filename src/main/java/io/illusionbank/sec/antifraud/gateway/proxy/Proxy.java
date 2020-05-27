package io.illusionbank.sec.antifraud.gateway.proxy;

import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public interface Proxy {
    Promise<JsonObject> call();
}
