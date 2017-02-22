/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.multithread;

import cal.methods.SimilarityMethods;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * This class holds all information to run multithreaded
 *
 * @author Sule
 */
public class SimilarityResult {

    private String spectrumName,
            spectrumChargeAsString,
            spectrumToCompare = "",
            spectrumToCompareChargeAsString = "";
    private MSnSpectrum bestSimilarSpec;
    private double spectrumPrecursorMZ,
            score;
    private EnumMap<SimilarityMethods, Double> scores = new EnumMap<SimilarityMethods, Double>(SimilarityMethods.class);
    private ArrayList<PairwiseComparison> allPairwiseComparisons = new ArrayList<PairwiseComparison>();

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

    public String getSpectrumToCompare() {
        return spectrumToCompare;
    }

    public void setSpectrumToCompareName(String spectrumToCompare) {
        this.spectrumToCompare = spectrumToCompare;
    }

    public String getSpectrumToCompareChargeAsString() {
        return spectrumToCompareChargeAsString;
    }

    public void setspectrumToCompareChargeAsString(String spectrumToCompareChargeAsString) {
        this.spectrumToCompareChargeAsString = spectrumToCompareChargeAsString;
    }

    public ArrayList<PairwiseComparison> getAllPairwiseComparisons() {
        return allPairwiseComparisons;
    }

    public void setAllPairwiseComparisons(ArrayList<PairwiseComparison> allPairwiseComparisons) {
        this.allPairwiseComparisons = allPairwiseComparisons;
    }

    
    /**
     * This method updates a score if the same spectrum with higher similarity
     * score MSE has best scores as the lowest score
     *
     * @param similarityMethods
     * @param score
     */
    public void updateScore(SimilarityMethods similarityMethods, double score, String name) {
        if (score > scores.get(similarityMethods) && (!similarityMethods.equals(SimilarityMethods.MEAN_SQUARED_ERROR))) {
            scores.put(similarityMethods, score);
            setSpectrumToCompareName(name);
        }
        if (score < scores.get(similarityMethods) && (similarityMethods.equals(SimilarityMethods.MEAN_SQUARED_ERROR))) {
            scores.put(similarityMethods, score);
            setSpectrumToCompareName(name);
        }
    }  
    
}
