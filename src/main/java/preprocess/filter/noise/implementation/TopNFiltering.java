/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.implementation;

import preprocess.filter.noise.interfaces.NoiseFilter;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class keeps the top N intense peaks
 *
 * @author Sule
 */
public class TopNFiltering implements NoiseFilter {

    private int topN = 50;

    /**
     *
     * @param topN is number of top intense peaks
     */
    public TopNFiltering(int topN) {
        this.topN = topN;
    }

    /**
     * This class keeps only the top N intense peaks
     *
     * @param ms
     * @return
     */
    @Override
    public MSnSpectrum noiseFilter(MSnSpectrum ms) {
        ArrayList<Peak> peakList = new ArrayList<Peak>(ms.getPeakList());
        // Sort peaks in descending order on intensities
        Collections.sort(peakList, Peak.DescendingIntensityComparator);
        // This part makes sure that topN can never be bigger than peakList 
       if (peakList.size() < topN) {
            topN = peakList.size();
        }
        // This selects the top N peaks
        ArrayList<Peak> subPeakList = new ArrayList<Peak>(peakList.subList(0, topN));
        // clear and set a peak list
        ms.getPeakList().clear();
        ms.setMzOrdered(false);
        ms.setPeaks(subPeakList);
        return ms;
    }

}
