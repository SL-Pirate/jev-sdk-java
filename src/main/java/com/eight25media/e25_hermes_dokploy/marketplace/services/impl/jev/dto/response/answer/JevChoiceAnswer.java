package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;

import java.util.Map;

public class JevChoiceAnswer extends JevAnswer {
    public final String choice;
    public final double confidence;
    public final Map<String, Double> probabilities;

    public JevChoiceAnswer(String choice, double confidence, Map<String, Double> probabilities) {
        super(JevType.CHOICE);
        this.choice = choice;
        this.confidence = confidence;
        this.probabilities = probabilities;
    }
}
