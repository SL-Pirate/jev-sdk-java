package dev.isira.jev.dto.response.answer;

import dev.isira.jev.dto.JevType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class JevScoreAnswer extends JevAnswer {
    public final double score;
    public final double confidence;
    public final Map<String, String> legend;
    public final Map<String, Double> probabilities;

    // Explicit creator so the JSON mapping does not depend on the build keeping
    // constructor parameter names (-parameters) or on the consumer's Jackson setup.
    @JsonCreator
    public JevScoreAnswer(
            @JsonProperty("score") double score,
            @JsonProperty("confidence") double confidence,
            @JsonProperty("legend") Map<String, String> legend,
            @JsonProperty("probabilities") Map<String, Double> probabilities
    ) {
        super(JevType.SCORE);
        this.score = score;
        this.confidence = confidence;
        this.legend = legend;
        this.probabilities = probabilities;
    }
}
