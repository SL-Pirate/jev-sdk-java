package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.request;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevChoiceAnswer;
import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer.JevNoulAnswer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.STATE;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.animalQuestion;
import static com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.JevFixtures.foxFastQuestion;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JevSingleQuestionRequestTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void wrapsTheQuestionUnderTheDefaultKey() {
        var question = foxFastQuestion();

        var request = JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder()
                .state(STATE)
                .question(question)
                .build();

        assertEquals(Map.of(JevSingleQuestionRequest.DEFAULT_QUESTION_KEY, question), request.questions);
        assertSame(question, request.getQuestion());
        assertEquals(STATE, request.state);
        assertEquals("jev-latest", request.model);
    }

    @Test
    void wrapsTheQuestionUnderACustomKey() {
        var question = animalQuestion();

        var request = JevSingleQuestionRequest.<JevChoiceAnswer>singleQuestionBuilder()
                .state(STATE)
                .model("jev-1.13.0")
                .questionKey("animal")
                .question(question)
                .build();

        assertEquals(Map.of("animal", question), request.questions);
        assertEquals("jev-1.13.0", request.model);
    }

    @Test
    void rejectsAMissingQuestion() {
        assertThrows(NullPointerException.class,
                () -> JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder().state(STATE).build());
    }

    @Test
    void serializesExactlyLikeAMultiQuestionRequest() {
        var request = JevSingleQuestionRequest.<JevNoulAnswer>singleQuestionBuilder()
                .state(STATE)
                .question(foxFastQuestion())
                .build();

        var actual = mapper.readTree(mapper.writeValueAsString(request));

        var expected = mapper.readTree("""
                {
                  "model": "jev-latest",
                  "state": "%s",
                  "questions": {"question": {"type": "noul", "instructions": "Is the fox fast?"}}
                }
                """.formatted(STATE));
        assertEquals(expected, actual);
        assertFalse(actual.has("question"), "the convenience accessor must not leak into the wire format");
    }
}
