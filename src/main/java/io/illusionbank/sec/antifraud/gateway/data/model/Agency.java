package io.illusionbank.sec.antifraud.gateway.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Agency {
    private String code;
    private String state;
    private String city;
}
