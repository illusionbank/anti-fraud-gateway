package io.illusionbank.sec.antifraud.gateway.data.repository;

import io.illusionbank.sec.antifraud.gateway.data.model.Agency;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class AgencyRepository {

    private Vertx vertx;

    public AgencyRepository(Vertx vertx) {
        this.vertx = vertx;
    }

    private RedisAPI redisAPI() {
        RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
        Redis redis = Redis.createClient(vertx, options);
        return RedisAPI.api(redis);
    }

    public Future findAgencies() {
        Promise promise = Promise.promise();
        redisAPI().keys("agency:*", h-> {
            if(h.succeeded()) {
                loadAgenciesByKeysAndCompletePromise(h.result(), promise);
            } else {
                promise.fail(h.cause());
            }
        });
        return promise.future();
    }

    private void loadAgenciesByKeysAndCompletePromise(Response keysResponse, Promise promise) {
        CompositeFuture compositeFuture = CompositeFuture.all(aaa(keysResponse));
        compositeFuture.onComplete(agencies->{
            if (agencies.succeeded()) {
                List<Agency> list = agencies.result().list();
                promise.complete(list);
            } else {
                promise.fail(agencies.cause());
            }
        });
    }

    private List<Future> aaa(Response keysResponse) {
        return keysResponse.stream()
                .map(key->loadAgencyByKey(key.toString()))
                .collect(Collectors.toList());
    }

    private Future<Agency> loadAgencyByKey(String key) {
        Promise promise = Promise.promise();
        redisAPI().hgetall(key, hh->{
            if(hh.succeeded()) {
                Response response = hh.result();
                String code = response.get("code").toString();
                String state = response.get("state").toString();
                String city = response.get("city").toString();
                promise.complete(new Agency(code, state, city));
            } else {
                promise.fail(hh.cause());
            }
        });
        return promise.future();
    }

}
