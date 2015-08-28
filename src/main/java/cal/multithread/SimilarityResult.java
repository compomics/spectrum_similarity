/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.multithread;

import cal.methods.SimilarityMethods;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.EnumMap;

/**
 * This class holds all information to run multithreaded Game
 *
 * @author Sule
 */
public class SimilarityResult {

    private String spectrumName,
            spectrumChargeAsString,
            tmpSpectrumName = "",
            tmpSpectrumChargeAsString = "";
    private MSnSpectrum bestSimilarSpec;
    private double spectrumPrecursorMZ,
            score;
    private EnumMap<SimilarityMethods, Double> scores = new EnumMap<SimilarityMethods, Double>(SimilarityMethods.class);

    public SimilarityResult(String spectrumName, MSnSpectrum bestSimilarSpec, String spectrumChargeAsString, double spectrumPrecursorMZ) {
        this.spectrumName = spectrumName;
        this.bestSimilarSpec = bestSimilarSpec;
        this.spectrumChargeAsString = spectrumChargeAsString;
        this.spectrumPrecursorMZ = spectrumPrecursorMZ;
        scores.put(SimilarityMethods.DOT_PRODUCT, Double.MIN_VALUE);
        scores.put(SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD, Double.MIN_VALUE);
        scores.put(SimilarityMethods.PEARSONS_CORRELATION, Double.MIN_VALUE);
        scores.put(SimilarityMethods.SPEARMANS_CORRELATION, Double.MIN_VALUE);
        scores.put(SimilarityMethods.MSRobin, Double.MIN_VALUE);
        scores.put(SimilarityMethods.MEAN_SQUARED_ERROR, Double.MAX_VALUE);
    }

    public MSnSpectrum getBestSimilarSpec() {
        return bestSimilarSpec;
    }

    public void setBestSimilarSpec(MSnSpectrum bestSimilarSpec) {
        this.bestSimilarSpec = bestSimilarSpec;
    }

    public String getSpectrumName() {
        return spectrumName;
    }

    public String getSpectrumChargeAsString() {
        return spectrumChargeAsString;
    }

    public double getSpectrumPrecursorMZ() {
        return spectrumPrecursorMZ;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public EnumMap<SimilarityMethods, Double> getScores() {
        return scores;
    }

    public void setScores(EnumMap<SimilarityMethods, Double> scores) {
        this.scores = scores;
    }

    public String getTmpSpectrumName() {
        return tmpSpectrumName;
    }

    public void setTmpSpectrumName(String tmpSpectrumName) {
        this.tmpSpectrumName = tmpSpectrumName;
    }

    public String getTmpSpectrumChargeAsString() {
        return tmpSpectrumChargeAsString;
    }

    public void setTmpSpectrumChargeAsString(String tmpSpectrumChargeAsString) {
        this.tmpSpectrumChargeAsString = tmpSpectrumChargeAsString;
    }

    /**
     * This method updates a score if the same spectrum with higher similarity
     * score MSE has best scores as the lowest score
     *
     * @param similarityMethods
     * @param score
     */
    public void updateScore(SimilarityMethods similarityMethods, double score) {
        if (score > scores.get(similarityMethods) && (!similarityMethods.equals(SimilarityMethods.MEAN_SQUARED_ERROR))) {
            scores.put(similarityMethods, score);
        }
        if (score < scores.get(similarityMethods) && (similarityMethods.equals(SimilarityMethods.MEAN_SQUARED_ERROR))) {
            scores.put(similarityMethods, score);
        }
    }
}
