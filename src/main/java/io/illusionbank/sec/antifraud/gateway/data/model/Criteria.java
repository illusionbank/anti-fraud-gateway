package io.illusionbank.sec.antifraud.gateway.data.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter @Setter
public class Criteria {
    private String criteria;
    private String[] values;

    public boolean matchValue(boolean valueFromRequest) {
        return matchValue(Boolean.toString(valueFromRequest));
    }

    public boolean matchValue(String valueFromRequest) {
        return Arrays.stream(values).filter(value-> value.equalsIgnoreCase(valueFromRequest)).count() > 0;
    }
}
