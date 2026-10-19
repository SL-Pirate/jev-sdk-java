package dev.isira.jev.dto.request;

import dev.isira.jev.dto.request.question.JevQuestion;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static dev.isira.jev.JevFixtures.REQUEST_JSON;
import static dev.isira.jev.JevFixtures.STATE;
import static dev.isira.jev.JevFixtures.animalQuestion;
import static dev.isira.jev.JevFixtures.energyQuestion;
import static dev.isira.jev.JevFixtures.foxFastQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JevRequestTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, JevQuestion<?>> threeQuestions() {
        return Map.of("animal", animalQuestion(), "energy", energyQuestion(), "fox_fast", foxFastQuestion());
    }

    @Test
    void builderDefaultsModelToJevLatest() {
        var request = JevRequest.builder().state(STATE).questions(threeQuestions()).build();

        assertEquals("jev-latest", request.model);
    }

    @Test
    void builderKeepsAnExplicitModel() {
        var request = JevRequest.builder().state(STATE).model("jev-1.13.0").questions(threeQuestions()).build();

        assertEquals("jev-1.13.0", request.model);
    }

    @Test
    void twoArgumentConstructorDefaultsModelToJevLatest() {
        var request = new JevRequest(STATE, threeQuestions());

        assertEquals("jev-latest", request.model);
    }

    @Test
    void exposesStateAndQuestionsAsGiven() {
        var questions = threeQuestions();

        var request = JevRequest.builder().state(STATE).questions(questions).build();

        assertEquals(STATE, request.state);
        assertSame(questions, request.questions);
    }

    @Test
    void rejectsMissingQuestions() {
        assertThrows(NullPointerException.class, () -> JevRequest.builder().state(STATE).build());
        assertThrows(NullPointerException.class, () -> new JevRequest(STATE, null));
    }

    @Test
    void serializesToTheWireFormatTheApiAccepts() {
        var request = JevRequest.builder().state(STATE).questions(threeQuestions()).build();

        var actual = mapper.readTree(mapper.writeValueAsString(request));

        assertEquals(mapper.readTree(REQUEST_JSON), actual);
    }
}
