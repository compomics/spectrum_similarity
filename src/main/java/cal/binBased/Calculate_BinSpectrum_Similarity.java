/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

import cal.methods.SimilarityMethods;
import com.compomics.util.math.BasicMathFunctions;
import java.util.ArrayList;
import java.util.Objects;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author Sule
 */
public class Calculate_BinSpectrum_Similarity implements Calculate_BinSpectrum_Similarity_Interface {

    protected BinMSnSpectrum bin_specA, // One BinMSnSpectrum to be analyzed
            bin_specB;// Another BinMSnSpectrum to be analyzed
    protected SimilarityMethods sim_method; // Similarity calculation method
    protected double score = 0, // similarity score
            fragmentTolerance, // fragmentation tolerance (MS2error) - it is required to set a bin size..
            x, // x is weight for intensity
            y; // y is weight for m/Z
    protected boolean isScoreCalculated = false;

    /* Constructors */
    public Calculate_BinSpectrum_Similarity(BinMSnSpectrum bin_specA, BinMSnSpectrum bin_specB,
            SimilarityMethods sim_method, double fragmentTolerance) {
        this.bin_specA = bin_specA;
        this.bin_specB = bin_specB;
        this.sim_method = sim_method;
        this.fragmentTolerance = fragmentTolerance;
        x = 0;
        y = 0;
    }

    public Calculate_BinSpectrum_Similarity(BinMSnSpectrum bin_specA, BinMSnSpectrum bin_specB) {
        this.bin_specA = bin_specA;
        this.bin_specB = bin_specB;
        sim_method = SimilarityMethods.DOT_PRODUCT;
        fragmentTolerance = 0.5;
        x = 0;
        y = 0;
    }

    public Calculate_BinSpectrum_Similarity(BinMSnSpectrum bin_specA, BinMSnSpectrum bin_specB,
            SimilarityMethods sim_method, double fragmentTolerance, double x, double y) {
        this.bin_specA = bin_specA;
        this.bin_specB = bin_specB;
        this.sim_method = sim_method;
        this.fragmentTolerance = fragmentTolerance;
        this.x = x;
        this.y = y;
    }

    /* Getter and setter methods */
    public BinMSnSpectrum getBin_specA() {
        return bin_specA;
    }

    public void setBin_specA(BinMSnSpectrum bin_specA) {
        this.bin_specA = bin_specA;
    }

    public BinMSnSpectrum getBin_specB() {
        return bin_specB;
    }

    public void setBin_specB(BinMSnSpectrum bin_specB) {
        this.bin_specB = bin_specB;
    }

    public SimilarityMethods getSim_method() {
        return sim_method;
    }

    public void setMethod(SimilarityMethods sim_method) {
        this.sim_method = sim_method;
    }

    public double getScore() {
        if (!isScoreCalculated) {
            calculateSimilarity(bin_specA, bin_specB, sim_method);
        }
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getFragmentTolerance() {
        return fragmentTolerance;
    }

    public void setFragmentTolerance(double fragmentTolerance) {
        this.fragmentTolerance = fragmentTolerance;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isIsScoreCalculated() {
        return isScoreCalculated;
    }

    public void setIsScoreCalculated(boolean isScoreCalculated) {
        this.isScoreCalculated = isScoreCalculated;
    }

    /* Methods */
    @Override
    public double calculateSimilarity(BinMSnSpectrum specA, BinMSnSpectrum specB, SimilarityMethods method) {
        double[] xArray = bin_specA.getBin_spectrum(),
                yArray = bin_specB.getBin_spectrum();
        switch (method) {
            case DOT_PRODUCT:
                calculateDotProductDerived(xArray, yArray, 1, 0, false);
                break;
            case NORMALIZED_DOT_PRODUCT_STANDARD:
                // Calculate standard dot product score
                calculateDotProductDerived(xArray, yArray, 1, 0, true);
                break;
            case NORMALIZED_DOT_PRODUCT_HORAI:
                // Calculate weighted dot product score based on Horai 
                calculateDotProductDerived(xArray, yArray, 0.5, 2, true);
                break;
            case NORMALIZED_DOT_PRODUCT_SOKOLOW:
                // Calculate weighted dot product score based on Skolow
                calculateDotProductDerived(xArray, yArray, 0.5, 1, true);
                break;
            case NORMALIZED_DOT_PRODUCT_ZHANG:
                // Calculate weighted dot product score based on Zhang
                calculateDotProductDerived(xArray, yArray, 0.53, 1.5, true);
                break;
            case NORMALIZED_DOT_PRODUCT_USER_DEFINED:
                // Calculate weighted dot product score and weights are defined by user
                calculateDotProductDerived(xArray, yArray, x, y, true);
                break;
            case MEAN_SQUARED_ERROR:
                // Calculate mean squared error
                calculateMxSquareError();
                break;
            case MEDIAN_SQUARED_ERROR:
                // Calculate median squared error
                calculateMxSquareError();
                break;
            case ROOT_MEAN_SQUARE_ERROR:
                // Calculate root mean square error
                calculateMxSquareError();
                break;
            case ROOT_MEDIAN_SQUARE_ERROR:
                // Calculate root median square error
                calculateMxSquareError();
                break;
            case PEARSONS_CORRELATION:
                // Calculate pearson correlation coefficient - linear relationship (Mean)
                calculateCorrelation();
                break;
            case SPEARMANS_CORRELATION:
                // Calculate Spearmans correation coefficient - monotonic relationship (Ranks)
                calculateCorrelation();
                break;
            case SLIDING_DOT_PRODUCT:
                calculateSlidingDotProduct();
                break;
        }
        return score;
    }

    /**
     * This method calculates correlation coefficients between two MSnSpectrum
     * object while taking into account of intensities of the mutual peaks. A
     * perfect score is 1 (or -1). And value range is [-1:1]
     *
     */
    public void calculateCorrelation() {
        // TODO: Make sure if any of binSpectra is empty, score i NaN!       
        if (sim_method.equals(SimilarityMethods.SPEARMANS_CORRELATION)) {
            SpearmansCorrelation sc = new SpearmansCorrelation();
            score = sc.correlation(bin_specA.getBin_spectrum(), bin_specB.getBin_spectrum());
        } else if (sim_method.equals(SimilarityMethods.PEARSONS_CORRELATION)) {
            PearsonsCorrelation pc = new PearsonsCorrelation();
            score = pc.correlation(bin_specA.getBin_spectrum(), bin_specB.getBin_spectrum());
        }
    }

    /**
     * This method calculates dot product score according to given weights.
     * Maximum similarity gives 1. Minimum gives 0.
     *
     * @param xArray - binned-spectrumX with intensities summed up
     * @param yArray - binned-spectrumY with intensities summed up
     * @param x - weight for peak intensity
     * @param y - weight for peak m/z
     * @param isNormalized - true: normalize calculated dot-score, false: no
     * normalization on dot-score
     * @return a score
     */
    public double calculateDotProductDerived(double[] xArray, double[] yArray, double x, double y, boolean isNormalized) {
        // TODO: Make sure if any of binSpectra is empty, score i NaN! and return!         
        // now, calculate dot product score
        double dot_product_alpha_beta = 0,
                dot_product_alpha_alpha = 0,
                dot_product_beta_beta = 0;
        double binSize = 2 * fragmentTolerance,
                mz = bin_specA.getMin_value() + binSize;
        for (int i = 0; i < xArray.length; i++) {
            double mz_peakA = mz,
                    mz_peakB = mz,
                    intensity_peakA = xArray[i],
                    intensity_peakB = yArray[i];
            boolean control = false;
            if (intensity_peakA == 0 && intensity_peakB == 0) {
                control = true;
            }
            if (!control) {
                if (i == xArray.length - 1) {// make sure last one is not empty..
                    mz_peakA = mz_peakA + 0.0000001;
                    mz_peakB = mz_peakB + 0.0000001;
                }
                double alpha = Math.pow(intensity_peakA, x) * Math.pow(mz_peakA, y),
                        beta = Math.pow(intensity_peakB, x) * Math.pow(mz_peakB, y);
                dot_product_alpha_beta = dot_product_alpha_beta + (double) (alpha * beta);
                dot_product_alpha_alpha = dot_product_alpha_alpha + (double) (alpha * alpha);
                dot_product_beta_beta = dot_product_beta_beta + (double) (beta * beta);
                mz = mz + binSize;
            }
        }
        if (isNormalized) {
            double normalized_dot_product = (double) dot_product_alpha_beta / (double) (Math.sqrt(dot_product_alpha_alpha * dot_product_beta_beta));
            score = normalized_dot_product;
        } else {
            score = dot_product_alpha_beta;
        }
        return score;
    }

    /**
     * This method calculates scores depending on (root)meanSquaredError or
     * (root)medianSquaredError based on
     *
     * Important: If intensities on both corresponding bins are 0, it skips that
     * part. Indeed, 0 stands for if error is perfect, but it is zero because
     * there is no value, not because there are values which are very close to
     * each other..
     *
     * Maximum similarity (or the best score) gives 0. there is no minimum
     * score, since the value may end +Inf
     *
     */
    public void calculateMxSquareError() {
        ArrayList<Double> errors = new ArrayList<Double>();
        // now, calculate MxSquared error product score
        double[] xArray = bin_specA.getBin_spectrum(),
                yArray = bin_specB.getBin_spectrum();
        for (int i = 0; i < xArray.length; i++) {
            boolean control = false;
            double intensity_peakA = xArray[i],
                    intensity_peakB = yArray[i],
                    square_diff_intensity = Math.pow((intensity_peakA - intensity_peakB), 2);
            if (intensity_peakA == 0 && intensity_peakB == 0) {
                control = true;
            }
            if (!control) {
                errors.add(square_diff_intensity);
            }
        }
        // ready to calculate mean (root)squared error or median square error
        if (sim_method.equals(SimilarityMethods.MEAN_SQUARED_ERROR)) {
            // calculate based on MEAN
            score = BasicMathFunctions.mean(errors);
        } else if (sim_method.equals(SimilarityMethods.MEDIAN_SQUARED_ERROR)) {
            // calculate based on MEDIAN
            score = BasicMathFunctions.median(errors);
        } else if (sim_method.equals(SimilarityMethods.ROOT_MEAN_SQUARE_ERROR)) {
            // calculate root square based on MEAN
            double tmp_score = BasicMathFunctions.mean(errors);
            score = Math.sqrt(tmp_score);
        } else if (sim_method.equals(SimilarityMethods.ROOT_MEDIAN_SQUARE_ERROR)) {
            // calculate root square based on MEDIAN
            double tmp_score = BasicMathFunctions.median(errors);
            score = Math.sqrt(tmp_score);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.bin_specA);
        hash = 11 * hash + Objects.hashCode(this.bin_specB);
        hash = 11 * hash + Objects.hashCode(this.sim_method);
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.fragmentTolerance) ^ (Double.doubleToLongBits(this.fragmentTolerance) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
        hash = 11 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
        hash = 11 * hash + (this.isScoreCalculated ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Calculate_BinSpectrum_Similarity other = (Calculate_BinSpectrum_Similarity) obj;
        if (!Objects.equals(this.bin_specA, other.bin_specA)) {
            return false;
        }
        if (!Objects.equals(this.bin_specB, other.bin_specB)) {
            return false;
        }
        if (this.sim_method != other.sim_method) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (Double.doubleToLongBits(this.fragmentTolerance) != Double.doubleToLongBits(other.fragmentTolerance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Calculate_BinMSnSpectrum_Similarity{" + "bin_specA=" + bin_specA + ", bin_specB=" + bin_specB + ", sim_method=" + sim_method + ", score=" + score + ", fragmentTolerance=" + fragmentTolerance + " x=" + x + ", y=" + y + ", isScoreCalculated=" + isScoreCalculated + '}';
    }

    /**
     * This calculates sliding-dot product like Sequest based on given spectra
     *
     */
    private void calculateSlidingDotProduct() {
        boolean isNormalized = false;
        double[] xArray = bin_specA.getBin_spectrum(),
                yArray = bin_specB.getBin_spectrum();
        // dot-product between two binned-spectrum without any shift..
        double dot_r_0 = calculateDotProductDerived(xArray, yArray, 1, 0, isNormalized),
                dot_res = 0;
        int num_of_observation = 0;
        // this also includes T=0
        ArrayList<double[]> binSpectra = bin_specB.getBinSpectra();
        for (double[] binSpectrum : binSpectra) {
            // shifting the compared spectrum (either theoretical or another experimental spectrum)
            num_of_observation++;
            dot_res += calculateDotProductDerived(xArray, binSpectrum, 1, 0, isNormalized);
        }
        double mean_dot_res = dot_res / num_of_observation;
        score = dot_r_0 - mean_dot_res;
        isScoreCalculated = true;
    }

}
