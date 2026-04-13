package es.ggm.infor.softskills.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "soft-skills.ranking")
public class RankingProperties {

    private RankingMode mode = RankingMode.WEIGHTED_AVERAGE;
    private long confidenceTargetSamples = 20L;

    public RankingMode getMode() {
        return mode;
    }

    public void setMode(RankingMode mode) {
        this.mode = mode;
    }

    public long getConfidenceTargetSamples() {
        return confidenceTargetSamples;
    }

    public void setConfidenceTargetSamples(long confidenceTargetSamples) {
        this.confidenceTargetSamples = confidenceTargetSamples;
    }
}
