/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.implementation;

import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.util.ArrayList;
import java.util.Collections;
//import org.apache.log4j.Logger;

/**
 * This class divide a spectrum into windows and then selects N peaks with
 * highest intensity per each defined window size on an MSnSpectrum.
 *
 * @author Sule
 */
public class DivideAndTopNFilter extends Filter {

    private int topN;
    private double windowMassSize = 100;

    /**
     * This constructs an object to filter out spectra based on 100Da window and
     * it selects X peaks with highest intensities for each window.
     *
     * @param expSpectrum is an experimental spectrum
     * @param topN is picked peak numbers with highest intensities
     */
    public DivideAndTopNFilter(Spectrum expSpectrum, int topN) {
        super.expSpectrum = expSpectrum;
        this.topN = topN;
//        LOGGER = Logger.getLogger(ConfigHolder.class);
    }

    /**
     * This constructs an object with a given window size instead of a default
     * value.
     *
     * The default window size is 100Da
     *
     * @param expSpectrum is an experimental spectrum
     * @param topN is picked peak numbers with highest intensities
     * @param windowMassSize size of window, based on this a given spectrum is
     * divided into smaller parts.
     *
     */
    public DivideAndTopNFilter(Spectrum expSpectrum, int topN, double windowMassSize) {
        super.expSpectrum = expSpectrum;
        this.topN = topN;
        this.windowMassSize = windowMassSize;
//        LOGGER = Logger.getLogger(ConfigHolder.class);
    }

    @Override
    protected void process() {
//        LOGGER.info(expSpectrum.getSpectrumTitle());
        double startMz = expSpectrum.getMinMz(),
                limitMz = startMz + windowMassSize;
        ArrayList<Peak> cPeaks = new ArrayList<Peak>();
        for (int index_exp = 0; index_exp < expSpectrum.getOrderedMzValues().length; index_exp++) {
            double tmpMZ = expSpectrum.getOrderedMzValues()[index_exp];
            Peak tmpPeak = expSpectrum.getPeakMap().get(tmpMZ);
            if (tmpMZ < limitMz) {
                cPeaks.add(tmpPeak);
            } else {
                Collections.sort(cPeaks, Peak.DescendingIntensityComparator);
                int tmp_num = topN;
                if (topN > cPeaks.size()) {
                    tmp_num = cPeaks.size();
                }
                for (int num = 0; num < tmp_num; num++) {
                    Peak tmpCPeakToAdd = cPeaks.get(num);
                    filteredPeaks.add(tmpCPeakToAdd);
                }
                cPeaks.clear();
                limitMz = limitMz + windowMassSize;
                index_exp = index_exp - 1;
            }
        }
        if (!cPeaks.isEmpty()) {
            Collections.sort(cPeaks, Peak.DescendingIntensityComparator);
            int tmp_num = topN;
            if (topN > cPeaks.size()) {
                tmp_num = cPeaks.size();
            }
            for (int num = 0; num < tmp_num; num++) {
                Peak tmpCPeakToAdd = cPeaks.get(num);
                filteredPeaks.add(tmpCPeakToAdd);
            }
        }
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public double getWindowMassSize() {
        return windowMassSize;
    }

    public void setWindowMassSize(double windowMassSize) {
        this.windowMassSize = windowMassSize;
    }

}
