/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

import static bookChapter.GetSpecAnDID.getUPSSpecAndIDs;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * This class selects UPS-spectra based on their selected mascot scores. Then,
 * it stores them on a file containing these selected spectra
 *
 * @author Sule
 */
public class SelectSpectraToCompare {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, MzMLUnmarshallerException {
        File ups_mgf = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/Orbi2_study6a_W080314_6QC1_sigma48_ft8_pc.mgf"),
                ups_yeast_mgf = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/Orbi2_study6a_W080314_6E008_yeast_S48_ft8_pc.mgf"),
                ups_mascot = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mascot_search/Orbi2_study6a_W080314_6QC1_sigma48_ft8_pc_SvenSPyeast.dat.parsed.ms2pip.percolator_only.ms2pipscore"),
                ups_yeast_mascot = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mascot_search/Orbi2_study6a_W080314_6E008_yeast_S48_ft8_pc_SvenSPyeast.dat.parsed.ms2pip.percolator_only.ms2pipscore");

        File mgf_upsSpecs_at_upsSample = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/exp/Orbi2_study6a_W080314_6QC1_sigma48_ft8_pc_UPS_3.mgf"),
                mgf_upsSpecs_at_upsyeastSample = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/exp/Orbi2_study6a_W080314_6E008_yeast_S48_ft8_pc_UPS_3.mgf"),
                mgf_nonUPSSpecs_at_upsyeastSample = new File("C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/exp/Orbi2_study6a_W080314_6E008_yeast_S48_ft8_pc_NONUPS_3.mgf");
        // select UPS identified spectra to be scored against...
        HashMap<String, Identifications> upsSpecs_at_upsSample = getUPSSpecAndIDs(ups_mascot, 0.05, true),
                upsSpecs_at_upsyeastSample = getUPSSpecAndIDs(ups_yeast_mascot, 0.05, true),
                nonUPSSpecs_at_upsyeastSample = getUPSSpecAndIDs(ups_yeast_mascot, 0.05, false);

        write(mgf_upsSpecs_at_upsSample,
                ups_mgf,
                upsSpecs_at_upsSample);

        write(mgf_upsSpecs_at_upsyeastSample,
                ups_yeast_mgf,
                upsSpecs_at_upsyeastSample);

        write(mgf_nonUPSSpecs_at_upsyeastSample,
                ups_yeast_mgf,
                nonUPSSpecs_at_upsyeastSample);

        // check min m/z and max m/z - would be necessary for binning
        double min_mz = Double.MAX_VALUE,
                max_mz = 0;
        double[] minmz_maxmz = getMinMaxMZ(mgf_upsSpecs_at_upsSample, min_mz, max_mz);
        minmz_maxmz = getMinMaxMZ(mgf_upsSpecs_at_upsyeastSample, minmz_maxmz[0], minmz_maxmz[1]);
        minmz_maxmz = getMinMaxMZ(mgf_nonUPSSpecs_at_upsyeastSample, minmz_maxmz[0], minmz_maxmz[1]);
        System.out.println("min m/z=" + minmz_maxmz[0]);
        System.out.println("max m/z=" + minmz_maxmz[1]);

    }

    private static double[] getMinMaxMZ(File ups_mgf, double min_mz, double max_mz) throws IOException, IllegalArgumentException, MzMLUnmarshallerException {
        double[] mzs = new double[2];
        SpectrumFactory fct = SpectrumFactory.getInstance();
        fct.addSpectra(ups_mgf, new WaitingHandlerCLIImpl());
        // load mgf file
        for (String mgfTitle : fct.getSpectrumTitles(ups_mgf.getName())) {
            MSnSpectrum m = (MSnSpectrum) fct.getSpectrum(ups_mgf.getName(), mgfTitle);
            if (m.getMinMz() < min_mz) {
                min_mz = m.getMinMz();
            }
            if (m.getMaxMz() > max_mz) {
                max_mz = m.getMaxMz();
            }
        }
        mzs[0] = min_mz;
        mzs[1] = max_mz;
        return mzs;
    }

    /**
     * Write selected mgfs on a given file
     *
     * @param mgf
     * @param ups_mgf
     * @param upsSpecs_at_upsSample
     * @throws IOException
     * @throws MzMLUnmarshallerException
     */
    private static void write(File mgf, File ups_mgf, HashMap<String, Identifications> upsSpecs_at_upsSample) throws IOException, MzMLUnmarshallerException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(mgf));
        // read each spectrum on the given file
        SpectrumFactory fct = SpectrumFactory.getInstance();
        fct.addSpectra(ups_mgf, new WaitingHandlerCLIImpl());
        for (String title : upsSpecs_at_upsSample.keySet()) {
            // load mgf file
            for (String mgfTitle : fct.getSpectrumTitles(ups_mgf.getName())) {
                if (mgfTitle.equals(title)) {
                    MSnSpectrum m = (MSnSpectrum) fct.getSpectrum(ups_mgf.getName(), mgfTitle);
                    bw.write(m.asMgf());
                }
            }
        }
        bw.close();
    }

}
