package dev.isira.jev;

import dev.isira.jev.dto.JevUsage;
import dev.isira.jev.dto.request.question.JevChoiceQuestion;
import dev.isira.jev.dto.request.question.JevNoulQuestion;
import dev.isira.jev.dto.request.question.JevScoreQuestion;
import dev.isira.jev.dto.response.JevResponse;
import dev.isira.jev.dto.response.answer.JevChoiceAnswer;
import dev.isira.jev.dto.response.answer.JevNoulAnswer;
import dev.isira.jev.dto.response.answer.JevScoreAnswer;

import java.util.List;
import java.util.Map;

/**
 * Shared sample data for the test suite.
 * <p>
 * {@link #REQUEST_JSON} is a body the Jev API ({@code POST /v1/systemone}) accepted, and
 * {@link #RESPONSE_JSON} is the response it returned for it, so the unit tests exercise
 * the real wire format rather than an invented one.
 */
public final class JevFixtures {
    private JevFixtures() {
    }

    public static final String STATE =
            "The quick brown fox jumps over the lazy dog. The fox is fast and the dog is sleepy.";

    public static final Map<String, Object> ANIMAL_CHOICES = Map.of("fox", "the fox", "dog", "the dog");
    public static final List<Object> ENERGY_CRITERIA =
            List.of("1: asleep", "2: lazy", "3: neutral", "4: active", "5: hyper");

    public static JevChoiceQuestion animalQuestion() {
        return new JevChoiceQuestion("Which animal is described as fast?", ANIMAL_CHOICES);
    }

    public static JevScoreQuestion energyQuestion() {
        return new JevScoreQuestion("How energetic is the dog?", ENERGY_CRITERIA);
    }

    public static JevNoulQuestion foxFastQuestion() {
        return new JevNoulQuestion("Is the fox fast?");
    }

    public static final String REQUEST_JSON = """
            {
              "model": "jev-latest",
              "state": "The quick brown fox jumps over the lazy dog. The fox is fast and the dog is sleepy.",
              "questions": {
                "animal": {
                  "type": "choice",
                  "instructions": "Which animal is described as fast?",
                  "criteria": {"fox": "the fox", "dog": "the dog"}
                },
                "energy": {
                  "type": "score",
                  "instructions": "How energetic is the dog?",
                  "criteria": ["1: asleep", "2: lazy", "3: neutral", "4: active", "5: hyper"]
                },
                "fox_fast": {
                  "type": "noul",
                  "instructions": "Is the fox fast?"
                }
              }
            }
            """;

    public static final String RESPONSE_JSON = """
            {
              "model": "jev-1.13.0",
              "answers": {
                "animal": {
                  "type": "choice",
                  "choice": "fox",
                  "confidence": 1.0,
                  "probabilities": {"fox": 1.0, "dog": 0.0}
                },
                "energy": {
                  "type": "score",
                  "score": 0.95,
                  "confidence": 0.95,
                  "legend": {"0": "1: asleep", "1": "2: lazy", "2": "3: neutral", "3": "4: active", "4": "5: hyper"},
                  "probabilities": {"0": 0.06, "1": 0.94, "2": 0.0, "3": 0.0, "4": 0.0}
                },
                "fox_fast": {
                  "type": "noul",
                  "noul": 0.99
                }
              },
              "usage": {"input_tokens": 400, "output_tokens": 62}
            }
            """;

    /** The same response as {@link #RESPONSE_JSON}, built directly as objects. */
    public static JevResponse response() {
        return new JevResponse(
                "jev-1.13.0",
                Map.of(
                        "animal", new JevChoiceAnswer("fox", 1.0, Map.of("fox", 1.0, "dog", 0.0)),
                        "energy", new JevScoreAnswer(0.95, 0.95,
                                Map.of("0", "1: asleep", "1", "2: lazy", "2", "3: neutral", "3", "4: active", "4", "5: hyper"),
                                Map.of("0", 0.06, "1", 0.94, "2", 0.0, "3", 0.0, "4", 0.0)),
                        "fox_fast", new JevNoulAnswer(0.99)),
                new JevUsage(400, 62));
    }
}
