/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

/**
 *
 * @author Sule
 */
public abstract class BinSpectrum {
    protected double min_value, // minimum mz value of the two spectra that are compared
            max_value, // maximum value of the two spectra that are compared
            fragment_tolerance; //to set size of a bin    
    protected double[] bin_spectrum; //bin versioned spectrum with sum of all intensities
    protected boolean is_scaled_into_bins; // false shows that a spectrum is not binned yet, true shows that a binspectrum is generated. 
    

    /* Getter and setter methods */
    public boolean isIs_scaled_into_bins() {
        return is_scaled_into_bins;
    }

    public void setIs_scaled_into_bins(boolean is_scaled_into_bins) {
        this.is_scaled_into_bins = is_scaled_into_bins;
    }
    
        
    /**
     * This method returns a min value used to start minimum bin creation
     * (double value of min m/z)
     *
     * @return
     */
    public double getMin_value() {
        return min_value;
    }

    /**
     * This method returns a max value used to start minimum bin creation
     * (double value of min m/z)
     *
     * @return
     */
    public double getMax_value() {
        return max_value;
    }

    /**
     * This method returns a fragment tolerance that is used to set a size of a
     * bin Note that a bin size is 2*fragment tolerance
     *
     * @return a fragment tolerance
     */
    public double getFragment_tolerance() {
        return fragment_tolerance;
    }

    /**
     * This method returns a bin spectrum
     *
     * @return a double array with weight of each unit is sum of intensities on
     * a given range
     */
    public double[] getBin_spectrum() {
        if (!is_scaled_into_bins) {
            construct_bin_spectrum();
        }
        return bin_spectrum;
    }

    /**
     * This methods converts an MSnSpectrum object into bin scaled version. The
     * range of bins starts from minimum value (minimum m/z value between
     * spectra that are going to be compared). Size of each bin equals to
     * 2*fragment_tolerance. Weight of each bin equals to sum of all intensities
     * between the range in selection (including peak on lowest border and
     * excluding peaks on highest border). Upper limit is max_value+0.00001 to
     * make sure that if the last peak is exactly on the border, intensity value
     * would be added. It fills double array with these intensity values and
     * then returns bin_scaled_spectrum.
     *
     */
    public abstract void construct_bin_spectrum();

}


