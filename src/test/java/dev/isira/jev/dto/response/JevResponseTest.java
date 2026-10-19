package dev.isira.jev.dto.response;

import dev.isira.jev.dto.JevType;
import dev.isira.jev.dto.JevUsage;
import dev.isira.jev.dto.response.answer.JevChoiceAnswer;
import dev.isira.jev.dto.response.answer.JevNoulAnswer;
import dev.isira.jev.dto.response.answer.JevScoreAnswer;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Set;

import static dev.isira.jev.JevFixtures.RESPONSE_JSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JevResponseTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private JevResponse read() {
        return mapper.readValue(RESPONSE_JSON, JevResponse.class);
    }

    @Test
    void deserializesModelUsageAndAnswerKeys() {
        var response = read();

        assertEquals("jev-1.13.0", response.model());
        assertEquals(new JevUsage(400, 62), response.usage());
        assertEquals(Set.of("animal", "energy", "fox_fast"), response.answers().keySet());
    }

    @Test
    void usageMapsSnakeCaseTokenCounts() {
        var usage = mapper.readValue("{\"input_tokens\": 7, \"output_tokens\": 3}", JevUsage.class);

        assertEquals(7, usage.inputTokens());
        assertEquals(3, usage.outputTokens());
    }

    @Test
    void deserializesChoiceAnswerFromTypeTag() {
        var answer = assertInstanceOf(JevChoiceAnswer.class, read().answers().get("animal"));

        assertEquals(JevType.CHOICE, answer.type);
        assertEquals("fox", answer.choice);
        assertEquals(1.0, answer.confidence);
        assertEquals(Map.of("fox", 1.0, "dog", 0.0), answer.probabilities);
    }

    @Test
    void deserializesScoreAnswerFromTypeTag() {
        var answer = assertInstanceOf(JevScoreAnswer.class, read().answers().get("energy"));

        assertEquals(JevType.SCORE, answer.type);
        assertEquals(0.95, answer.score);
        assertEquals(0.95, answer.confidence);
        assertEquals(Map.of("0", "1: asleep", "1", "2: lazy", "2", "3: neutral", "3", "4: active", "4", "5: hyper"),
                answer.legend);
        assertEquals(Map.of("0", 0.06, "1", 0.94, "2", 0.0, "3", 0.0, "4", 0.0), answer.probabilities);
    }

    @Test
    void deserializesNoulAnswerFromTypeTag() {
        var answer = assertInstanceOf(JevNoulAnswer.class, read().answers().get("fox_fast"));

        assertEquals(JevType.NOUL, answer.type);
        assertEquals(0.99, answer.noul);
    }

    @Test
    void ignoresUnknownFieldsSoNewApiFieldsDoNotBreakClients() {
        var json = """
                {
                  "model": "jev-1.13.0",
                  "request_id": "abc-123",
                  "answers": {"q": {"type": "noul", "noul": 0.5, "explanation": "added later"}},
                  "usage": {"input_tokens": 1, "output_tokens": 2, "cached_tokens": 0}
                }
                """;

        var response = mapper.readValue(json, JevResponse.class);

        assertEquals(0.5, assertInstanceOf(JevNoulAnswer.class, response.answers().get("q")).noul);
        assertEquals(new JevUsage(1, 2), response.usage());
    }

    @Test
    void rejectsUnknownAnswerType() {
        var json = """
                {"model": "m", "answers": {"q": {"type": "essay", "text": "..."}}, "usage": {"input_tokens": 1, "output_tokens": 1}}
                """;

        assertThrows(DatabindException.class, () -> mapper.readValue(json, JevResponse.class));
    }
}
