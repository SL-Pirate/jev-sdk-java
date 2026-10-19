package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request.JevSingleQuestionRequest;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.ANIMAL_CHOICES;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.ENERGY_CRITERIA;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.STATE;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.animalQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.energyQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.foxFastQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Talks to the live Jev API. Needs {@code JEV_KEY}, which build.gradle.kts loads from a
 * local {@code .env}; without it these tests are skipped. Assertions check the SDK's
 * plumbing (shape and ranges), not the model's opinions, so they stay stable across runs.
 */
@Tag("integration")
@EnabledIfEnvironmentVariable(named = "JEV_KEY", matches = ".+",
        disabledReason = "JEV_KEY is not set; add it to .env to run live API tests")
class JevClientIntegrationTest {
    private final JevClient client = new JevClient(System.getenv("JEV_KEY"));

    private static void assertProbability(double value) {
        assertTrue(value >= 0.0 && value <= 1.0, "expected a value in [0, 1] but got " + value);
    }

    @Test
    void answersAllThreeQuestionTypesInOneRequest() throws Exception {
        var animal = animalQuestion();
        var energy = energyQuestion();
        var fox = foxFastQuestion();
        var request = JevRequest.builder()
                .state(STATE)
                .questions(Map.of("animal", animal, "energy", energy, "fox_fast", fox))
                .build();

        var response = client.sendRequest(request);

        assertFalse(response.model().isBlank());
        assertTrue(response.usage().inputTokens() > 0);
        assertTrue(response.usage().outputTokens() > 0);
        assertEquals(Set.of("animal", "energy", "fox_fast"), response.answers().keySet());

        var choice = animal.getAnswerFromResponse(response);
        assertTrue(ANIMAL_CHOICES.containsKey(choice.choice), "unexpected choice " + choice.choice);
        assertEquals(ANIMAL_CHOICES.keySet(), choice.probabilities.keySet());
        assertProbability(choice.confidence);

        var score = energy.getAnswerFromResponse(response);
        assertEquals(ENERGY_CRITERIA.size(), score.legend.size());
        assertEquals(score.legend.keySet(), score.probabilities.keySet());
        assertProbability(score.confidence);

        assertProbability(fox.getAnswerFromResponse(response).noul);
    }

    @Test
    void singleQuestionRequestReturnsATypedAnswer() throws Exception {
        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state(STATE)
                .question(animalQuestion())
                .build();

        var response = client.sendSingleQuestionRequest(request);

        assertFalse(response.model().isBlank());
        assertNotNull(response.usage());
        assertTrue(ANIMAL_CHOICES.containsKey(response.answer().choice));
        assertProbability(response.answer().confidence);
    }

    @Test
    void rejectsAnInvalidApiKeyWithTheHttpStatus() {
        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state(STATE)
                .question(animalQuestion())
                .build();

        var error = assertThrows(IOException.class,
                () -> new JevClient("not-a-real-key").sendSingleQuestionRequest(request));

        assertTrue(error.getMessage().startsWith("HTTP 4"), error.getMessage());
    }
}
