/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 * This class scales a spectrum into bins. It is used to compare two spectra,
 * therefore the min and the max m/z values must be known to scale a spectrum
 * into bins. A weight of each bin equals to sum/mean/median of all intensities
 * in the given range. It is not possible to modify constructed bin_spectrum.
 *
 *
 * @author Sule
 */
public class BinMSnSpectrum extends BinSpectrum {

    private MSnSpectrum spectrum;
    private ArrayList< double[]> binSpectra = new ArrayList<double[]>(); // list of binMsnSpectrum objects
    protected ArrayList<Peak> peakList = new ArrayList<Peak>();
    protected int intensities_sum_or_mean_or_median = 0, // 0= Sum. 1=searched against MeanRespectrum 2=searched against MedianRespectrum
            correctionFactor = 74,
            bin_size=0;
    private boolean isSlidingDotProductCalculated = false;

    /**
     *
     * @param spectrum
     * @param min_value
     * @param max_value
     * @param fragment_tolerance
     * @param intensities_sum_or_mean_or_median
     * @param correctionFactor for sliding-dot product calculation
     */
    public BinMSnSpectrum(MSnSpectrum spectrum, double min_value, double max_value, double fragment_tolerance, int intensities_sum_or_mean_or_median, int correctionFactor) {
//        super(min_value, max_value, fragment_tolerance);
        super.min_value = min_value;
        super.max_value = max_value;
        super.fragment_tolerance = fragment_tolerance;
        this.isSlidingDotProductCalculated = true;
        this.correctionFactor = correctionFactor;
        this.intensities_sum_or_mean_or_median = intensities_sum_or_mean_or_median;
        this.spectrum = spectrum;
        for (Peak p : spectrum.getPeakList()) {
            peakList.add(p);
        }
        construct_bin_spectrum();
    }

    public BinMSnSpectrum(MSnSpectrum spectrum, double min_value, double max_value, double fragment_tolerance, int intensities_sum_or_mean_or_median) {
//        super(min_value, max_value, fragment_tolerance);
        super.min_value = min_value;
        super.max_value = max_value;
        super.fragment_tolerance = fragment_tolerance;
        this.intensities_sum_or_mean_or_median = intensities_sum_or_mean_or_median;
        this.spectrum = spectrum;
        for (Peak p : spectrum.getPeakList()) {
            peakList.add(p);
        }
        construct_bin_spectrum();
    }

    public BinMSnSpectrum(ArrayList<Peak> fP_spectrumA, double min_value, double max_value, double fragment_tolerance, int intensities_sum_or_mean_or_median) {
        super.min_value = min_value;
        super.max_value = max_value;
        super.fragment_tolerance = fragment_tolerance;
        this.intensities_sum_or_mean_or_median = intensities_sum_or_mean_or_median;
        peakList = fP_spectrumA;
        construct_bin_spectrum();
    }

    public BinMSnSpectrum(ArrayList<Peak> fP_spectrumA, double min_value, double max_value, double fragment_tolerance, int intensities_sum_or_mean_or_median, int correctionFactor) {
//        super(min_value, max_value, fragment_tolerance);
        super.min_value = min_value;
        super.max_value = max_value;
        super.fragment_tolerance = fragment_tolerance;
        this.isSlidingDotProductCalculated = true;
        this.correctionFactor = correctionFactor;
        this.intensities_sum_or_mean_or_median = intensities_sum_or_mean_or_median;
        peakList = fP_spectrumA;
        construct_bin_spectrum();
    }

    /* Getter and setter methods */
    /**
     * This method returns a spectrum that is going to converted to bins. Note
     * that size of bin spectrum will be still the same since min and max values
     * have not changed. Only weights on bin would be changed.
     *
     * @return an MSnSpectrum to be converted bin version.
     */
    public MSnSpectrum getSpectrum() {
        return spectrum;
    }

    public ArrayList<double[]> getBinSpectra() {
        return binSpectra;
    }

    /**
     * This methods scales an MSnSpectrum object into bins. The range of bins
     * starts from minimum value (minimum m/z value between spectra that are
     * going to be compared). Size of each bin equals to 2*fragment_tolerance.
     * Weight of each bin equals to sum of all intensities between the range in
     * selection (including peak on lowest border and excluding peaks on highest
     * border). Upper limit is max_value+0.00001 to make sure that if the last
     * peak is exactly on the border, intensity value would be added. From all
     * picked peaks, the weight of a bin in selection is assigned by sum/mean/or
     * median of all picked intensities. It fills double array with these
     * intensity values and then returns bin_scaled_spectrum.
     *
     */
    @Override
    public void construct_bin_spectrum() {
        if (!is_scaled_into_bins) {
            bin_spectrum = getBin_spectrum(0);
            is_scaled_into_bins = true;

            // if it is asked, also fill in mz shifted binSpectra
            if (isSlidingDotProductCalculated) {
                binSpectra = prepareBinSpectra();
            }
        }
    }

    public int getCorrectionFactor() {
        return correctionFactor;
    }

    public boolean isIsSlidingDotProductCalculated() {
        return isSlidingDotProductCalculated;
    }

    public double[] getBin_spectrum(int shift) {
        ArrayList<Double> bin_spec_al = new ArrayList<Double>();
        double binSize = (fragment_tolerance * 2),
                upperLimit = max_value + 0.00001;
        for (double lowerLimit = min_value; lowerLimit < upperLimit; lowerLimit = lowerLimit + binSize) {
            double tmp_intensity_bin = 0;
            DescriptiveStatistics obj = new DescriptiveStatistics();
            for (Peak p : peakList) {
                double mz = p.getMz() + shift;
                if (mz >= lowerLimit && mz < lowerLimit + binSize) {
                    obj.addValue(p.intensity);
                }
            }
            if (obj.getN() > 0) {
                if (intensities_sum_or_mean_or_median == 0) {
                    tmp_intensity_bin = obj.getSum();
                } else if (intensities_sum_or_mean_or_median == 1) {
                    tmp_intensity_bin = obj.getMean();
                } else if (intensities_sum_or_mean_or_median == 2) {
                    tmp_intensity_bin = obj.getPercentile(50);
                }
            }
            // put every bin_pectrum
            bin_spec_al.add(tmp_intensity_bin);
        }
        // convert an arraylist to double array
        // initiate size of array
        bin_size = bin_spec_al.size();
        double[] bin_spectrum = new double[bin_spec_al.size()];
        for (int i = 0; i < bin_spec_al.size(); i++) {
            bin_spectrum[i] = bin_spec_al.get(i);
        }
        return bin_spectrum;
    }

    private ArrayList<double[]> prepareBinSpectra() {
        // first prepare bin-spectrum to be filled with zero
        int size = (2 * correctionFactor) + 1;
        
        ArrayList<double[]> shiftedSpectra = new ArrayList<double[]>(size);
        for(int i = 0; i<size; i++){
            double[] shiftedSpectrum = new double[bin_size];
            for (int j = 0; j<bin_size;j++){
                shiftedSpectrum[j]=0;
            }
            shiftedSpectra.add(shiftedSpectrum);
        }        
        // now fill each bin spectrum with correct mz values.
        double binSize = (fragment_tolerance * 2),
                upperLimit = max_value + 0.00001;
        int current_index = 0;
        for (double lowerLimit = min_value+correctionFactor; lowerLimit < upperLimit-correctionFactor; lowerLimit = lowerLimit + binSize) {
            double tmp_intensity_bin = 0;
            DescriptiveStatistics obj = new DescriptiveStatistics();
            for (Peak p : peakList) {
                double mz = p.getMz();
                if (mz >= lowerLimit && mz < lowerLimit + binSize) {
                    obj.addValue(p.intensity);
                }
            }
            if (obj.getN() > 0) {
                if (intensities_sum_or_mean_or_median == 0) {
                    tmp_intensity_bin = obj.getSum();
                } else if (intensities_sum_or_mean_or_median == 1) {
                    tmp_intensity_bin = obj.getMean();
                } else if (intensities_sum_or_mean_or_median == 2) {
                    tmp_intensity_bin = obj.getPercentile(50);
                }
            }
            // put every bin_pectrum            
            int filling_index = current_index;
            // check every bin spectrum            
            for (double[] shifted : shiftedSpectra) {
                shifted[filling_index] = tmp_intensity_bin;
                filling_index++;
            }
            current_index++;
        }
        return shiftedSpectra;
    }

}
