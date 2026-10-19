package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevSingleQuestionRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponseHandler;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevSingleAnswerResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevAnswer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;
import xyz.elemental.mediatype.MediaType;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
public class JevClient {
    private final String apiKey;
    private final ObjectMapper MAPPER = new ObjectMapper();
    private final HttpClient restClient = HttpClient.newHttpClient();
    private final JevResponseHandler responseHandler = new JevResponseHandler();

    public JevResponse sendRequest(JevRequest request) throws IOException, InterruptedException {
        final var req = HttpRequest.newBuilder(URI.create("https://api.typesafe.ai/v1/systemone"))
                .headers("Authorization", "Bearer " + apiKey, "Content-Type", MediaType.APPLICATION_JSON)
                .POST((HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(request))))
                .build();
        final HttpResponse<JevResponse> res = restClient.send(req, responseHandler);
        if (res.statusCode() / 100 != 2) throw new IOException("HTTP " + res.statusCode());
        return res.body();
    }

    public <A extends JevAnswer> @NonNull JevSingleAnswerResponse<A> sendSingleQuestionRequest(JevSingleQuestionRequest<A> request) throws IOException, InterruptedException {
        final var response = sendRequest(request);
        final var answer = request.getQuestion().getAnswerFromResponse(response);
        return new JevSingleAnswerResponse<>(response.model(), answer, response.usage());
    }
}
