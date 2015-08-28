/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.filter.main;

import preprocess.filter.noise.implementation.NoiseFilteringPrideAsap;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import com.compomics.util.waiting.WaitingHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class RunNoiseFiltering {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {

        File mgfs = new File("C:/Users/Sule/Documents/NetBeansProjects/TapeWormAnalysis/Data/mgfs/Proteowizard/Step4_after_UNAMdb_analysis/Goat_taenia_hydatigena_after_unam_analysis");
        int num = 0;
        // count spectra size before and after
        File result = new File("goat_all_spec_number.txt");
        BufferedWriter bw = new BufferedWriter(new FileWriter(result));
        bw.write("SpecName" + "\t" + "Before_Spec_Size" + "\t" + "After_Spec_Size" + "\n");
        WaitingHandler wr = new WaitingHandlerCLIImpl();
        for (File mgf : mgfs.listFiles()) {
            num = 0;
            if (mgf.getName().endsWith("mgf")) {
                File newMgf = new File(mgf.getName().substring(0, mgf.getName().indexOf(".mgf")) + "_noisefiltered_2.mgf");
                BufferedWriter bw2 = new BufferedWriter(new FileWriter(newMgf));
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf, wr);
                // System.out.println(mgf.getName());
                for (String title : fct.getSpectrumTitles(mgf.getName())) {
                    MSnSpectrum msms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title);
                    int before = msms.getPeakList().size();
                    NoiseFilteringPrideAsap noiseFilterImp = new NoiseFilteringPrideAsap();
                    noiseFilterImp.noiseFilter(msms);
                    int after = msms.getPeakList().size();
                    String asMgf = msms.asMgf();
                    bw2.write(asMgf);
                    bw2.newLine();
                    System.out.println("Before=" + "\t" + before + "\t" + "after=" + after + "\t");
                    bw.write(msms.getSpectrumTitle() + "\t" + before + "\t" + after + "\n");
                }
                // bw.write(mgf.getName() + "\t" + num + "\t"+ before+ "\t"+ after "\n");
                // System.out.println(mgf.getName() + "\t" + num);
                fct.clearFactory();
                bw2.close();
            }
        }
        bw.close();
    }
}
