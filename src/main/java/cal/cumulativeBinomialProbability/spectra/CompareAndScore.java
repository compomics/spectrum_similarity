/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.spectra;

import preprocess.filter.noise.implementation.Filter;
import cal.cumulativeBinomialProbability.score.MSRobin;
import preprocess.filter.noise.implementation.DivideAndTopNFilter;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

/**
 *
 * This class finds matched peaks between two experimental spectra with a given
 * fragment tolerance.
 *
 * Then it calls MSRobin object to calculate cumulative binominal probability
 * based function. It calculates 10 different MSRobin scores and assign the
 * highest msRobinScore as a similarity match
 *
 *
 * @author Sule
 */
public class CompareAndScore {

    private Spectrum spectrumA, // experimental MS2 spectrum
            spectrumB; //experimental MS2 spectrum
    private double fragTol; // fragment tolerance, Da Level, not PPM!
    private int MSRobinOption; // 0-sqrt(Intensities), 1-Intensities
    private int intensityOption; // 0-summing up 0.5*(Exp_Int/TotalInt)A+0.5*(ExpInt/TotalInt) 1-multiply (Exp_Int/TotalInt)*(ExpInt/TotalInt) 
    private boolean isCalculated = false; // This makes sure that match and scoring only calculate once
    private double msRobinScore = 0,
            massWindow = 100,
            intensity_part = 0,
            probability_part = 0;

    /* Constructor */
    public CompareAndScore(Spectrum spectrumA, Spectrum spectrumB, double fragTol, int msRobinOption, int intensityOption) {
        this.spectrumA = spectrumA;
        this.spectrumB = spectrumB;
        this.fragTol = fragTol;
        this.MSRobinOption = msRobinOption;
        this.intensityOption = intensityOption;
    }

    public CompareAndScore(Spectrum spectrumA, Spectrum spectrumB, double fragTol, int msRobinOption, int intensityOption, double massWindow) {
        this.spectrumA = spectrumA;
        this.spectrumB = spectrumB;
        this.fragTol = fragTol;
        this.MSRobinOption = msRobinOption;
        this.intensityOption = intensityOption;
        this.massWindow = massWindow;
    }

    /* getters and setters */
    public Spectrum getSpectrumA() {
        return spectrumA;
    }

    public Spectrum getSpectrumB() {
        return spectrumB;
    }

    public double getFragTol() {
        return fragTol;
    }

    /**
     * This method finds matched peaks between two filtered out experimental
     * spectra and calculates MSRobin msRobinScore
     *
     * @return
     */
    public double getMSRobinScore() throws IOException, Exception {
        if (!isCalculated) {
            msRobinScore = match_and_score();
        }
        return msRobinScore;
    }

    /**
     * This method finds experimental peaks on one experimental spectrum closed
     * to another peak on other experimental spectrum with a given fragment
     * tolerance.TotalN for MSRobin score is size of spectrum with more peaks.
     * Otherwise these unmatched peaks from larger spectra will not be
     * considered while scoring.
     *
     *
     * Then, MSRobins with 10 different picked peaks m[1-10] values are
     * calculated. It returns the highest msRobinScore between all of them!
     *
     * TODO: fragment tolerance only works as ppm!
     *
     * @param expMS2_1
     * @param expMS2_2
     * @return
     */
    private double match_and_score() throws Exception {
        ArrayList<Double> scores = new ArrayList<Double>();
        for (int topN = 1; topN < 11; topN++) {
//            System.out.print(spectrumA.getSpectrumTitle() + "\t" + spectrumB.getSpectrumTitle() + "\t");
//            System.out.print(topN + "\t");
            Filter filterA = new DivideAndTopNFilter(spectrumA, topN, massWindow),
                    filterB = new DivideAndTopNFilter(spectrumB, topN, massWindow);
            double probability = (double) topN / (double) massWindow;
            ArrayList<Peak> fP_spectrumA = filterA.getFilteredPeaks(),
                    fP_spectrumB = filterB.getFilteredPeaks();
            double[] results = new double[4];
            if (fP_spectrumB.size() < fP_spectrumA.size()) {
                results = prepareData(fP_spectrumA, fP_spectrumB);
            } else {
                results = prepareData(fP_spectrumB, fP_spectrumA);
            }
            int totalN = (int) results[0],
                    n = (int) results[1];
            double tmp_intensity_part = results[2];
//            System.out.print(totalN + "\t");
            MSRobin object = new MSRobin(probability, totalN, n, tmp_intensity_part, MSRobinOption);
            double score = object.getScore();
            scores.add(score);
            intensity_part = object.getIntensity_part();
            probability_part = object.getProbability_part();
        }
        double finalScore = Collections.max(scores);
        return finalScore;
    }

    public double getIntensity_part() {
        return intensity_part;
    }

    public void setIntensity_part(double intensity_part) {
        this.intensity_part = intensity_part;
    }

    public double getProbability_part() {
        return probability_part;
    }

    public void setProbability_part(double probability_part) {
        this.probability_part = probability_part;
    }

    private double[] prepareData(ArrayList<Peak> filteredExpMS2_1, ArrayList<Peak> filteredExpMS2_2) {
        double[] results = new double[4];
        HashSet<Peak> mPeaks_2 = new HashSet<Peak>(); //matched peaks from filteredExpMS2_2
        double intensities_1 = 0,
                intensities_2 = 0,
                explainedIntensities_1 = 0,
                explainedIntensities_2 = 0;
        double alpha_alpha = 0,
                beta_beta = 0,
                alpha_beta = 0;
        boolean is_intensities2_ready = false;
        for (int i = 0; i < filteredExpMS2_1.size(); i++) {
            Peak p1 = filteredExpMS2_1.get(i);
            double mz_p1 = p1.getMz(),
                    intensity_p1 = p1.getIntensity(),
                    diff = fragTol,// Based on Da.. not ppm...
                    foundInt_1 = 0,
                    foundInt_2 = 0;
            intensities_1 += intensity_p1;
            Peak matchedPeak_2 = null;
            for (Peak peak_expMS2_2 : filteredExpMS2_2) {
                double tmp_mz_p2 = peak_expMS2_2.getMz(),
                        tmp_diff = (tmp_mz_p2 - mz_p1),
                        tmp_intensity_p2 = peak_expMS2_2.getIntensity();
                if (!is_intensities2_ready) {
                    intensities_2 += tmp_intensity_p2;
                }
                if (Math.abs(tmp_diff) < diff) {
                    matchedPeak_2 = peak_expMS2_2;
                    diff = Math.abs(tmp_diff);
                    foundInt_1 = intensity_p1;
                    foundInt_2 = tmp_intensity_p2;
                } else if (tmp_diff == diff) {
                    // so this peak is indeed in between of two peaks
                    // So, just the one on the left side is being chosen..
                }
            }
            is_intensities2_ready = true;
            if (foundInt_1 != 0 && !mPeaks_2.contains(matchedPeak_2)) {
                mPeaks_2.add(matchedPeak_2);
                alpha_alpha += foundInt_1 * foundInt_1;
                beta_beta += foundInt_2 * foundInt_2;
                alpha_beta += foundInt_1 * foundInt_2;

                explainedIntensities_1 += foundInt_1;
                explainedIntensities_2 += foundInt_2;
            }
        }
        // double dot_score_intensities = calculateDot(filteredExpMS2_1, filteredExpMS2_2);
        int totalN = filteredExpMS2_1.size(),
                n = mPeaks_2.size();
        double intensityPart = 0;
        if (intensityOption == 3) {
            //Making sure that not to have NaN due to zero!
            if (n != 0) {
                intensityPart = calculateIntensityPart(alpha_alpha, beta_beta, alpha_beta);
//                System.out.println(n + "\t" + totalN + "\t" + intensityPart);
            }
        } else {
            intensityPart = calculateIntensityPart(explainedIntensities_1, intensities_1, explainedIntensities_2, intensities_2, intensityOption);
        }
        results[0] = totalN;
        results[1] = n;
        results[2] = intensityPart;
        return results;
    }

    private double calculateIntensityPart(double explainedIntensities_1, double intensities_1, double explainedIntensities_2, double intensities_2, int intensityOption) {
        double intensity_part = 0;
        double tmp_part_1 = explainedIntensities_1 / intensities_1,
                tmp_part_2 = explainedIntensities_2 / intensities_2;
        if (intensityOption == 0) {
            intensity_part = (0.5 * tmp_part_1) + (0.5 * tmp_part_2);
        } else if (intensityOption == 1) {
            intensity_part = tmp_part_1 * tmp_part_2;
        } else if (intensityOption == 2) {
            intensity_part = Math.pow(10, (1 - (tmp_part_1 * tmp_part_2)));
        }
        return intensity_part;
    }

    private double calculateIntensityPart(double alpha_alpha, double beta_beta, double alpha_beta) {
        double intensityPart = alpha_beta / (Math.sqrt(alpha_alpha * beta_beta));
        return intensityPart;
    }

}
