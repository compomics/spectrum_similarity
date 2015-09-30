/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.score;

import util.MathsUtil;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class calculates cumulative binominal probability based scores with
 * considering intensities from experimental spectra to
 * cumulativeBinomialProbability these.
 *
 * n: number of matched peaks
 *
 * N: number of peaks of a spectrum with a bigger peak list
 *
 * p: probability,topN/windowSize from a Filter object. Note that topN is [1-10]
 *
 * Note that cumulative binominal probability function calculates the score as
 * inclusive (not exclusive)
 *
 * Intensity_part(IP): [(0.5*(Explained_Intensities A/All_Intensities
 * A))+(0.5*(Explained_Intensities B/All_intensitiesB))]
 *
 * For each filtered peakList with a given topN parameter, p is calculated as
 * explained and cumulative binominal probability based scoring function part is
 * calculated as:
 *
 * Probability_Part (PP) = -10*[-log(P)]
 *
 * Later on, intensity part is introduced as with two options:
 *
 * (option0) Final_score = PP*Sqrt(IP)
 *
 * (option1) Final_score = PP*IP *
 *
 * @author Sule
 */
public class MSRobin extends CumulativeBinomialProbabilityBasedScoring implements Score_Interface {

    private double intensity_part;
    private double option; // 0: Sqrt(IP) 1: IP and some others

    /**
     * To construct an object to calculate MSRobin cumulativeBinomialProbability
     *
     * @param p probability,topN/windowSize from a Filter object. Note that topN
     * is [1-10]
     *
     * @param N number of peaks of a spectrum with a bigger peak list
     * @param n number of matched peaks between two experimental spectra
     * @param intensity_part a pre-calculated value while picking peaks
     * @param option 0-Intensities are squared, 1-No preprocessing on intensity
     * part
     *
     */
    public MSRobin(double p, int N, int n, double intensity_part, int option) {
        super.p = p;
        super.N = N;
        super.n = n;
        this.option = option;
        this.intensity_part = intensity_part;
    }

    /**
     * To calculate CumulativeBinominalProbability with given n,N and p values.
     * n is inclusive during cumulative binominal probability function
     *
     * @return
     * @throws Exception
     */
    @Override
    public double calculateCumulativeBinominalProbability() throws Exception {
        double probability = 0;
        for (int k = n; k < N + 1; k++) {
            double factorial_part = MathsUtil.calculateCombination(N, k);
            double tmp_probability = factorial_part * (Math.pow(p, k)) * (Math.pow((1 - p), (N - k)));
            probability += tmp_probability;
        }
        return probability;
    }

    /**
     * This calculate MSRobin cumulativeBinomialProbability with an option in
     * selection
     */
    @Override
    public void calculateScore() {

        try {
            double probability_part = calculateCumulativeBinominalProbability();
            if (option == 0) {
                probability_part = -10 * (Math.log10(probability_part));
                intensity_part = (Math.sqrt(intensity_part));
                score = probability_part * intensity_part;
            } else if (option == 1) {
                probability_part = -10 * (Math.log10(probability_part));
                score = probability_part * intensity_part;
            } else if (option == 2) {
                probability_part = -(Math.log10(probability_part));
                score = probability_part * intensity_part;
            } else if (option == 3) {
                // only probability
                score = -10 * (Math.log10(probability_part));
            } else if (option == 4) {
                // only probability
                score = (1 - probability_part) * intensity_part;
            }
            score += 0; // just making sure the value would not be negative zero           
            isCalculated = true;
        } catch (Exception ex) {
            Logger.getLogger(MSRobin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getIntensity_part() {
        return intensity_part;
    }

    public void setIntensity_part(double intensity_part) {
        this.intensity_part = intensity_part;
    }

    public double getProbability_part() throws Exception {
        double probability_part = calculateCumulativeBinominalProbability();
        return probability_part;
    }

}
