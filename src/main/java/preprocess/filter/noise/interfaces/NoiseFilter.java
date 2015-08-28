/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.interfaces;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

/**
 *
 * @author Sule
 */
public interface NoiseFilter {
    
    /**
     * This method applies noise filtering a given MSnSpectrum object.
     * @param ms
     * @return 
     */
    public MSnSpectrum noiseFilter(MSnSpectrum ms);
    
}
