/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.implementation;

import com.compomics.pride_asa_pipeline.logic.spectrum.filter.impl.NoiseThresholdFinderImpl;
import preprocess.filter.noise.interfaces.NoiseFilter;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This noise filtering was derived from PrideAsap 
 * 
 */
public class NoiseFilteringPrideAsap implements NoiseFilter {

    private static final double PRECURSOR_MASS_WINDOW = 18.0;

    public NoiseFilteringPrideAsap() {        
    }

    @Override
    public MSnSpectrum noiseFilter(MSnSpectrum ms) {
        NoiseThresholdFinderImpl threshold_finder = new NoiseThresholdFinderImpl();
        double[] intensityValuesAsArray = ms.getIntensityValuesAsArray();
        double threshold = threshold_finder.findNoiseThreshold(intensityValuesAsArray),
                precursor_mz = ms.getPrecursor().getMz();
        Collection<Peak> peaks = ms.getPeakList();
        if (peaks == null) {
            return null;
        }
        ArrayList<Peak> result = new ArrayList<Peak>();
        for (Peak peak : peaks) {
            //add the peak to the peak list if the peak intensity > threshold
            // and if the MZ ratio is not in 18D range of experimental precursor mass
            if (peak.getIntensity() >= threshold && !(precursor_mz - PRECURSOR_MASS_WINDOW < peak.mz && peak.mz < precursor_mz + PRECURSOR_MASS_WINDOW)) {
                result.add(peak);
            }
        }
        ms.getPeakList().clear();
        ms.setPeaks(result);
        return ms;
    }
}
