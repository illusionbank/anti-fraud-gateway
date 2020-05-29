package io.illusionbank.sec.antifraud.legacy.command.mainframe;

import io.illusionbank.sec.antifraud.gateway.data.model.Transaction;
import io.illusionbank.sec.antifraud.gateway.web.request.AnalyzeRequest;
import io.illusionbank.sec.antifraud.legacy.command.SocketCommand;
import io.vertx.core.Vertx;

public class CommandFactory {
    public static SocketCommand createWith(Vertx vertx, AnalyzeRequest request, Transaction transaction) {
        return null;
    }
}
