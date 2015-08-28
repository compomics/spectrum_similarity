/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability;

import cal.cumulativeBinomialProbability.spectra.CompareAndScore;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class Testing {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, MzMLUnmarshallerException, ClassNotFoundException {

        File first_mgf = new File("C:\\Users\\Sule\\Desktop\\sim/yeast_human_test.mgf"),
                second_mgf = new File("C:\\Users\\Sule\\Desktop\\sim/yeast_test.mgf");
        ArrayList<MSnSpectrum> first_spectra = new ArrayList<MSnSpectrum>(),
                second_spectra = new ArrayList<MSnSpectrum>();
        CompareAndScore match;
        int intensityOption = 0;
        SpectrumFactory fct = SpectrumFactory.getInstance();

        if (first_mgf.getName().endsWith(".mgf")) {
            fct.clearFactory();
            fct.addSpectra(first_mgf);
            for (String title : fct.getSpectrumTitles(first_mgf.getName())) {
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(first_mgf.getName(), title);
                first_spectra.add(ms);
            }
        }

        if (second_mgf.getName().endsWith(".mgf")) {
            fct.clearFactory();
            fct.addSpectra(second_mgf);
            for (String title : fct.getSpectrumTitles(second_mgf.getName())) {
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(second_mgf.getName(), title);
                second_spectra.add(ms);
            }
        }

        for (MSnSpectrum msms : first_spectra) {
            if (!msms.getPeakList().isEmpty()) {
//                Transformations tr = Transformations.LOG_2;;
//                TransformIntensitiesImp transform = new TransformIntensitiesImp(tr, msms);
//                transform.transform(tr);
//                msms.setPeakList(transform.getTr_peaks());
                for (MSnSpectrum msms_second : second_spectra) {
                    if (!msms_second.getPeakList().isEmpty()) {

//                        TransformIntensitiesImp transform2 = new TransformIntensitiesImp(tr, msms_second);
//                        transform2.transform(tr);
//                        msms_second.setPeakList(transform2.getTr_peaks());
                        match = new CompareAndScore(msms, msms_second, 0.5, 0, intensityOption);
                        double psMscore = match.getMSRobinScore();
                        System.out.println("Option1" + "\t" + msms.getSpectrumTitle() + "\t" + msms_second.getSpectrumTitle() + "\t" + "similarity score=" + "\t" + psMscore);

                        match = new CompareAndScore(msms, msms_second, 0.5, 1,intensityOption);
                        psMscore = match.getMSRobinScore();
                        System.out.println("Option2" + "\t" + msms.getSpectrumTitle() + "\t" + msms_second.getSpectrumTitle() + "\t" + "similarity score=" + "\t" + psMscore);
                        
                        System.out.println("**************");
                    }
                }
            }
        }

    }

}
