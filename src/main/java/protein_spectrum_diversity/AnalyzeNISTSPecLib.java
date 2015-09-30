/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protein_spectrum_diversity;

import cal.cumulativeBinomialProbability.spectra.CompareAndScore;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import config.ConfigHolder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.configuration.ConfigurationException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This method works on one mgf files which contains spectra from NIST-spectral library.
 * According to the given highCoveragedProteinList, it selects any spectra from peptide derived from this highCoveragedProteinList.
 * For each protein in the highCoveragedProteinList, it calculates spectra for peptides of one protein.
 * 
 * 
 * @author Sule
 */
public class AnalyzeNISTSPecLib {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ConfigurationException, ConfigurationException, IOException, MzMLUnmarshallerException, Exception {
        double fragTol = 0.5;
        int msRobinOption = ConfigHolder.getInstanceProteinDiversity().getInt("msrobin"),
                msRobinIntensity = ConfigHolder.getInstanceProteinDiversity().getInt("msrobinintensityoption"),
                run_INTOpt = ConfigHolder.getInstanceProteinDiversity().getInt("charge.option");
        SpectrumFactory fct = SpectrumFactory.getInstance();
        String spectraName = ConfigHolder.getInstanceProteinDiversity().getString("spectra.folder.name"),
                clFileName = ConfigHolder.getInstanceProteinDiversity().getString("cl.file.name"),
                outputFolderName = ConfigHolder.getInstanceProteinDiversity().getString("output.folder.name"),
                highCoveragedProteinList = ConfigHolder.getInstanceProteinDiversity().getString("high.coveraged.protein.list");
        File spectraFile = new File(spectraName),
                highCovaredProteinsList = new File(highCoveragedProteinList);

        HashMap<String, ArrayList<CLEntry>> accession_and_peptides = getAccessionAndPeptides(new File(clFileName), highCovaredProteinsList);
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(outputFolderName + "\\" + "all_output_only_doubly_charged.txt"));
        String outputTitle = "Accession" + "\t" + "spectrum" + "\t" + "spectrumToCompare" + "\t" + "probability" + "\t" + "intensity" + "\t" + "MSRobin" + "\n";
        bw2.write(outputTitle);

        // load all spectra
        fct.addSpectra(spectraFile, new WaitingHandlerCLIImpl());
        
        for (String acc : accession_and_peptides.keySet()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolderName + "\\" + acc + "_output.txt"));
            bw.write(outputTitle);
            // get theoeretical spectra
            ArrayList<CLEntry> clentries = accession_and_peptides.get(acc);
            ArrayList<CLEntry> toAnalyze = new ArrayList<CLEntry>();
            // select all possible MS/MS spectra derived from one protein
            for (CLEntry cl : clentries) {
                MSnSpectrum tmpMSMS = (MSnSpectrum) fct.getSpectrum(spectraFile.getName(), String.valueOf(cl.getSpectrum_id()));
                cl.setMsms(tmpMSMS);
                toAnalyze.add(cl);
            }
            // calculate...
            for (int i = 0; i < toAnalyze.size() - 1; i++) {
                boolean doesCompare = false;
                MSnSpectrum tmpSp = toAnalyze.get(i).getMsms();
                if (tmpSp.getPrecursor().getPossibleChargesAsString().equals("2+") && run_INTOpt == 0) {
                    doesCompare = true;
                }
                if (tmpSp.getPrecursor().getPossibleChargesAsString().equals("3+") && run_INTOpt == 1) {
                    doesCompare = true;
                }
                if (run_INTOpt == 2) {
                    doesCompare = true;
                }
                // check also 
                if (doesCompare) {
                    for (int j = i + 1; j < toAnalyze.size() - 1; j++) {
                        boolean doesCompareToCompare = false;
                        MSnSpectrum tmpNextSp = toAnalyze.get(j).getMsms();
                        if (tmpNextSp.getPrecursor().getPossibleChargesAsString().equals("2+") && run_INTOpt == 0) {
                            doesCompareToCompare = true;
                        }
                        if (tmpNextSp.getPrecursor().getPossibleChargesAsString().equals("3+") && run_INTOpt == 1) {
                            doesCompareToCompare = true;
                        }
                        if (run_INTOpt == 2) {
                            doesCompareToCompare = true;
                        }
                        if (doesCompareToCompare) {
                            CompareAndScore comparison = new CompareAndScore(tmpSp, tmpNextSp, fragTol, msRobinOption, msRobinIntensity);
                            double score = comparison.getMSRobinScore();
                            bw.write(acc + "\t" + tmpSp.getSpectrumTitle() + "\t" + tmpNextSp.getSpectrumTitle() + "\t"
                                    + comparison.getProbability_part() + "\t" + comparison.getIntensity_part() + "\t" + score);
                            bw.newLine();

                            bw2.write(acc + "\t" + tmpSp.getSpectrumTitle() + "\t" + tmpNextSp.getSpectrumTitle() + "\t"
                                    + comparison.getProbability_part() + "\t" + comparison.getIntensity_part() + "\t" + score);
                            bw2.newLine();
                        }
                    }
                }
            }
            bw.close();
        }
        bw2.close();
    }

    private static HashMap<String, ArrayList<CLEntry>> getAccessionAndPeptides(File file, File highCovaredProteinsList) throws IOException {
        ArrayList<String> highCovareged = getList(highCovaredProteinsList);
        HashMap<String, ArrayList<CLEntry>> accsApeptides = new HashMap<String, ArrayList<CLEntry>>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("spec_id")) {
                CLEntry obj = new CLEntry(line);
                String protein = obj.getProtein();
                if (accsApeptides.containsKey(protein) && highCovareged.contains(protein)) {
                    accsApeptides.get(protein).add(obj);
                } else if (!accsApeptides.containsKey(protein) && highCovareged.contains(protein)) {
                    ArrayList<CLEntry> clentries = new ArrayList<CLEntry>();
                    clentries.add(obj);
                    accsApeptides.put(protein, clentries);
                }
            }
        }
        return accsApeptides;
    }

    private static ArrayList<String> getList(File highCovaredProteinsList) throws FileNotFoundException, IOException {
        ArrayList<String> list = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(highCovaredProteinsList));
        String line = "";
        while ((line = br.readLine()) != null) {
            list.add(line);
        }
        return list;
    }

}
