package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevUsage;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevAnswer;

public record JevSingleAnswerResponse<T extends JevAnswer>(String model, T answer, JevUsage usage) {
}
