/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.precursor;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

/**
 * This class enables removal of precursor ion from a given experimental spectrum
 * 
 * @author Sule
 */
public abstract class RemovePrecursor {

    protected MSnSpectrum ms;
    protected double fragmentTolerance;

    /**
     *
     * @param ms an MSnSpectrum object
     * @param fragmentTolerance fragment tolerance to pick peaks close to
     * precursor ion
     */
    public RemovePrecursor(MSnSpectrum ms, double fragmentTolerance) {
        this.ms = ms;
        this.fragmentTolerance = fragmentTolerance;
    }

    /**
     * This method to remove any precursor peak from an already given MSnSpectrum on the
     * constructor.. 
     */
    public abstract void removePrecursor();

}
