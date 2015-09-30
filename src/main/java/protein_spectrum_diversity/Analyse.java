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
import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.configuration.ConfigurationException;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class directly calculates pairwise similarities
 * Finding spectra method was designed to find spectra on Kenneth's folder structure on NAS
 * 
 * @author Sule
 */
public class Analyse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ConfigurationException, ConfigurationException, IOException, MzMLUnmarshallerException, Exception {

        double fragTol = 0.5;
        int msRobinOption = ConfigHolder.getInstanceProteinDiversity().getInt("msrobinintensityoption"),
                intensityOption = 1;
        SpectrumFactory fct = SpectrumFactory.getInstance();
        String spectraName = ConfigHolder.getInstanceProteinDiversity().getString("spectra.folder.name"),
                peptides = ConfigHolder.getInstanceProteinDiversity().getString("peptide.file.name"),
                rootFolderName = ConfigHolder.getInstanceProteinDiversity().getString("root.folder.name"),
                outputFolderName = ConfigHolder.getInstanceProteinDiversity().getString("output.folder.name");
        File rootFolder = new File(rootFolderName);
        System.out.println(spectraName);

        HashMap<String, ArrayList<String>> accession_and_peptides = getAccessionAndPeptides(new File(peptides));
        // write all pairwise comparisons on one file
        BufferedWriter all = new BufferedWriter(new FileWriter(outputFolderName + "\\" + "all_output.txt"));
        String outputTitle = "Accession" + "\t" + "spectrum" + "\t" + "spectrumToCompare" + "\t" + "probability" + "\t" + "intensity" + "\t" + "MSRobin" + "\n";
        all.write(outputTitle);
        for (String acc : accession_and_peptides.keySet()) {
            // write pairwise comparisons for each proteins on one file separately
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFolderName + "\\" + acc + "_output.txt"));
            bw.write(outputTitle);
            System.out.println(outputFolderName + "\\" + acc + "_output.txt");
            // get theoeretical spectra
            File mergePeptides = mergePeptides(accession_and_peptides.get(acc), rootFolder, outputFolderName, acc);
            if (mergePeptides.getName().endsWith(".mgf")) {
                fct.clearFactory();
                fct.addSpectra(mergePeptides, new WaitingHandlerCLIImpl());
                for (String title : fct.getSpectrumTitles(mergePeptides.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mergePeptides.getName(), title);
                    // read each given file
                    for (String title2 : fct.getSpectrumTitles(mergePeptides.getName())) {
                        if (!title.equals(title2)) {
                            MSnSpectrum ms2 = (MSnSpectrum) fct.getSpectrum(mergePeptides.getName(), title2);
                            CompareAndScore comparison = new CompareAndScore(ms, ms2, fragTol, msRobinOption, intensityOption);
                            bw.write(acc + "\t" + ms.getSpectrumTitle() + "\t"
                                    + ms2.getSpectrumTitle() + "\t" + comparison.getProbability_part() + "\t" + comparison.getIntensity_part() + "\t" + comparison.getMSRobinScore());
                            bw.newLine();
                            all.write(acc + "\t" + ms.getSpectrumTitle() + "\t"
                                    + ms2.getSpectrumTitle() + "\t" + comparison.getProbability_part() + "\t" + comparison.getIntensity_part() + "\t" + comparison.getMSRobinScore());
                            all.newLine();
                        }
                    }
                }
            }
            bw.close();
        }
        all.close();
    }

    public static File getMGFForPeptide(String peptidesequence, File rootFolder) {
        String[] tokens = peptidesequence.split("(?<=\\G.{" + 3 + "})");
        String path = rootFolder.getAbsolutePath();
        for (String aToken : tokens) {
            path = path + "/" + aToken;
        }
        path = path + "/" + peptidesequence + ".mgf";
        return new File(path);
    }

    public static File mergePeptides(Collection<String> sequences, File rootFolder, String outputFolder, String accession) throws IOException {
        File outputFile = new File(outputFolder + File.separator + accession + "_merged.mgf");
        FileWriter out = new FileWriter(outputFile, true);
        for (String aSequence : sequences) {
            File sequenceMGF = getMGFForPeptide(aSequence, rootFolder);
            if (sequenceMGF.exists() && !sequenceMGF.isDirectory()) {
                BufferedReader in = new BufferedReader(new FileReader(sequenceMGF));
                String line = "";
                while ((line = in.readLine()) != null) {
                    out.append(line).append(System.lineSeparator()).flush();
                }
            }
        }
        out.flush();
        return outputFile;
    }

    private static HashMap<String, ArrayList<String>> getAccessionAndPeptides(File file) throws FileNotFoundException, IOException {
        HashMap<String, ArrayList<String>> accession_and_sequence = new HashMap<String, ArrayList<String>>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "";
        while ((line = br.readLine()) != null) {
            String[] sp = line.split("\t");
            String pep = sp[0],
                    protein = sp[1];
            pep = pep.substring(2, pep.length() - 3);
            if (accession_and_sequence.containsKey(protein)) {
                accession_and_sequence.get(protein).add(pep);
            } else {
                ArrayList<String> peps = new ArrayList<String>();
                peps.add(pep);
                accession_and_sequence.put(protein, peps);
            }
        }
        return accession_and_sequence;
    }

}
