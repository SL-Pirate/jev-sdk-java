package com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.response.answer;

import com.eight25media.e25_hermes_dokploy.marketplace.services.impl.jev.dto.JevType;

import java.util.Map;

public class JevScoreAnswer extends JevAnswer {
    public final double score;
    public final double confidence;
    public final Map<String, String> legend;
    public final Map<String, Double> probabilities;

    public JevScoreAnswer(
            double score,
            double confidence,
            Map<String, String> legend,
            Map<String, Double> probabilities
    ) {
        super(JevType.SCORE);
        this.score = score;
        this.confidence = confidence;
        this.legend = legend;
        this.probabilities = probabilities;
    }
}
