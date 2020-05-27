package io.illusionbank.sec.antifraud.gateway.web.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter @Setter
public class AnalyzeRequest {
    private String transaction;
    private String channel;
    private String client;
    private String agency;
    private List<AnalyzeAttribute> attributes;

    public Map<String, Object> attributesToMap() {
        return attributes.parallelStream().collect(Collectors.toMap(AnalyzeAttribute::getKey, Function.identity()));
    }

}


