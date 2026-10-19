package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevUsage;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.RESPONSE_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Drives the handler through a real {@link HttpClient} against a loopback server, since
 * the handler's job is to turn an HTTP body into a {@link JevResponse}.
 */
class JevResponseHandlerTest {
    private static HttpServer server;
    private static volatile int status = 200;
    private static volatile byte[] body = new byte[0];

    @BeforeAll
    static void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            try (var out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    private static HttpResponse<JevResponse> fetch(int responseStatus, String responseBody) throws Exception {
        status = responseStatus;
        body = responseBody.getBytes(StandardCharsets.UTF_8);
        var uri = URI.create("http://127.0.0.1:" + server.getAddress().getPort() + "/");
        try (var client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder(uri).GET().build(), new JevResponseHandler());
        }
    }

    @Test
    void parsesJsonBodyIntoJevResponse() throws Exception {
        var response = fetch(200, RESPONSE_JSON);

        assertEquals(200, response.statusCode());
        var parsed = response.body();
        assertEquals("jev-1.13.0", parsed.model());
        assertEquals(new JevUsage(400, 62), parsed.usage());
        assertEquals("fox", assertInstanceOf(JevChoiceAnswer.class, parsed.answers().get("animal")).choice);
    }

    @Test
    void parsesBodyRegardlessOfStatusCodeSoCallersCanInspectBoth() throws Exception {
        var response = fetch(503, "{\"model\": \"jev-1.13.0\", \"answers\": {}, \"usage\": null}");

        assertEquals(503, response.statusCode());
        assertEquals("jev-1.13.0", response.body().model());
    }

    @Test
    void failsOnMalformedBody() {
        assertThrows(IOException.class, () -> fetch(200, "<html>not json</html>"));
    }
}
