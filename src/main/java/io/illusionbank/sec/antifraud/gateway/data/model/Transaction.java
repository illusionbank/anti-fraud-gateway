package io.illusionbank.sec.antifraud.gateway.data.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter @Setter
public class Transaction {
    private String name;
    private String format;
    private String host;
    private Integer port;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Migration migration;

    public boolean hasMigration() {
        return Objects.nonNull(migration);
    }
}
