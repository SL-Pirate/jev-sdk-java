package dev.isira.jev.dto.response.answer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.isira.jev.dto.JevType;

public class JevBoxAnswer extends JevAnswer {
    public final Box box;
    public final double confidence;

    @JsonCreator
    public JevBoxAnswer(@JsonProperty("box") Box box, @JsonProperty("confidence") double confidence) {
        super(JevType.BOX);
        this.box = box;
        this.confidence = confidence;
    }

    public record Box(double top, double bottom, double left, double right) {
    }
}
