package dev.isira.jev;

import dev.isira.jev.dto.request.JevRequest;
import dev.isira.jev.dto.request.JevSingleQuestionRequest;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.JevResponseHandler;
import dev.isira.jev.dto.response.JevSingleAnswerResponse;
import dev.isira.jev.dto.response.answer.JevAnswer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

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
                .headers("Authorization", "Bearer " + apiKey, "Content-Type", "application/json")
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
