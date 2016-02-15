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

/**
 * This class discards peaks with intensities smaller than
 * (Percentage*MaximumIntensity). The default percentage is 5%
 *
 * @author Sule
 */
public class DiscardLowIntensePeaks implements NoiseFilter {

    private double percentage = 5.00; // X% of the highest intensity

    public DiscardLowIntensePeaks(double percentage) {
        this.percentage = percentage;
    }

    /**
     * This method discards peaks with intensities less than given percentage
     * (on constructor) of the maximum intensities.
     *
     * Default percentage is 5%
     *
     */
    @Override
    public MSnSpectrum noiseFilter(MSnSpectrum ms) {
        // select the peak with the most intense
        double highest_intensity = Double.MIN_VALUE;
        for (Peak p : ms.getPeakList()) {
            double tmp_intensity = p.getIntensity();
            if (highest_intensity < tmp_intensity) {
                highest_intensity = tmp_intensity;
            }
        }
        // find the threshold value and keep only the one higher than threshold
        double threshold = (highest_intensity * percentage) / 100;
        ArrayList<Peak> subList = new ArrayList<Peak>();
        for (Peak p : ms.getPeakList()) {
            double tmp_intensity = p.getIntensity();
            if (tmp_intensity >= threshold) {
                subList.add(p);
            }
        }
        ms.getPeakList().clear();
        ms.setMzOrdered(false);
        ms.setPeaks(subList);
        return ms;
    }

}
