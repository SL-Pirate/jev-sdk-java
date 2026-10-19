package dev.isira.jev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JevUsage(@JsonProperty("input_tokens") int inputTokens, @JsonProperty("output_tokens") int outputTokens) {
}
