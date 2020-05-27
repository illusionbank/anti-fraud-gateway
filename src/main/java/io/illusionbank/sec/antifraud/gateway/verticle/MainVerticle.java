package io.illusionbank.sec.antifraud.gateway.verticle;

import io.illusionbank.sec.antifraud.gateway.data.model.Agency;
import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.data.repository.AgencyRepository;
import io.illusionbank.sec.antifraud.gateway.io.FileEntry;
import io.illusionbank.sec.antifraud.gateway.io.JsonFileReader;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    AgencyRepository agencyRepository = new AgencyRepository(vertx);
    JsonFileReader jsonFileReader = new JsonFileReader(vertx);

    CompositeFuture dependencies = CompositeFuture.all(
      jsonFileReader.loadAndGroupFiles("env"),
      agencyRepository.findAgencies()
    );

    dependencies.onComplete(adependenciesResult->{
      if(adependenciesResult.succeeded()) {
        List list = adependenciesResult.result().list();
        Optional<List<Agency>> agenciesOptional = list.parallelStream().filter(i->i instanceof List).findFirst();
        Optional<Map<String, FileEntry>> filesOptional = list.parallelStream().filter(i->i instanceof Map).findFirst();

        List<Agency> agencies = agenciesOptional.map(Function.identity()).orElseGet(()-> Collections.emptyList());
        Map<String, FileEntry> files = filesOptional.map(Function.identity()).orElseGet(()-> Collections.emptyMap());

        JsonArray transactionsJsonArray = files.get("transactions.json").getContent().toJsonArray();

        List<Transaction> transactions = transactionsJsonArray.stream()
                .map(a->(JsonObject)a)
                .map(json->json.mapTo(Transaction.class))
                .collect(Collectors.toList());

        startHttpServerVerticleWithDependencies(startPromise, agencies, transactions);

      } else {
        startPromise.fail(adependenciesResult.cause());
      }
    });
  }

  private void startHttpServerVerticleWithDependencies(Promise<Void> startPromise, List<Agency> agencies, List<Transaction> transactions) {
    vertx.deployVerticle(new WebServerVerticle(agencies, transactions), handler-> {
      if(handler.succeeded()) {
        log.info("WebServerVerticle - Deploy finalizado");
        startPromise.complete();
      } else {
        log.error("WebServerVerticle Error, {}", handler.cause());
        startPromise.fail(handler.cause());
      }
    });
  }
}
