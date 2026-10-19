package dev.isira.jev.dto.response.answer;

import dev.isira.jev.dto.JevType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class JevChoiceAnswer extends JevAnswer {
    public final String choice;
    public final double confidence;
    public final Map<String, Double> probabilities;

    // Explicit creator so the JSON mapping does not depend on the build keeping
    // constructor parameter names (-parameters) or on the consumer's Jackson setup.
    @JsonCreator
    public JevChoiceAnswer(
            @JsonProperty("choice") String choice,
            @JsonProperty("confidence") double confidence,
            @JsonProperty("probabilities") Map<String, Double> probabilities
    ) {
        super(JevType.CHOICE);
        this.choice = choice;
        this.confidence = confidence;
        this.probabilities = probabilities;
    }
}
