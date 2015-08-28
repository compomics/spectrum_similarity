/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.precursor;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;

/**
 *
 * This class removes an m/z window around a precursor ion
 *
 * @author Sule
 */
public class RemoveWindowAround extends RemovePrecursor {

    private double windowSize = 10; // 10u as the default value

    /**
     * Remove any peaks within 10 window size around the precursor ion
     *
     * @param msms an MSnSpectrum object
     * @param fragmentTolerance fragment tolerance to set a size of a window
     * around precursor ion
     */
    public RemoveWindowAround(MSnSpectrum msms, double fragmentTolerance) {
        super(msms, fragmentTolerance);

    }

    /**
     * Remove any peaks within windowSize window size around the precursor ion
     *
     * @param msms an MSnSpectrum object
     * @param fragmentTolerance fragment tolerance to set a size of a window
     * around precursor ion
     * @param windowSize a window around a precursor ion
     *
     */
    public RemoveWindowAround(MSnSpectrum msms, double fragmentTolerance, double windowSize) {
        super(msms, fragmentTolerance);
        this.windowSize = windowSize;

    }

    /**
     * This method removes a window around precursor ion
     */
    @Override
    public void removePrecursor() {
        double precursorMZ = ms.getPrecursor().getMz();
        ArrayList<Peak> keptPeaks = new ArrayList<Peak>();
        ArrayList<Double> mzsWithinAWindow = new ArrayList<Double>();
        double[] mzValuesAsArray = ms.getOrderedMzValues();
        // remove any peak if mz is smaller than  and bigger than or equal to 
        for (int i = 0; i < mzValuesAsArray.length; i++) {
            double mz = mzValuesAsArray[i];
            if (mz < (precursorMZ + (windowSize/2)) && mz > (precursorMZ - (windowSize/2))) {
                mzsWithinAWindow.add(mz);
            }
        }
        // update a peak list...
        for (Double mz : ms.getPeakMap().keySet()) {
            if (!mzsWithinAWindow.contains(mz)) {
                keptPeaks.add(ms.getPeakMap().get(mz));
            }
        }
        // now clear peak list from possibly derived from precursor peaks
        ms.getPeakList().clear();
        ms.setMzOrdered(false);
        ms.setPeaks(keptPeaks);
    }

}
