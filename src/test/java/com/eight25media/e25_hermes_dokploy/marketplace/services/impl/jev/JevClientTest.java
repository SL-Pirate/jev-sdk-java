package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevUsage;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevSingleQuestionRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.JevResponse;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevNoulAnswer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.STATE;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.animalQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.foxFastQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Covers the client logic that sits above the HTTP call. The call itself is pinned to the
 * live API URL, so it is exercised by {@link JevClientIntegrationTest} instead.
 */
class JevClientTest {
    /** Returns a canned response instead of talking to the network, and remembers what was sent. */
    private static final class StubClient extends JevClient {
        private final JevResponse canned;
        private JevRequest sent;

        StubClient(JevResponse canned) {
            super("test-key");
            this.canned = canned;
        }

        @Override
        public JevResponse sendRequest(JevRequest request) {
            sent = request;
            return canned;
        }
    }

    @Test
    void singleQuestionRequestIsSentUnchanged() throws Exception {
        var client = new StubClient(JevFixtures.response());
        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state(STATE).questionKey("animal").question(animalQuestion()).build();

        client.sendSingleQuestionRequest(request);

        assertSame(request, client.sent);
    }

    @Test
    void singleQuestionRequestUnwrapsTheAnswerForItsKey() throws Exception {
        var client = new StubClient(JevFixtures.response());
        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state(STATE).questionKey("animal").question(animalQuestion()).build();

        var response = client.sendSingleQuestionRequest(request);

        assertEquals("fox", response.answer().choice);
        assertEquals(1.0, response.answer().confidence);
        assertEquals("jev-1.13.0", response.model());
        assertEquals(new JevUsage(400, 62), response.usage());
    }

    @Test
    void singleQuestionRequestUsesTheDefaultKeyWhenNoneIsGiven() throws Exception {
        var client = new StubClient(new JevResponse("jev-1.13.0",
                Map.of(JevSingleQuestionRequest.DEFAULT_QUESTION_KEY, new JevNoulAnswer(0.25)),
                new JevUsage(10, 2)));
        var request = JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder()
                .state(STATE).question(foxFastQuestion()).build();

        var response = client.sendSingleQuestionRequest(request);

        assertEquals(0.25, response.answer().noul);
    }

    @Test
    void singleQuestionRequestYieldsNullAnswerWhenTheApiOmitsTheKey() throws Exception {
        var client = new StubClient(new JevResponse("jev-1.13.0", Map.of(), new JevUsage(10, 2)));
        var request = JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder()
                .state(STATE).question(foxFastQuestion()).build();

        var response = client.sendSingleQuestionRequest(request);

        assertNull(response.answer());
        assertEquals("jev-1.13.0", response.model());
    }
}
