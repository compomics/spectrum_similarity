/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.implementation;

import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.util.ArrayList;

/**
 * This class divide a spectrum into 10 intervals. For each interval, normalize
 * intensities by 50
 *
 * @author Sule
 */
public class DivideAndNormalize extends Filter {

    private int intervals; // how many intervals are at one experimental spectrum
    private double normalize_value; // normalization value (each intensity normalize up to this)

    /**
     * A spectrum will be divided into #intervals For each interval, the maximum
     * intensity would be 50, the other intensities would be rescaled by its
     * ratio to the maximum intensity and multiplied by 50
     *
     * @param expSpectrum an experimental spectrum
     * @param intervals number of intervals dividing this given spectrum
     * @param normalize_value the value that would all intensities would be
     * normalized by
     */
    public DivideAndNormalize(Spectrum expSpectrum, int intervals, double normalize_value) {
        super.expSpectrum = expSpectrum;
        this.intervals = intervals;
        this.normalize_value = normalize_value;
    }

    public int getIntervals() {
        return intervals;
    }

    public double getNormalize_value() {
        return normalize_value;
    }

    @Override
    public void process() {
        double minMz = expSpectrum.getMinMz(),
                maxMZ = expSpectrum.getMaxMz();
        // Introducing 0.0000001 in order to make sure the last peak would be in the last bin
        double interval_size = (maxMZ - minMz + 0.0000001) / intervals; // divide one spectrum into 10 intervals
        double tmp_lower_interval = minMz, // inclusive
                tmp_upper_interval = minMz + interval_size; // exclusive...
        ArrayList<Peak> interval_peaks = new ArrayList<Peak>(); // peaks within interval
        double max_intensity_interval = 0;
        for (int index_exp = 0; index_exp < expSpectrum.getOrderedMzValues().length; index_exp++) {
            double tmpMZ = expSpectrum.getOrderedMzValues()[index_exp];
            Peak tmpPeak = expSpectrum.getPeakMap().get(tmpMZ);
            if (tmpMZ >= tmp_lower_interval && tmpMZ < tmp_upper_interval) {
                interval_peaks.add(tmpPeak);
                if (max_intensity_interval < tmpPeak.intensity) {
                    max_intensity_interval = tmpPeak.intensity;
                }
            }
            if ((tmpMZ >= tmp_upper_interval) || (index_exp == expSpectrum.getOrderedMzValues().length - 1)) {
                // normalize all up to 50
                for (Peak p : interval_peaks) {
                    double normalized_intensity = (double) (p.intensity * normalize_value) / (double) max_intensity_interval;
                    filteredPeaks.add(new Peak(p.mz, normalized_intensity));
                }
                // reset parameters
                max_intensity_interval = 0;
                interval_peaks = new ArrayList<Peak>();
                tmp_lower_interval += interval_size;
                tmp_upper_interval += interval_size;
                if ((index_exp != expSpectrum.getOrderedMzValues().length - 1)) {
                    index_exp = index_exp - 1; // making sure to correctly starting if this is not the last interval...
                }
            }
        }
    }

}
