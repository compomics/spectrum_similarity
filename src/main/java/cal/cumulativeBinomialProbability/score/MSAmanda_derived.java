/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.score;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class calculates cumulative binominal probability based scores with
 * considering intensities from experimental spectra to
 * cumulativeBinomialProbability these.
 *
 * n: number of matched peaks
 *
 * N: number of (picked) peaks of a filtered spectrum with topN
 *
 * p: probability,topN/windowSize from a Filter object. Note that topN is [1-10]
 *
 * Note that cumulative binominal probability function calculates the score as
 * inclusive (not exclusive)
 *
 * Intensity_part(IP): (Explained_IntensitiesA/All_IntensitiesA)
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
public class MSAmanda_derived extends CumulativeBinomialProbabilityBasedScoring {

    private double intensity, // sum of all intensities from every picked peak
            explainedIntensity, // sum of all intensities from matched picked peak
            weight = -1;
    private int intensityOption; // 0:SQRT(IP), 1:IP

    /**
     *
     * @param p probability=topN/windowSize, topN=[1- 10] peaks
     * @param N Picked (filtered) peaks on an experimental spectrum (on MSAmanda
     * derived but this equals to all peaks at a theoretical spectrum on
     * Andromeda derived)
     * @param n Matched peaks-is number of matched peaks against a theoretical
     * spectrum
     * @param intensity sum of all intensities from every picked peak
     * @param explainedIntensity sum of all intensities from matched picked peak
     * @param intesityOption 0-Intensities are squared, 1-No preprocessing on
     * intensity part
     * @param ScoreName name of scoring function
     *
     */
    public MSAmanda_derived(double p, int N, int n, double intensity, double explainedIntensity, int intesityOption, ScoreName name) {
        super.p = p;
        super.N = N;
        super.n = n;
        this.intensity = intensity;
        this.explainedIntensity = explainedIntensity;
        this.intensityOption = intesityOption;
        this.name = name;
    }

    public MSAmanda_derived(double p, int N, int n, double intensity, double explainedIntensity, int intesityOption, ScoreName name, double weight) {
        super.p = p;
        super.N = N;
        super.n = n;
        this.intensity = intensity;
        this.explainedIntensity = explainedIntensity;
        this.intensityOption = intesityOption;
        this.name = name;
        this.weight = weight;
    }

    /**
     * This calculate MSAmanda_derived cumulativeBinomialProbability with an
     * option in selection. Option0-Sqrt(IntensityPart) Option1-IntensityPart
     */
    @Override
    protected void calculateScore() {
        try {
            double tmp = 0;
            double probability_based_score = super.calculateCumulativeBinominalProbability(),
                    intensity_part = (double) explainedIntensity / (double) intensity;
            if (intensityOption == 0 && probability_based_score != 0) {
                tmp = -10 * (Math.log10(probability_based_score));
                intensity_part = (Math.sqrt(intensity_part));
                score = tmp * intensity_part;
            } else if (intensityOption == 1 && probability_based_score != 0) {
                tmp = - 10 * (Math.log10(probability_based_score));
                score = tmp * intensity_part;
            } else if (intensityOption == 2 && probability_based_score != 0) {
                score = -10 * (Math.log10(probability_based_score / intensity_part));
            }
            if (weight != -1 && weight != 0) {
                score = score + (score * Math.log(1 / (double) weight));
            }
            // to eliminate obtaining -0 values..
            score += 0.0;
            isCalculated = true;
        } catch (Exception ex) {
            Logger.getLogger(MSAmanda_derived.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
