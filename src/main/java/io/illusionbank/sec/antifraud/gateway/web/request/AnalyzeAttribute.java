package io.illusionbank.sec.antifraud.gateway.web.request;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AnalyzeAttribute {
    private String key;
    private String value;
}
