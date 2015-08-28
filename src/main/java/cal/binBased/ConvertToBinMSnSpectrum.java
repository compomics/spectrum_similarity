/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

import preprocess.filter.noise.implementation.DiscardLowIntensePeaks;
import preprocess.filter.noise.implementation.NoiseFilteringPrideAsap;
import preprocess.filter.noise.implementation.TopNFiltering;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.preprocess.transformation.implementation.TransformIntensitiesImp;
import com.compomics.util.experiment.massspectrometry.preprocess.transformation.methods.Transformations;
import java.io.FileNotFoundException;
import java.io.IOException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class converts an MSnSpectrum object to BinMSnSpectrum object to
 * calculate similarities
 *
 * @author Sule
 */
public class ConvertToBinMSnSpectrum {

    private double min_mz,// min mz to start binning
            max_mz,// max mz to end binnig
            fragment_tolerance; // to bin
    private int topN, // to apply noise filtering to select topN intense peaks
            percentage,// to apply noise filtering to remove peak with intensities less than percentage*maximum intensity
            noise_filtering_case,
            transformation_case,
            intensities_sum_or_mean_or_median;

    /**
     * Constructor
     *
     * @param min_mz minimum mz from all over data set to start binning
     * @param max_mz maximum mz from all over data set to end binnin
     * @param topN to apply noise filtering to select topN intense peaks
     * @param percentage to apply noise filtering to remove peak with
     * intensities less than percentage*maximum intensity
     * @param fragment_tolerance to bin spectra bin size = 2*fragment_tolerance
     * @param noise_filtering_case 0-nothing,1-PrideAsapNoiseFiltering
     * 2-TopNFiltering 3-DiscardLowIntensePeaks
     * @param transformation_case 0-nothing, 1-Log2, 2-Sqr root
     * @param intensities_sum_or_mean_or_median 0-sum, 1-mean, 2-median
     */
    public ConvertToBinMSnSpectrum(double min_mz, double max_mz, int topN, int percentage, double fragment_tolerance, int noise_filtering_case, int transformation_case, int intensities_sum_or_mean_or_median) {
        this.min_mz = min_mz;
        this.max_mz = max_mz;
        this.topN = topN;
        this.percentage = percentage;
        this.fragment_tolerance = fragment_tolerance;
        this.noise_filtering_case = noise_filtering_case;
        this.transformation_case = transformation_case;
        this.intensities_sum_or_mean_or_median = intensities_sum_or_mean_or_median;
    }

    /**
     * This method enables converting MSnSpectrum into BinMSnSpectrum to
     * calculate similarities
     *
     * @param ms an MSnSpectrum object
     * @param isNFTR true: first noise filtering then intensity transformation,
     * false; first intensity transformation then noise filtering
     *
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws MzMLUnmarshallerException
     */
    public BinMSnSpectrum convertToBinMSnSpectrum(MSnSpectrum ms, boolean isNFTR)
            throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        BinMSnSpectrum bin_msms = null;
        // first run noise filtering
        if (isNFTR) {
            if (noise_filtering_case > 0) {
                apply_noise_filtering(ms);
            }
            // then transform intensities..
            if (transformation_case > 0) {
                transform_intensities(ms);
            }
        } else {
            // first transformation 
            if (transformation_case > 0) {
                transform_intensities(ms);
            }
            // then noise filtering
            if (noise_filtering_case > 0) {
                apply_noise_filtering(ms);
            }
        }
        // create a BinMSnSpectrum from MSnSpectrum object
        bin_msms = new BinMSnSpectrum(ms, min_mz, max_mz, fragment_tolerance, intensities_sum_or_mean_or_median);
        return bin_msms;
    }

    /**
     * This class transforms intensities either Log2 or Square root 1-Log2
     * 2-Square root
     *
     * @param transformation_case
     * @param ms an MSnSpectrum object
     */
    private void transform_intensities(MSnSpectrum ms) {
        Transformations tr = null;
        TransformIntensitiesImp transform = null;
        switch (transformation_case) {
            case 1: // Log 2
                tr = Transformations.LOG_2;
                transform = new TransformIntensitiesImp(tr, ms);
                transform.transform(tr);
                ms.setPeakList(transform.getTr_peaks());
                break;
            case 2: // Square root
                tr = Transformations.SQR_ROOT;
                transform = new TransformIntensitiesImp(tr, ms);
                transform.transform(tr);
                ms.setPeakList(transform.getTr_peaks());
                break;
        }
    }

    /**
     * This class applies noise filtering 1 - PrideAsapNoiseFiltering 2 -
     * TopNFiltering 3 - DiscardLowIntensePeaks
     *
     * @param noise_filtering_case [0-3]
     * @param ms an MSnSpectrum object
     *
     */
    private void apply_noise_filtering(MSnSpectrum ms) {
        switch (noise_filtering_case) {
            case 1:
                NoiseFilteringPrideAsap noiseFilterImp = new NoiseFilteringPrideAsap();
                noiseFilterImp.noiseFilter(ms);
                break;
            case 2:
                TopNFiltering topNFiltering = new TopNFiltering(topN);
                topNFiltering.noiseFilter(ms);
                break;
            case 3:
                DiscardLowIntensePeaks discardLowIntensePeaks = new DiscardLowIntensePeaks(percentage);
                discardLowIntensePeaks.noiseFilter(ms);
                break;
        }
    }

}
