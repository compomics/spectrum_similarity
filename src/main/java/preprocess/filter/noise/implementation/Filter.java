/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.noise.implementation;

import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * This class filter or normalize peaks on a given MSnSpectrum.
 *
 * @author Sule
 */
public abstract class Filter {

    protected Spectrum expSpectrum;
    protected ArrayList<Peak> filteredPeaks= new ArrayList<Peak>();
    protected Logger LOGGER;

    /**
     * Apply filtering-process
     */
    protected abstract void process() ;

    public Spectrum getExpMSnSpectrum() {
        return expSpectrum;
    }

    public void setExpMSnSpectrum(Spectrum expMSnSpectrum) {
        this.expSpectrum = expMSnSpectrum;
    }

    /**
     * This method returns a list of filtered-peaks from given MSnSpectrum.
     * 
     * @return an arraylist of Peaks
     */
    public ArrayList<Peak> getFilteredPeaks() {
        if (filteredPeaks.isEmpty()&& !expSpectrum.getPeakList().isEmpty()) {
            process();
        }
        return filteredPeaks;
    }
}
