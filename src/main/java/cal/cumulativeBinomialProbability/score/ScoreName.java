/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cal.cumulativeBinomialProbability.score;

/**
 * Scoring function names
 * @author Sule
 */
public enum ScoreName {
    AndromedaD, // Andromeda derive - cumulative binomial probability derived function/only number of peak/N=theoretical peaks 
    MSAmandaD, // MSAmanda derive - cumulative binomial probability derived function/number of peak and explained intensities/N=matched experimental peaks 
    TheoMSAmandaD, // (Theoretical)MSAmanda derive - cumulative binomial probability derived function/number of peak and explained intensities/N=theoretical experimental peaks 
    AndromedaDWeighted,
    TheoMSAmandaDWeighted
}
