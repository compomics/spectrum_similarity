/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.multithread;

import cal.methods.SimilarityMethods;
import java.util.Objects;

/**
 * This class holds information about pairwise spectrum comparisons
 * 
 * @author Sule
 */
public class PairwiseComparison {
    private String spectrumA,
            spectrumB;
    private double score;
    private SimilarityMethods similarityMethod;
    
    /* Constructor */
    
    /**
     * To construct a PairwiseComparison object
     * 
     * @param spectrumA 
     * @param spectrumB
     * @param score score between spectrumA and spectrum B
     * @param similarityMethod scoring function
     */
    public PairwiseComparison(String spectrumA, String spectrumB, double score, SimilarityMethods similarityMethod) {
        this.spectrumA = spectrumA;
        this.spectrumB = spectrumB;
        this.score = score;
        this.similarityMethod = similarityMethod;
    }

    /** Methods **/
    public String getSpectrumA() {
        return spectrumA;
    }

    public void setSpectrumA(String spectrumA) {
        this.spectrumA = spectrumA;
    }

    public String getSpectrumB() {
        return spectrumB;
    }

    public void setSpectrumB(String spectrumB) {
        this.spectrumB = spectrumB;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public SimilarityMethods getMethod() {
        return similarityMethod;
    }

    public void setMethod(SimilarityMethods method) {
        this.similarityMethod = method;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.spectrumA);
        hash = 79 * hash + Objects.hashCode(this.spectrumB);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 79 * hash + Objects.hashCode(this.similarityMethod);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PairwiseComparison other = (PairwiseComparison) obj;
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (!Objects.equals(this.spectrumA, other.spectrumA)) {
            return false;
        }
        if (!Objects.equals(this.spectrumB, other.spectrumB)) {
            return false;
        }
        if (this.similarityMethod != other.similarityMethod) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PairwiseComparison{" + "spectrumA=" + spectrumA + ", spectrumB=" + spectrumB + ", score=" + score + ", method=" + similarityMethod + '}';
    }
    
    
    
}
