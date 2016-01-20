/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.precursor;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;

/**
 * This class removes any peak within a fragment tolerance. These peaks were
 * found to be derived from a precursor ion
 *
 * @author Sule
 */
public class RemovePrecursorRelatedPeaks extends RemovePrecursor {

    /**
     *
     * @param msms an MSnSpectrum object
     * @param fragmentTolerance fragment tolerance to pick peaks close to
     * precursor ion
     */
    public RemovePrecursorRelatedPeaks(MSnSpectrum msms, double fragmentTolerance) {
        super(msms, fragmentTolerance);
    }

    /**
     * This method finds out any peak within a fragment tolerance which is
     * derived from a precursor
     *
     */
    @Override
    public void removePrecursor() {
        ArrayList<Double> precursorPeaksMZ = new ArrayList<Double>();
        ArrayList<Peak> peaksToRemove = new ArrayList<Peak>(),
                peaks = new ArrayList<Peak>(ms.getPeakList());
        // get a precursor charge 
        double precursorMZ = ms.getPrecursor().getMz();
        int charge = ms.getPrecursor().getPossibleCharges().get(ms.getPrecursor().getPossibleCharges().size() - 1).value;
        precursorPeaksMZ.add(precursorMZ);
        // first select peaks may derive from a precursor
        double precursorMass = ms.getPrecursor().getMass(charge),
                protonTheoMass = ElementaryIon.proton.getTheoreticMass();
        if (charge >= 1) {
            while (charge >= 1) {
                double tmpMZ = (precursorMass + (protonTheoMass * charge)) / charge;
                precursorPeaksMZ.add(tmpMZ);
                charge--;
            }
        }
        // Now check actual peaks to get them
        int startIndex = 0;
        for (Double possiblePrecursorMZ : precursorPeaksMZ) {
            Peak removedPeak = null;
            boolean found_a_close_peak = false;
            // to find a closest peak
            double tmpFragmentTolerance = fragmentTolerance;
            for (int i = startIndex; i < peaks.size(); i++) {
                Peak tmpPeak = peaks.get(i);
                double diffMZ = Math.abs(tmpPeak.getMz() - possiblePrecursorMZ);
                if (diffMZ <= tmpFragmentTolerance) {
                    tmpFragmentTolerance = diffMZ;
                    removedPeak = ms.getPeakMap().get(tmpPeak.getMz());
                    if (i > startIndex && !found_a_close_peak) {
                        startIndex = i;
                        found_a_close_peak = true;
                    }
                }
            }
            if (removedPeak != null) {
                peaksToRemove.add(removedPeak);
            }
        }
        // now clear peak list from possibly derived from precursor peaks
        peaks.removeAll(peaksToRemove);
        ms.getPeakList().clear();
        ms.setMzOrdered(false);
        ms.setPeaks(peaks);
    }

}
