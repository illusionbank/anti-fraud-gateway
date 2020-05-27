package io.illusionbank.sec.antifraud.gateway.data.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ClientInfo {
    private Agency agency;
    private boolean employeer;
    private String segment;
}
