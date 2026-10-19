package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JevTypeTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @ParameterizedTest
    @CsvSource({"NOUL,noul", "CHOICE,choice", "SCORE,score"})
    void serializesAsLowercaseWireValue(JevType type, String wire) {
        assertEquals("\"" + wire + "\"", mapper.writeValueAsString(type));
    }

    @ParameterizedTest
    @CsvSource({"NOUL,noul", "CHOICE,choice", "SCORE,score"})
    void deserializesFromWireValue(JevType type, String wire) {
        assertEquals(type, mapper.readValue("\"" + wire + "\"", JevType.class));
    }

    @ParameterizedTest
    @CsvSource({"NOUL,noul", "CHOICE,choice", "SCORE,score"})
    void exposesWireValueAsField(JevType type, String wire) {
        assertEquals(wire, type.value);
    }
}
