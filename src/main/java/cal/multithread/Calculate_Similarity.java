/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.multithread;

import cal.binBased.BinMSnSpectrum;
import cal.binBased.Calculate_BinSpectrum_Similarity;
import cal.cumulativeBinomialProbability.spectra.CompareAndScore;
import cal.methods.SimilarityMethods;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * This class is used to call during FutureList for multithreading
 * BinMSnSpectrum A is scored against all BinMSnSpectrum objects on the
 * arraylist. Then, the highest score for BinMSnSpectrum A is kept for Dot
 * Product Standard, Dot product weighted with Skolow, Pearson and Spearman
 *
 *
 * @author Sule
 */
public class Calculate_Similarity implements Callable<SimilarityResult> {

    private BinMSnSpectrum givenBinMSnSpectrum; // a binMSnSpectrum object to be calculated against all binMSnSpectra
    private ArrayList<BinMSnSpectrum> allBinMSnSpectraComparedTogivenBinMSnSpectrum; // a list of binMSnSpectra which each of them is calculated against to a given binMSnSpectrum

    private MSnSpectrum givenMSnSpectrum; // a spectrum object to be calculated against all given MSnSpectra
    private ArrayList<MSnSpectrum> allMSnSpectraComparedTogivenMSnSpectrum; // a list of MSnSpectra which each of them is calculated against to a given MSnSpectrum
    private int msRobinOption = 0, // 0-BinBased scoring, 1-MSRobin calculation
            calculationOptionIntensityMSRobin = 1, // 0 for summing up 0.5*(Exp_Int/TotalInt)A+0.5*(ExpInt/TotalInt) 1- multiply (Exp_Int/TotalInt)*(ExpInt/TotalInt) 
            msRobinIntensityOption = 0;
    private double fragTol = 0.5, // fragment tolerance, precursor tolerance
            precTol = 3;// default is precursor mz window of 3-Th

    /**
     * TO CALCULATE BIN-BASED SCORES!
     *
     * Note that PrecursorTolerance is always PPM!
     *
     * @param givenBinMSnSpectrum
     * @param allBinMSnSpectraComparedTogivenBinMSnSpectrum
     * @param fragTol
     * @param precTol
     */
    public Calculate_Similarity(BinMSnSpectrum givenBinMSnSpectrum, ArrayList<BinMSnSpectrum> allBinMSnSpectraComparedTogivenBinMSnSpectrum, double fragTol, double precTol) {
        this.allBinMSnSpectraComparedTogivenBinMSnSpectrum = allBinMSnSpectraComparedTogivenBinMSnSpectrum;
        this.givenBinMSnSpectrum = givenBinMSnSpectrum;
        this.precTol = precTol;
        this.fragTol = fragTol;
    }

    /**
     * TO CALCULATE MSROBIN!!
     *
     * Note that PrecursorTolerance is always PPM!
     *
     * @param givenMSnSpectrum
     * @param allMSnSpectraComparedTogivenMSnSpectrum
     * @param fragTol
     * @param precTol
     * @param calculationOptionIntensityMSRobin
     * @param msRobinIntensityOption
     */
    public Calculate_Similarity(MSnSpectrum givenMSnSpectrum, ArrayList<MSnSpectrum> allMSnSpectraComparedTogivenMSnSpectrum, double fragTol, double precTol, int calculationOptionIntensityMSRobin, int msRobinIntensityOption) {
        this.allMSnSpectraComparedTogivenMSnSpectrum = allMSnSpectraComparedTogivenMSnSpectrum;
        this.givenMSnSpectrum = givenMSnSpectrum;
        this.fragTol = fragTol;
        this.precTol = precTol;
        msRobinOption = 1;
        this.calculationOptionIntensityMSRobin = calculationOptionIntensityMSRobin;
        this.msRobinIntensityOption = msRobinIntensityOption;
    }

    /**
     * Call method to do multithreading
     *
     * @return a similarity result object with highest similarity score of given
     * MSnSpectrum against comparing all given spectra on the second data set
     * @throws Exception
     */
    @Override
    public SimilarityResult call() throws Exception {
        String titleOfGivenSpec,
                precChargeOfGivenSpec;
        double precursorMZOfGivenSpec = 0;
        // So, bin-based scoring...
        if (msRobinOption == 0) {
            titleOfGivenSpec = givenBinMSnSpectrum.getSpectrum().getSpectrumTitle();
            precChargeOfGivenSpec = givenBinMSnSpectrum.getSpectrum().getPrecursor().getPossibleChargesAsString();
            precursorMZOfGivenSpec = givenBinMSnSpectrum.getSpectrum().getPrecursor().getMz();
        } else {
            titleOfGivenSpec = givenMSnSpectrum.getSpectrumTitle();
            precChargeOfGivenSpec = givenMSnSpectrum.getPrecursor().getPossibleChargesAsString();
            precursorMZOfGivenSpec = givenMSnSpectrum.getPrecursor().getMz();
        }

        SimilarityResult similarityResult = new SimilarityResult(titleOfGivenSpec, null, precChargeOfGivenSpec, precursorMZOfGivenSpec);
        // So, here similarities are only bin based calculations! 
        if (msRobinOption == 0) {
            InnerIteratorSync<BinMSnSpectrum> iteratorBinSpectra = new InnerIteratorSync(allBinMSnSpectraComparedTogivenBinMSnSpectrum.iterator());
            while (iteratorBinSpectra.iter.hasNext()) {
                BinMSnSpectrum tmpBinMSnSpectrum = (BinMSnSpectrum) iteratorBinSpectra.iter.next();
                similarityResult.setTmpSpectrumName(tmpBinMSnSpectrum.getSpectrum().getSpectrumTitle());
                synchronized (tmpBinMSnSpectrum) {
                    // If precursor tolerance is bigger than 0, it means that PrecursorTolerance is important while calculation!
                    if (precTol > 0) {
                        double precursor_mz = tmpBinMSnSpectrum.getSpectrum().getPrecursor().getMz(),
                                diff = Math.abs(precursorMZOfGivenSpec - precursor_mz);
                        if (diff <= precTol) {
                            calculateBinBasedScores(givenBinMSnSpectrum, tmpBinMSnSpectrum, similarityResult);
                        }
                        // Precursor tolerance equals to 0, so no precursor diff 
                    } else {
                        calculateBinBasedScores(givenBinMSnSpectrum, tmpBinMSnSpectrum, similarityResult);
                    }
                }
            }
            // Here MSRobin scores are being calculated!
        } else {
            InnerIteratorSync<MSnSpectrum> iteratorSpectra = new InnerIteratorSync(allMSnSpectraComparedTogivenMSnSpectrum.iterator());
            while (iteratorSpectra.iter.hasNext()) {
                MSnSpectrum tmpMSnSpectrum = (MSnSpectrum) iteratorSpectra.iter.next();
                similarityResult.setTmpSpectrumName(tmpMSnSpectrum.getSpectrumTitle());
                synchronized (tmpMSnSpectrum) {
                    // Here precursor tolerance is important! 
                    if (precTol > 0) {
                        CompareAndScore obj = new CompareAndScore(givenMSnSpectrum, tmpMSnSpectrum, fragTol, msRobinIntensityOption, calculationOptionIntensityMSRobin);
                        double precursor_mz = tmpMSnSpectrum.getPrecursor().getMz(),
                                diff = Math.abs(precursorMZOfGivenSpec - precursor_mz);
                        if (diff <= precTol) {
                            double ms_robin = obj.getMSRobinScore();
                            similarityResult.updateScore(SimilarityMethods.MSRobin, ms_robin);
                        }
                    } else {
                        CompareAndScore obj = new CompareAndScore(givenMSnSpectrum, tmpMSnSpectrum, fragTol, msRobinIntensityOption, calculationOptionIntensityMSRobin);
                        double ms_robin = obj.getMSRobinScore();
                        similarityResult.updateScore(SimilarityMethods.MSRobin, ms_robin);
                    }
                }
            }
        }
        return similarityResult;
    }

    private void calculateBinBasedScores(BinMSnSpectrum binMSnSpectrum, BinMSnSpectrum tmpBinMSnSpectrum, SimilarityResult similarityResult) {
        Calculate_BinSpectrum_Similarity calculate = new Calculate_BinSpectrum_Similarity(binMSnSpectrum, tmpBinMSnSpectrum);
        double dot_score = calculate.getScore();
        similarityResult.updateScore(SimilarityMethods.DOT_PRODUCT, dot_score);

        calculate.setMethod(SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD);
        double normalized_dot_score = calculate.getScore();
        similarityResult.updateScore(SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD, normalized_dot_score);

        calculate.setMethod(SimilarityMethods.PEARSONS_CORRELATION);
        double pearson = calculate.getScore();
        similarityResult.updateScore(SimilarityMethods.PEARSONS_CORRELATION, pearson);

        calculate.setMethod(SimilarityMethods.SPEARMANS_CORRELATION);
        double spearman = calculate.getScore();
        similarityResult.updateScore(SimilarityMethods.SPEARMANS_CORRELATION, spearman);

        calculate.setMethod(SimilarityMethods.MEAN_SQUARED_ERROR);
        double mean_squared_error = calculate.getScore();
        similarityResult.updateScore(SimilarityMethods.MEAN_SQUARED_ERROR, mean_squared_error);

        similarityResult.setBestSimilarSpec(tmpBinMSnSpectrum.getSpectrum());
    }

    /**
     * To calculate precursor tolerance between two given precursor masses
     *
     * @param isPPM
     * @param expPrecursorMass_A
     * @param expPrecursorMass_B
     * @return
     */
    public double getMS1Err(boolean isPPM, double expPrecursorMass_A, double expPrecursorMass_B) {
        double error = 0;
        if (isPPM) {
            double diff = Math.abs(expPrecursorMass_A - expPrecursorMass_B);
            // TODO this might be either relative or absolute mass error
            double ppm_error = (diff) / expPrecursorMass_B;
            ppm_error = ppm_error * 1000000;
            error = ppm_error;
        } else {
            double mz_error = Math.abs(expPrecursorMass_A - expPrecursorMass_B);
            error = mz_error;
        }
        return error;
    }

    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
    private class InnerIteratorSync<T> {

        private Iterator<T> iter = null;

        public InnerIteratorSync(Iterator<T> aIterator) {
            iter = aIterator;
        }

        public synchronized T next() {
            T result = null;
            if (iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }
    }
}
