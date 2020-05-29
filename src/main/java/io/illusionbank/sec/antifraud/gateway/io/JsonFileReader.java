package io.illusionbank.sec.antifraud.gateway.io;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class JsonFileReader {
    private Vertx vertx;

    public JsonFileReader(Vertx vertx) {
        this.vertx = vertx;
    }

    private Promise<FileEntry> loadOneFile(String path) {
        Promise promise = Promise.promise();
        vertx.fileSystem().readFile(path, result-> {
            if(result.succeeded()) {
                promise.tryComplete(new FileEntry(result.result(), path));
            } else {
                promise.tryFail(result.cause());
            }
        });
        return promise;
    }

    private Map<String, FileEntry> toGroup(List<FileEntry> files) {
        Map<String, FileEntry> filesEntryAsMap = new HashMap<>();
        files.parallelStream().map(fe->(FileEntry)fe)
                .forEach(f-> filesEntryAsMap.put(f.getName(), f));
        return filesEntryAsMap;
    }

    private List<Future> loadAndGetFiles(List<String> files) {
        return files.parallelStream()
            .map(path-> loadOneFile(path))
            .map(promisse-> promisse.future())
            .collect(Collectors.toList());
    }

    public Future<Map<String, FileEntry>> getFilesFromDirectory(String directory) {
        Promise promise = Promise.promise();
        vertx.fileSystem().readDir(directory, filesResult -> {
            if (filesResult.succeeded()) {
                CompositeFuture aaa = CompositeFuture.all(loadAndGetFiles(filesResult.result()));
                aaa.onComplete(bbbb -> {
                    if (bbbb.succeeded()) {
                        promise.complete(toGroup(bbbb.result().list()));
                    } else {
                        promise.fail(bbbb.cause());
                    }
                });
            } else {
                promise.fail(filesResult.cause());
            }
        });
        return  promise.future();
    }
}
