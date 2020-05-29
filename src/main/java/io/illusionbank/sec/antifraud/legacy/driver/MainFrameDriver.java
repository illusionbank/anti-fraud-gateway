package io.illusionbank.sec.antifraud.legacy.driver;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.driver.Driver;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.illusionbank.sec.antifraud.legacy.command.SocketCommand;
import io.illusionbank.sec.antifraud.legacy.command.mainframe.CommandFactory;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainFrameDriver extends Driver {

    public MainFrameDriver(Vertx vertx, AnalyzeRequest request, Transaction currentTransaction) {
        super(vertx, request, currentTransaction);
    }

    @Override
    public Promise<JsonObject> call() {
        SocketCommand socketCommand = CommandFactory.createWith(vertx, request, currentTransaction);
        return socketCommand.execute();
    }
}
