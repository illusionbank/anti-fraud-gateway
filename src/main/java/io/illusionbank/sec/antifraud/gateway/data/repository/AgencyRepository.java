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
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class AgencyRepository {
    private Vertx vertx;
    private RedisAPI redisAPI;

    public AgencyRepository(Vertx vertx) {
        this.vertx = vertx;
    }

    private RedisAPI redisAPI() {
        if(Objects.isNull(redisAPI)) {
            RedisOptions options = new RedisOptions().setConnectionString("redis://localhost:6379");
            Redis redis = Redis.createClient(vertx, options);
            redisAPI = RedisAPI.api(redis);
        }
        return redisAPI;
    }

    public Future findAllAgencies() {
        Promise promise = Promise.promise();
        redisAPI().keys("agency:*", keysResult-> {
            if(keysResult.succeeded()) {
                loadAgenciesByKeysAndCompletePromise(keysResult.result(), promise);
            } else {
                promise.fail(keysResult.cause());
            }
        });
        return promise.future();
    }

    private void loadAgenciesByKeysAndCompletePromise(Response keysResponse, Promise promise) {
        CompositeFuture compositeFuture = CompositeFuture.all(loadAgencies(keysResponse));
        compositeFuture.onComplete(agenciesResult-> {
            if (agenciesResult.succeeded()) {
                List<Agency> list = agenciesResult.result().list();
                promise.complete(list);
            } else {
                promise.fail(agenciesResult.cause());
            }
        });
    }

    private List<Future> loadAgencies(Response keysResponse) {
        return keysResponse.stream()
                .map(key->loadAgencyByKey(key.toString()))
                .collect(Collectors.toList());
    }

    private Future<Agency> loadAgencyByKey(String key) {
        Promise promise = Promise.promise();
        redisAPI().hgetall(key, agencyResult-> {
            if(agencyResult.succeeded()) {
                Response response = agencyResult.result();
                String code = response.get("code").toString();
                String state = response.get("state").toString();
                String city = response.get("city").toString();
                promise.complete(new Agency(code, state, city));
            } else {
                promise.fail(agencyResult.cause());
            }
        });
        return promise.future();
    }

}
