package io.illusionbank.sec.antifraud.gateway;

import io.illusionbank.sec.antifraud.gateway.verticle.MainVerticle;
import io.vertx.core.Launcher;

public class MainGateway {

    public static void main(String[] args) {
        Launcher.executeCommand("run", MainVerticle.class.getName());
    }
}
