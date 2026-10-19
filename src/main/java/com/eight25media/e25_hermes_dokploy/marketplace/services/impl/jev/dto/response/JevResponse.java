package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevUsage;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevAnswer;

import java.util.Map;

public record JevResponse(String model, Map<String, JevAnswer> answers, JevUsage usage) {
}
