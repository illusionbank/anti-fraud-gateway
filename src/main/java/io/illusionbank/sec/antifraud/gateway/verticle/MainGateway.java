package io.illusionbank.sec.antifraud.gateway.verticle;

import io.vertx.core.Launcher;

public class MainGateway {

    public static void main(String[] args) {
        Launcher.executeCommand("run", MainVerticle.class.getName());
    }
}
