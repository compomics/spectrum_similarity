/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.score;

import org.apache.commons.math.distribution.BinomialDistribution;
import org.apache.commons.math.distribution.BinomialDistributionImpl;

/**
 * This abstract class is used to write cumulative binominal probability based
 * scoring function
 *
 * @author Sule
 */
public abstract class CumulativeBinomialProbabilityBasedScoring {

    protected double p; // probability of success on a single trial
    protected int N, // number of trials
            n;// number of successes
    protected double score = 0.0; // cumulative bionominal probability based score for given N,n,p.
    protected boolean isCalculated = false; //  To make sure that a score is not calculated over and over while calling getter method
    protected ScoreName name;

    /**
     * To calculate cumulative binominal probability score
     *
     */
    protected abstract void calculateScore();

    /**
     * It returns a cumulativeBinomialProbability. It checks if a it is already
     * calculated; if not it calculates
     *
     * @return
     */
    public double getScore() {
        if (!isCalculated) {
            calculateScore();
        }
        return score;
    }

    /**
     * To calculate CumulativeBinominalProbability with given n,N and p values.
     * by calling BinomialDistribution class on Apache
     *
     * @return
     * @throws Exception
     */
    protected double calculateCumulativeBinominalProbability() throws Exception {
        BinomialDistribution b = new BinomialDistributionImpl(N, p);
        int tmp = n - 1;
        double probability = 1 - b.cumulativeProbability(tmp);
        return probability;
    }

}
