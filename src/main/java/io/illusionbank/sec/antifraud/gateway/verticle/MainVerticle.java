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
import java.util.stream.Collectors;

@Slf4j
public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    AgencyRepository agencyRepository = new AgencyRepository(vertx);
    JsonFileReader jsonFileReader = new JsonFileReader(vertx);

    CompositeFuture dependenciesFuture = CompositeFuture.all(
      jsonFileReader.getFilesFromDirectory("env"),
      agencyRepository.findAllAgencies()
    );

    dependenciesFuture.onComplete(adependenciesResult->{
      if(adependenciesResult.failed()) {
        log.error("Erro ao buscar dependencias(Agencias e Transações) para o verticle");
        startPromise.fail(adependenciesResult.cause());

      } else {
        completeWithDependencies(startPromise, adependenciesResult.result().list());

      }

    });
  }


  private void completeWithDependencies(Promise<Void> startPromise, List filesAnAgenciesAsList) {
    final Optional<List<Agency>> agenciesOptional = filesAnAgenciesAsList.parallelStream()
            .filter(i->i instanceof List)
            .findFirst();

    final Optional<Map<String, FileEntry>> filesOptional = filesAnAgenciesAsList.parallelStream()
            .filter(i->i instanceof Map)
            .findFirst();

    List<Agency> agencies = agenciesOptional.orElseGet(()-> Collections.emptyList());
    Map<String, FileEntry> filesMap = filesOptional.orElseGet(()-> Collections.emptyMap());

    JsonArray transactionsJsonArray = filesMap.get("transactions.json").getContent().toJsonArray();

    List<Transaction> transactions = transactionsJsonArray.stream()
            .map(json->((JsonObject)json).mapTo(Transaction.class))
            .collect(Collectors.toList());

    deployWebServerVerticleWithDependenciesAndCompletePromise(startPromise, agencies, transactions);
  }

  private void deployWebServerVerticleWithDependenciesAndCompletePromise(Promise<Void> startPromise, List<Agency> agencies, List<Transaction> transactions) {
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
