package dev.isira.jev.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JevType {
    NOUL("noul"),
    CHOICE("choice"),
    SCORE("score");

    @JsonValue
    public final String value;

    JevType(String value) {
        this.value = value;
    }
}
