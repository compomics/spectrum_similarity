/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

import bookChapter.GetSpecAnDID;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 *
 * UPS-Run-output contains results from the comparison of the UPS-Matched
 * Spectra on the Yeast-UPS data set against UPS-matched spectra on the UPS data
 * set.
 *
 * NON-UPS-Run-output contains results from the comparison of the
 * NON-UPS-Matched Spectra on the Yeast-UPS data set against the UPS-matched
 * spectra on the UPS data set.
 *
 *
 * The first step:
 *
 * Read UPS-Run-output to check if the same peptide derived spectrum on the UPS
 * data set found
 *
 * Define same/different peptide - Write on Peptide Column
 *
 * Define same/different protein - Write on Protein Column
 *
 * Return #same_id_spectra
 *
 *
 * The second step:
 *
 * Read NON-UPS-Run-output to select randomly #same_id_spectra
 *
 * Check these randomly selected #same_id_spectra if they have the same peptide
 *
 * Define same/different peptide - Write on the PeptideColumn
 *
 * Define same/different peptide - Write on the Protein Column
 *
 *
 *
 * The third step: Write these checked data on the output
 *
 *
 *
 * @author Sule
 */
public class Similarity {

    private double pep = 0.05;
    private HashMap<String, Identifications> yeast_sigma_validated_spec_id = new HashMap<String, Identifications>(),
            sigma48_validated_spec_id = new HashMap<String, Identifications>();
    private ArrayList<Result> ups_run_output = new ArrayList<Result>(),
            non_ups_run_output = new ArrayList<Result>();

    private ArrayList<String> allSameIDs = new ArrayList<String>();
    private boolean areAllSameIDsReady = false;

    private File upsResult, // input
            nonUPSResult, // input2                
            output, // output
            yeast_ups_mascot,
            ups_mascot;

    public Similarity(File upsResult, File nonUPSResult, File output, File ups_mascot, File yeast_ups_mascot) throws IOException {
        this.upsResult = upsResult;
        this.nonUPSResult = nonUPSResult;
        this.output = output;
        this.ups_mascot = ups_mascot;
        this.yeast_ups_mascot = yeast_ups_mascot;
        sigma48_validated_spec_id = GetSpecAnDID.getSpecAndIDs(ups_mascot, pep);
        yeast_sigma_validated_spec_id = GetSpecAnDID.getSpecAndIDs(yeast_ups_mascot, pep);
        ups_run_output = prepareSimResults(upsResult);
        non_ups_run_output = prepareSimResults(nonUPSResult);
    }

    public void analyze() throws IOException {
        ArrayList<Result> spec_and_info_ups = check(ups_run_output, yeast_sigma_validated_spec_id, sigma48_validated_spec_id, true);
        // randomly select non-ups spectra
        ArrayList<Result> randomly_selected_nonups_run_output = new ArrayList<Result>(select_randomly(non_ups_run_output, spec_and_info_ups.size()));
        // now check these
        ArrayList<Result> spec_and_info_non_ups = check(randomly_selected_nonups_run_output, yeast_sigma_validated_spec_id, sigma48_validated_spec_id, false);
        // write them down
        writeOutput(output, spec_and_info_ups, spec_and_info_non_ups);
    }

    /**
     * It returns a list of results with scores bigger than given threshold
     *
     * @param threshold (>=)
     * @param scoreType 0-dot; 1-pearson; 2-spearman
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<Result> getResults(double threshold, int scoreType) throws FileNotFoundException, IOException {
        ArrayList<Result> res = new ArrayList<Result>();
        for (int i = 0; i < ups_run_output.size(); i++) {
            Result r = ups_run_output.get(i);
            boolean isSpecAIdentified = false,
                    isSpecBIdentified = false;
            String idSpecA = "",
                    idSpecB = "";
            double tmpScore = r.getDot_score();
            if (scoreType == 1) {
                tmpScore = r.getPearson();
            } else if (scoreType == 2) {
                tmpScore = r.getSpearman();
            }
            if (tmpScore >= threshold) {
                String specTitleA = r.getSpectrum_title_A(),
                        specTitleB = r.getSpectrum_title_B();
                if (yeast_sigma_validated_spec_id.containsKey(specTitleA)) {
                    // pep ide <=0.05% by default 
                    idSpecA = yeast_sigma_validated_spec_id.get(specTitleA).getPeptide();
                    isSpecAIdentified = true;
                }
                if (sigma48_validated_spec_id.containsKey(specTitleB)) {
                    // pep ide <=0.05% by default 
                    idSpecB = sigma48_validated_spec_id.get(specTitleB).getPeptide();
                    isSpecBIdentified = true;
                }
                res.add(r);
            }
            if (isSpecAIdentified && isSpecBIdentified) {
                r.setIsSpectrumAIdentified(true);
                r.setIsSpectrumBIdentified(true);
                if (idSpecA.equals(idSpecB)) {
                    r.setIsSame(true);
                }
            }
        }
        return res;
    }

    /**
     * It returns a list of results with scores bigger than given threshold
     *
     * @param input
     * @param output
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<Result> writeNewOutcome(File input, File output) throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("SpecA" + "\t" + "SpecB" + "\t" + "Identification" + "\t" + "dot_score" + "\t" + "spearman" + "\t" + "pearson" + "\t" + "MSE" + "\t" + "sameIdentification" + "\t" + "IsIdentified" + "\n");
        ArrayList<Result> res = new ArrayList<Result>();
        for (int i = 0; i < ups_run_output.size(); i++) {
            String sameInfo = "unknown",
                    idInfo = "notIdentified";
            Result r = ups_run_output.get(i);
            boolean isSpecAIdentified = false,
                    isSpecBIdentified = false;
            String idSpecA = "",
                    idSpecB = "";
            String specTitleA = r.getSpectrum_title_A(),
                    specTitleB = r.getSpectrum_title_B(),
                    identification = "";
            if (yeast_sigma_validated_spec_id.containsKey(specTitleA)) {
                // pep ide <=0.05% by default 
                idSpecA = yeast_sigma_validated_spec_id.get(specTitleA).getPeptide();
                isSpecAIdentified = true;
            }
            if (sigma48_validated_spec_id.containsKey(specTitleB)) {
                // pep ide <=0.05% by default 
                idSpecB = sigma48_validated_spec_id.get(specTitleB).getPeptide();
                isSpecBIdentified = true;
                identification = idSpecB;
            }
            res.add(r);
            if (isSpecAIdentified && isSpecBIdentified) {
                r.setIsSpectrumAIdentified(true);
                r.setIsSpectrumBIdentified(true);
                idInfo = "identified";
                if (idSpecA.equals(idSpecB)) {
                    r.setIsSame(true);
                    sameInfo = "same";
                } else {
                    sameInfo = "different";
                }
            }
            bw.write(r.getSpectrum_title_A() + "\t" + r.getSpectrum_title_B() + "\t" + identification + "\t" + r.getDot_score() + "\t" + r.getSpearman() + "\t" + r.getPearson() + "\t" + r.getMse() + "\t" + sameInfo + "\t" + idInfo + "\n");
        }
        bw.close();
        return res;
    }

    public ArrayList<String> getAllSameIDs() {
        if (!areAllSameIDsReady) {
            for (String spec : yeast_sigma_validated_spec_id.keySet()) {
                Identifications identification = yeast_sigma_validated_spec_id.get(spec);
                for (String nextSpec : sigma48_validated_spec_id.keySet()) {
                    Identifications nextIdentification = sigma48_validated_spec_id.get(nextSpec);
                    if (identification.getPeptide().equals(nextIdentification.getPeptide())
                            && identification.getCharge() == nextIdentification.getCharge()) {
                        allSameIDs.add(spec);
                    }
                }
            }
            areAllSameIDsReady = true;
        }
        return allSameIDs;
    }

    /**
     * It returns a list of results with scores bigger than given threshold
     *
     * @param upperThreshold smaller
     * @param lowerThreshold equal or bigger
     * @param scoreType 0-dot; 1-pearson; 2-spearman
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public ArrayList<Result> getResults(double upperThreshold, double lowerThreshold, int scoreType) throws FileNotFoundException, IOException {
        ArrayList<Result> res = new ArrayList<Result>();
        for (int i = 0; i < ups_run_output.size(); i++) {
            Result r = ups_run_output.get(i);
            boolean isSpecAIdentified = false,
                    isSpecBIdentified = false;
            String idSpecA = "",
                    idSpecB = "";
            double tmpScore = r.getDot_score();
            if (scoreType == 1) {
                tmpScore = r.getPearson();
            } else if (scoreType == 2) {
                tmpScore = r.getSpearman();
            }
            if (tmpScore >= lowerThreshold && tmpScore < upperThreshold) {
                String specTitleA = r.getSpectrum_title_A(),
                        specTitleB = r.getSpectrum_title_B();
                if (yeast_sigma_validated_spec_id.containsKey(specTitleA)) {
                    // pep ide <=0.05% by default 
                    idSpecA = yeast_sigma_validated_spec_id.get(specTitleA).getPeptide();
                    isSpecAIdentified = true;
                }
                if (sigma48_validated_spec_id.containsKey(specTitleB)) {
                    // pep ide <=0.05% by default 
                    idSpecB = sigma48_validated_spec_id.get(specTitleB).getPeptide();
                    isSpecBIdentified = true;
                }
                res.add(r);
            }
            if (isSpecAIdentified && isSpecBIdentified) {
                r.setIsSpectrumAIdentified(true);
                r.setIsSpectrumBIdentified(true);
                if (idSpecA.equals(idSpecB)) {
                    r.setIsSame(true);
                }
            }
        }
        return res;
    }

    public ArrayList<Result> prepareSimResults(File similarityResult) throws FileNotFoundException, IOException {
        ArrayList<Result> res = new ArrayList<Result>();
        BufferedReader br = new BufferedReader(new FileReader(similarityResult));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Spectrum")) {
                String[] split = line.split("\t");
                String specTitleA = split[0],
                        specAChargeInfo = split[1],
                        specAPrecMS = split[2],
                        specTitleB = split[3];
                double dot_score = Double.parseDouble(split[4]),
                        normalized_dot_score = Double.parseDouble(split[5]),
                        pearson = Double.parseDouble(split[6]),
                        spearman = Double.parseDouble(split[7]),
                        mse = Double.parseDouble(split[8]);
                Result r = new Result(specTitleA, specTitleB, "", "", specAChargeInfo, specAPrecMS, dot_score, normalized_dot_score, pearson, spearman, mse);
                res.add(r);
            }
        }
        return res;
    }

    public String info(ArrayList<Result> res) {
        int ids = 0,
                alls = 0,
                same = 0;
        for (Result r : res) {
            alls++;
            if (r.isIsSpectrumAIdentified() && r.isIsSpectrumBIdentified()) {
                ids++;
            }
            if (r.isIsSame()) {
                same++;
            }
        }
        return alls + "\t" + ids + "\t" + same;
    }

    private ArrayList<Result> check(ArrayList<Result> ups_run_output,
            HashMap<String, Identifications> yeast_sigma_validated_spec_id,
            HashMap<String, Identifications> sigma48_validated_spec_id,
            boolean doesCollectSamePeptide) {

        ArrayList<Result> result = new ArrayList<Result>();
        for (Result r : ups_run_output) {
            boolean isSamePeptide = false;
            Identifications yeast_ups_one = yeast_sigma_validated_spec_id.get(r.getSpectrum_title_A()),
                    ups_one = sigma48_validated_spec_id.get(r.getSpectrum_title_B());
            String yeast_ups_one_peptide = yeast_ups_one.getPeptide(),
                    yeast_ups_one_protein = yeast_ups_one.getProtein(),
                    
                    ups_one_peptide = ups_one.getPeptide(),
                    up_one_protein = ups_one.getProtein();
            
            // set peptide identification to write
            r.setPeptideA(yeast_ups_one.getPeptide());
            r.setPeptideB(ups_one.getPeptide());

            if (yeast_ups_one_peptide.equals(ups_one_peptide)) {
                r.setPeptideInfo("samePeptide");
                isSamePeptide = true;
            }
            if (yeast_ups_one_protein.equals(up_one_protein)) {
                r.setProteinInfo("sameProtein");
            }
            // either collecting the same peptide 
            if (isSamePeptide && doesCollectSamePeptide) {
                result.add(r);
            }
            // or different peptide
            if (!isSamePeptide && !doesCollectSamePeptide) {
                result.add(r);
            }

        }
        return result;
    }

    /**
     * Randomly select #num of scored spectra on non_ups_run_output list
     *
     * @param non_ups_run_output
     * @param size
     * @return
     */
    private HashSet<Result> select_randomly(ArrayList<Result> non_ups_run_output, int size) {
        // generate one Random Integer value
      
        HashSet<Result> randomly_selected = new HashSet<Result>();
        while (randomly_selected.size() < size) {
            int random = new Random().nextInt(non_ups_run_output.size());
            // randomly select it
            Result r = non_ups_run_output.get(random);
            randomly_selected.add(r);
        }
        return randomly_selected;
    }

    private void writeOutput(File output, ArrayList<Result> spec_and_info_ups, ArrayList<Result> spec_and_info_non_ups) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("Spectrum_title_A" + "\t"
                + "Spectrum_title_B" + "\t"
                + "PeptideInfo" + "\t"
                + "ProteinInfo" + "\t"
                + "SubDataset" + "\t"
                + "Peptide_A" + "\t"
                + "Peptide_B" + "\t"
                + "Dot_score" + "\t"
                +"Normalized_dot_product"+"\t"
                + "Pearson" + "\t"
                + "Spearman" + "\t"
                + "MSE" + "\n");
        for (Result r : spec_and_info_ups) {
            bw.write(r.getSpectrum_title_A() + "\t"
                    + r.getSpectrum_title_B() + "\t"
                    + r.getPeptideInfo() + "\t"
                    + r.getProteinInfo() + "\t"
                    + "UPS-Matched" + "\t"
                    + r.getPeptideA() + "\t"
                    + r.getPeptideB() + "\t"
                    + r.getDot_score() + "\t"
                    +r.getNormalized_dot_score()+"\t"
                    + r.getPearson() + "\t"
                    + r.getSpearman() + "\t"
                    + r.getMse() + "\n");
        }
        for (Result r : spec_and_info_non_ups) {
            bw.write(r.getSpectrum_title_A() + "\t"
                    + r.getSpectrum_title_B() + "\t"
                    + r.getPeptideInfo() + "\t"
                    + r.getProteinInfo() + "\t"
                    + "NonUPS-Matched" + "\t"
                    + r.getPeptideA() + "\t"
                    + r.getPeptideB() + "\t"
                    + r.getDot_score() + "\t"
                     +r.getNormalized_dot_score()+"\t"
                    + r.getPearson() + "\t"
                    + r.getSpearman() + "\t"
                    + r.getMse() + "\n");
        }
        bw.close();
    }

}
