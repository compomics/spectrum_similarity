/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.theoretical;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import config.ConfigHolder;
import com.compomics.util.protein.Protein;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import start.CalculateMS1Err;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class runs SEQUEST-like and Andromeda-like scoring for given spectra
 * (within a selected folder) and in silico digested protein database The
 * settings are at BookChapte.properties
 *
 * @author Sule
 */
public class AnalyzeTheoreticalMSMSCalculation {

    /**
    * 
    * @param args
    * @throws IOException
    * @throws FileNotFoundException
    * @throws ClassNotFoundException
    * @throws IOException
    * @throws InterruptedException
    * @throws MzMLUnmarshallerException 
    */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, InterruptedException, MzMLUnmarshallerException {
        Logger l = Logger.getLogger("AnalyzeTheoreticalMSMSCalculation");
        Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm:ss.SSS a");
        String now = formatter.format(date);
        l.info("Calculation starts at " + now);
        double precursorTolerance = ConfigHolder.getInstance().getDouble("precursor.tolerance"),
                fragmentTolerance = ConfigHolder.getInstance().getDouble("fragment.tolerance");
        String databaseName = ConfigHolder.getInstance().getString("database.name"),
                spectraName = ConfigHolder.getInstance().getString("spectra.name"),
                output = ConfigHolder.getInstance().getString("output");
        int correctionFactor = ConfigHolder.getInstance().getInt("correctionFactor");
        boolean theoFromAllCharges = ConfigHolder.getInstance().getBoolean("hasAllPossCharge");
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write("SpectrumTitle" + "\t"
                + "PrecursorMZ" + "\t" + "PrecursorCharge" + "\t"
                + "Observed Mass (M+H)" + "\t"
                + "AndromedaLikeScore" + "\t" + "SequestLikeScore" + "\t"
                + "PeptideByAndromedaLikeScore" + "\t" + "PeptideBySequestLikeScore" + "\t"
                + "LevenshteinDistance" + "\t" + "TotalScoredPeps" + "\t"
                + "isCorrectMatchByAndromedaLike" + "\t" + "isCorrectMatchBySequestLikeScore" + "\n");

        // first load all sequences into the memory 
        HashSet<DBEntry> dbEntries = getDBEntries(databaseName);
        // for every spectrum-calculate both score...
        // now convert to binExperimental spectrum
        int num = 0;
        SpectrumFactory fct = SpectrumFactory.getInstance();
        num = 0;
        File f = new File(spectraName);
        if (spectraName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                num++;
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);
                // here calculate all except this is an empty spectrum...
                if (ms.getPeakList().size() > 2) {
                    // to check a spectrum with negative values..
                    String text = result(ms, precursorTolerance, dbEntries, fragmentTolerance, correctionFactor, theoFromAllCharges);
                    if (!text.isEmpty()) {
                        bw.write(text);
                    }
                }
                if (num % 500 == 0) {
                    l.info("Running " + num + " spectra." + Calendar.getInstance().getTime());
                }
            }
        }
        l.info("Program finished at " + Calendar.getInstance().getTime());

        bw.close();
    }

    private static String result(MSnSpectrum msms, double precursorTolerance, HashSet<DBEntry> peptideAndMass, double fragmentTolerance, int correctionFactor, boolean hasAllPossCharge) throws IllegalArgumentException, IOException, MzMLUnmarshallerException {
        String res = "";
        HashSet<String> texts = new HashSet<String>();
        HashMap<Peptide, Boolean> allSelectedPeps = getSelectedTheoPeps(msms, precursorTolerance, peptideAndMass); // select peptides within a given precursor tolerance
        int scoredPeps = allSelectedPeps.size();
        ArrayList<Identify> sequestResults = new ArrayList<Identify>(),
                andromedaResults = new ArrayList<Identify>();
        // for every peptide... calculate each score...
        for (Peptide selectedPep : allSelectedPeps.keySet()) {
            Identify toCalculateSequest = new Identify(msms, selectedPep, fragmentTolerance, true, allSelectedPeps.get(selectedPep), scoredPeps, correctionFactor, hasAllPossCharge),
                    toCalculateAndromeda = new Identify(msms, selectedPep, fragmentTolerance, false, allSelectedPeps.get(selectedPep), scoredPeps, correctionFactor, hasAllPossCharge);
            sequestResults.add(toCalculateSequest);
            andromedaResults.add(toCalculateAndromeda);
        }
        if (!sequestResults.isEmpty()) {
            HashSet<Identify> theBestSEQUESTResults = getBestResult(sequestResults),
                    theBestAndromedaResults = getBestResult(andromedaResults);
            texts = printInfo(theBestAndromedaResults, theBestSEQUESTResults);
        }
        for (String t : texts) {
            res += t;
        }
        return res;
    }

    /**
     * This method load all sequences in a memory
     *
     * @param databaseName
     * @return
     */
    private static HashSet<DBEntry> getDBEntries(String databaseName) throws IOException {
        HashSet<DBEntry> dbEntries = new HashSet<DBEntry>();
        DBLoader loader = DBLoaderLoader.loadDB(new File(databaseName));
        Protein protein = null;
        // get a crossLinkerName object        
        while ((protein = loader.nextProtein()) != null) {
            String sequence = protein.getSequence().getSequence();
            String descrp = protein.getHeader().getDescription(),
                    acc = protein.getHeader().getAccession();
            Peptide tmpPep = new Peptide(sequence, new ArrayList<ModificationMatch>());
            double tmpPepMass = tmpPep.getMass();
            DBEntry dbEntry = new DBEntry(tmpPep, descrp, acc, tmpPepMass);
            dbEntries.add(dbEntry);
        }
        return dbEntries;
    }

    private static HashMap<Peptide, Boolean> getSelectedTheoPeps(MSnSpectrum msms, double precursorTolerance, HashSet<DBEntry> dbEntries) throws IOException, IllegalArgumentException {
//        ArrayList<Peptide> selected_UPS = new ArrayList<Peptide>(),
//                selected_PFU = new ArrayList<Peptide>();
        // select peptides to be compared...
        HashMap<Peptide, Boolean> allTheoPeps = new HashMap< Peptide, Boolean>();
        for (DBEntry dbEntry : dbEntries) {
            Peptide p = dbEntry.getPeptide();
            double peptideMass = dbEntry.getPeptideMass();
            String acc = dbEntry.getProteinAccession(),
                    descrp = dbEntry.getProteinDescription();
            double tmpMS1Tolerance = CalculateMS1Err.getMS1Err(true, peptideMass, msms.getPrecursor().getMass(msms.getPrecursor().getPossibleCharges().get(0).value));
            boolean isCorrect = false;
            if (tmpMS1Tolerance <= precursorTolerance && !descrp.contains("contaminant") && acc.contains("ups")) {
                // correct hit
                isCorrect = true;
                allTheoPeps.put(p, isCorrect);
//                selected_UPS.add(p);
            } else if (tmpMS1Tolerance <= precursorTolerance && !descrp.contains("contaminant")) {
                isCorrect = false;
                allTheoPeps.put(p, isCorrect);
//                selected_PFU.add(p);
            }
        }
        return allTheoPeps;
    }

    private static HashSet<Identify> getBestResult(ArrayList<Identify> results) {
        Collections.sort(results, Identify.ScoreDESC);
        double bestScore = results.get(0).getScore();
        String pep = results.get(0).getPeptide().getSequence();
        HashSet<Identify> selectedTops = new HashSet<Identify>();
        selectedTops.add(results.get(0));
        for (int i = 1; i < results.size(); i++) {
            Identify ind = results.get(i);
            double tmpScore = ind.getScore();
            String tmpPep = ind.getPeptide().getSequence();
            // making sure that non-redundant peptide would be only found
            if (tmpScore == bestScore && !pep.equals(tmpPep)) {
                bestScore = tmpScore;
                selectedTops.add(ind);
            }
        }
        return selectedTops;
    }

    private static HashSet<String> printInfo(HashSet<Identify> theBestAndromedaResults, HashSet<Identify> theBestSEQUESTResults) {
        ///    bw.write("Score" + "\t" + "LevenshteinDistance" + "\t" + "Sequence" + "\t" + "Charge" + "\t" + "Correct" + "\t" + "Spec" + "\n");
        String result = "";
        HashSet<String> results = new HashSet<String>();
        for (Identify andromeda : theBestAndromedaResults) {
            for (Identify sequest : theBestSEQUESTResults) {
                double observedMass = CalculateObservedMass.calculateMass(andromeda.getSpectrum());
                int levenshteinDistance = StringUtils.getLevenshteinDistance(andromeda.getPeptide().getSequence(), sequest.getPeptide().getSequence());
                result = andromeda.getSpectrum().getSpectrumTitle() + "\t"
                        + andromeda.getSpectrum().getPrecursor().getMz() + "\t" + andromeda.getSpectrum().getPrecursor().getPossibleChargesAsString() + "\t"
                        + observedMass + "\t"
                        + andromeda.getScore() + "\t" + sequest.getScore() + "\t"
                        + andromeda.getPeptide().getSequence() + "\t" + sequest.getPeptide().getSequence() + "\t"
                        + levenshteinDistance + "\t" + sequest.getTotalScoredPeps() + "\t"
                        + andromeda.isIsCorrectMatch() + "\t" + sequest.isIsCorrectMatch() + "\n";
                results.add(result);
            }
        }
        return results;
    }

}
