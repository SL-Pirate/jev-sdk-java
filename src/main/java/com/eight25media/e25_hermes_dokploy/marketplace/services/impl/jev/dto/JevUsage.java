package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record JevUsage(@JsonProperty("input_tokens") int inputTokens, @JsonProperty("output_tokens") int outputTokens) {
}
