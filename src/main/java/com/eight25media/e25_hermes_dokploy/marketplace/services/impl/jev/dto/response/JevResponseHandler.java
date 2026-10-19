package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response;

import tools.jackson.databind.ObjectMapper;

import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.ResponseInfo;

public class JevResponseHandler implements BodyHandler<JevResponse> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public HttpResponse.BodySubscriber<JevResponse> apply(ResponseInfo responseInfo) {
        return HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofByteArray(),
                body -> mapper.readValue(body, JevResponse.class)
        );
    }
}
