/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

import cal.methods.SimilarityMethods;

/**
 * This interface shows a method to calculate similarities between two
 * BinMSnSpectrum objects
 *
 * @author Sule
 */
public interface Calculate_BinSpectrum_Similarity_Interface {

    /**
     * This method estimates similarity between two given BinMSnSpectrum objects
     * based on selected SimilarityMethod. If a method is dot product, the best
     * score equals to 100 or 1 and mean or median based score is selected, the
     * best score equals to 0. Before calculating similarity between spectra,
     * they must be scaled into bins (BinMSnSpectrum).
     *
     * @param specA a BinMSnSpectrum object that is analyzed
     * @param specB BinMSnSpectrum object that is analyzed
     * @param method is a Similarity Method
     *
     * @return the similarity score between the given two BinMSnSpectrum object.     *
     */
    public double calculateSimilarity(BinMSnSpectrum specA, BinMSnSpectrum specB, SimilarityMethods method);

}
