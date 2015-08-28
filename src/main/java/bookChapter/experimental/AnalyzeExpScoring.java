/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

import cal.binBased.BinMSnSpectrum;
import cal.methods.SimilarityMethods;
import cal.multithread.Calculate_Similarity;
import cal.multithread.SimilarityResult;
import config.ConfigHolder;
import cal.binBased.ConvertToBinMSnSpectrum;
import preprocess.filter.precursor.RemovePrecursorRelatedPeaks;
import preprocess.filter.precursor.RemoveWindowAround;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class is used to
 *
 * @author Sule
 */
public class AnalyzeExpScoring {

    static SpectrumFactory fct = SpectrumFactory.getInstance();
    static Logger LOGGER = Logger.getLogger(ConfigHolder.class);

    /**
     * This method convert an MSnSpectrum into a BinMSnSpectrum with also
     * applying a given preprocessing settings First it removes any precursor
     * related peaks if precursor_peak_removal is set either 1 or 2..
     *
     * @param ms - MSnSpectrum
     * @param charge_situation -0:all 1:given charge 2:if higher than 4 consider
     * all together
     * @param is_precursor_peak_removal - remove or keep peaks derived from
     * precursor
     * @param fragment_tolerance - fragment tolerance (bin size
     * =2*fragment_tolerance)
     * @param convertToBinMSnSpectrumObj
     * @param noiseFiltering
     * @param transformation_type
     * @param isNFTR - true: NF then TR, false TR then NF
     * @param intensities_sum_or_mean_or_median
     * @param charge
     * @return
     * @throws MzMLUnmarshallerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NumberFormatException
     */
    private static BinMSnSpectrum constructBinMSnSpectrum(MSnSpectrum ms,
            int precursor_peak_removal,
            double fragment_tolerance,
            ConvertToBinMSnSpectrum convertToBinMSnSpectrumObj,
            boolean isNFTR)
            throws MzMLUnmarshallerException, IOException, ClassNotFoundException, NumberFormatException {
        BinMSnSpectrum binMSnSpectrum = null;
        if (precursor_peak_removal == 1) {
            RemoveWindowAround p = new RemoveWindowAround(ms, fragment_tolerance);
            p.removePrecursor();
        } else if (precursor_peak_removal == 2) {
            RemovePrecursorRelatedPeaks p = new RemovePrecursorRelatedPeaks(ms, fragment_tolerance);
            p.removePrecursor();
        }

        if (!ms.getPeakMap().isEmpty()) {
            binMSnSpectrum = convertToBinMSnSpectrumObj.convertToBinMSnSpectrum(ms, isNFTR);
        }
        return binMSnSpectrum;
    }

    /**
     * This method constructs BinMSnSpectra from all spectra on mgf file then
     * puts all of them into an arrayList
     *
     * @param mgf_file
     * @param min_mz start mz for binning
     * @param max_mz end mz for binning
     * @param fragment_tolerance
     * @param noiseFiltering
     * @param transformation
     * @param intensities_sum_or_mean_or_median
     * @param topN
     * @param percentage
     * @param is_precursor_peak_removal
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws MzMLUnmarshallerException
     */
    private static ArrayList<BinMSnSpectrum> convert_all_MSnSpectra_to_BinMSnSpectra(File mgf_file, double min_mz, double max_mz, double fragment_tolerance,
            int noiseFiltering, int transformation, int topN, int precursor_peak_removal, int charge, boolean isNFTR)
            throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {

        int weighting = ConfigHolder.getInstance().getInt("sum_mean_median"),
                percent = ConfigHolder.getInstance().getInt("percent");
        ConvertToBinMSnSpectrum convertToBinMSnSpectrumObj = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percent, fragment_tolerance, noiseFiltering, transformation, weighting);
        ArrayList<BinMSnSpectrum> binspectra = new ArrayList<BinMSnSpectrum>();

        if (mgf_file.getName().endsWith(".mgf")) {
            fct.clearFactory();
            fct.addSpectra(mgf_file, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(mgf_file.getName())) {
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf_file.getName(), title);
                // do this preprocessing here...
                if (charge == 0) {
                    BinMSnSpectrum binMSnSpectrum = constructBinMSnSpectrum(ms, precursor_peak_removal, fragment_tolerance, convertToBinMSnSpectrumObj, isNFTR);
                    if (binMSnSpectrum != null) {
                        binspectra.add(binMSnSpectrum);
                    }
                } else if (charge == ms.getPrecursor().getPossibleCharges().get(0).value) {
                    BinMSnSpectrum binMSnSpectrum = constructBinMSnSpectrum(ms, precursor_peak_removal, fragment_tolerance, convertToBinMSnSpectrumObj, isNFTR);
                    if (binMSnSpectrum != null) {
                        binspectra.add(binMSnSpectrum);
                    }
                }
            }
        }
        return binspectra;
    }

    /**
     * This method calculates similarities bin-based between yeast_human spectra
     * on the first data set against all yeast spectra on the second data set
     *
     * @param min_mz
     * @param max_mz
     * @param topN
     * @param percentage
     * @param is_precursor_peak_removal
     * @param fragment_tolerance
     * @param noiseFiltering
     * @param transformation
     * @param spectraToCompare spectrum library
     * @param bw
     * @param charge
     * @param charge_situation
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws NumberFormatException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    /**
     *
     * @param spectraToCompare - spectrum library, which spectra are searched
     * against
     * @param spectra - spectrum to be scored against
     * @param bw
     * @param charge - 0: no charge restriction, >0: must be the same precursor
     * charge
     * @param precursorMassWindow
     * @param fragmentTolerance
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws NumberFormatException
     * @throws InterruptedException
     */
    private static void calculate_BinBasedScores(
            ArrayList<BinMSnSpectrum> spectraToCompare,
            ArrayList<BinMSnSpectrum> spectra,
            BufferedWriter bw,
            int charge,
            double precursorMassWindow, double fragmentTolerance)
            throws IllegalArgumentException, ClassNotFoundException, IOException, MzMLUnmarshallerException, NumberFormatException, InterruptedException {
        ExecutorService excService = Executors.newFixedThreadPool(ConfigHolder.getInstance().getInt("thread.numbers"));
        List<Future<SimilarityResult>> futureList = new ArrayList<Future<SimilarityResult>>();
        int counting = 0;
        for (BinMSnSpectrum binSp : spectra) {
            int tmpMSCharge = binSp.getSpectrum().getPrecursor().getPossibleCharges().get(0).value;
            if (charge == 0 || tmpMSCharge == charge) {
                if (!binSp.getSpectrum().getPeakList().isEmpty() && !spectraToCompare.isEmpty()) {
                    Calculate_Similarity similarity = new Calculate_Similarity(binSp, spectraToCompare, fragmentTolerance, precursorMassWindow);
                    Future future = excService.submit(similarity);
                    futureList.add(future);
                }
            }
        }
        for (Future<SimilarityResult> future : futureList) {
            try {
                counting++;
                if (counting % 400 == 0) {
                    LOGGER.info("Spectra number" + counting);
                }
                SimilarityResult get = future.get();
                String tmp_charge = get.getSpectrumChargeAsString(),
                        tmp_Name = get.getSpectrumName();
                MSnSpectrum bestMatched = get.getBestSimilarSpec();
                double tmpPrecMZ = get.getSpectrumPrecursorMZ();
                double dot_product = get.getScores().get(SimilarityMethods.DOT_PRODUCT),
                        normalized_dot_product = get.getScores().get(SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD),
                        pearson = get.getScores().get(SimilarityMethods.PEARSONS_CORRELATION),
                        spearman = get.getScores().get(SimilarityMethods.SPEARMANS_CORRELATION),
                        mean = get.getScores().get(SimilarityMethods.MEAN_SQUARED_ERROR);
                if (dot_product == Double.MIN_VALUE) {
                    // Means that score has not been calculated!
                } else {
                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t" + bestMatched.getSpectrumTitle() + "\t");
                    bw.write(dot_product + "\t" + normalized_dot_product + "\t" + pearson + "\t" + spearman + "\t" + mean + "\n");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.error(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
                LOGGER.error(e);
            }
        }
    }

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.io.FileNotFoundException
     * @throws java.lang.ClassNotFoundException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException, InterruptedException, ExecutionException {
        String upsFileName = ConfigHolder.getInstance().getString("ups.file.name"),
                yeastUPSFileName = ConfigHolder.getInstance().getString("yeast.ups.file.name"),
                outputFolder = ConfigHolder.getInstance().getString("output.folder.name"),
                identifier = ConfigHolder.getInstance().getString("identifier");
        int transformation = ConfigHolder.getInstance().getInt("transformation"), // 0-Nothing 1- Log2 2-Square root
                noiseFiltering = ConfigHolder.getInstance().getInt("filtering"), // 0-Nothing 1- PrideAsap 2-TopN 3-Discard peaks less than 5% of precursor  
                topN = ConfigHolder.getInstance().getInt("topN"), // Top intense N peaks 
                precursorRemoval = ConfigHolder.getInstance().getInt("precursor.peak.removal.option"); //0-Nothing, 1-Windowbased, 2-Any peaks
        boolean is_charged_based = ConfigHolder.getInstance().getBoolean("is.charged.based"), // select spectra only within the same charge state 
                isNFTR = ConfigHolder.getInstance().getBoolean("nf.tr"); // T-first noise filtering then transformation, F-first transformation then noise filtering
        double min_mz = ConfigHolder.getInstance().getDouble("min.mz"), // To start binning
                max_mz = ConfigHolder.getInstance().getDouble("max.mz"), // To end binning
                fragment_tolerance = ConfigHolder.getInstance().getDouble("fragment.tolerance"), // A bin size if 2*0.5
                precursor_mz_window = ConfigHolder.getInstance().getDouble("precursor.mz.window"); // 0-No PM tolerance otherwise the exact mass difference
        /// SETTINGS//////////////////////////////////////////
        // prepare arguments to run a Game
        File ups_file = new File(upsFileName),
                yeastUPSfile = new File(yeastUPSFileName);
        // prepare a title on an output file
        String noiseFilteringInfo = "None",
                transformationInfo = "None",
                precursorPeakRemoval = "None",
                chargeBased = "None",
                precTolBased = "0",
                isNFTRInfo = "NFTR";
        if (!isNFTR) {
            isNFTRInfo = "TRNF";
        }
        if (precursorRemoval == 1) {
            precursorPeakRemoval = "windowBased";
        } else if (precursorRemoval == 2) {
            precursorPeakRemoval = "anyRelevantPeaks";
        }

        if (noiseFiltering == 1) {
            noiseFilteringInfo = "Pride";
        } else if (noiseFiltering == 2) {
            noiseFilteringInfo = "Top"+topN+"N";
        } else if (noiseFiltering==3){
            noiseFilteringInfo="LowPrec";
        }
        if (transformation == 1) {
            transformationInfo = "Log2";
        } else if (transformation == 2) {
            transformationInfo = "Sqrt";
        }
        if (is_charged_based) {
            chargeBased = "givenCharge";
        }
        if (precursor_mz_window > 0) {
            precTolBased = String.valueOf(precursor_mz_window);
        }

        String param = "BC" + "_NFTR_" + isNFTRInfo + "_NF_" + noiseFilteringInfo + "_TR_" + transformationInfo + "_PPR_" + precursorPeakRemoval + "_CHR_" + chargeBased + "_PRECTOL_" + precTolBased + "_binScores.txt",
                paramTitle = "_NFTR_" + isNFTRInfo + "_NF_" + noiseFiltering + "_TR_" + transformation + "_PPR_" + precursorPeakRemoval + "_CHR_" + is_charged_based + "_PRECTOL_" + precursor_mz_window;

        File output = new File(outputFolder + identifier + "_" + param);
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        // write a title
        bw.write("Spectrum_Title" + "\t" + "charge" + "\t" + "Precursor" + "\t" + "best_matched_spectrum_title" + "\t");         // write title for an output file
        bw.write("dot_score" + paramTitle + "\t" +"normalized_dot_score" + paramTitle + "\t" + "pearson" + paramTitle + "\t" + "spearman" + paramTitle + "\t" + "mean_squared_error" + paramTitle + "\n");

        /// RUNNING //////////////////////////////////////////
        int[] charges = {2, 3, 4, 5, 6}; // restricting to only charge state based
        LOGGER.info("Option charged based=" + is_charged_based);
        // Run against all - no restriction for binned based calculation
        if (!is_charged_based) {
            ArrayList<BinMSnSpectrum> upsBinMSnSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(ups_file, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, precursorRemoval, 0, isNFTR),
                    yeastUPSBinMSnSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(yeastUPSfile, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, precursorRemoval, 0, isNFTR);
            LOGGER.info("Size of upsBinMSnSpectra=" + upsBinMSnSpectra.size());
            LOGGER.info("Size of yeastUPSBinMSnSpectra=" + yeastUPSBinMSnSpectra.size());
            calculate_BinBasedScores(upsBinMSnSpectra, yeastUPSBinMSnSpectra, bw, 0, precursor_mz_window, fragment_tolerance);
            // Run only the same charge state
        } else if (is_charged_based) {
            for (int charge : charges) {
                ArrayList<BinMSnSpectrum> upsBinMSnSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(ups_file, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, precursorRemoval, charge, isNFTR),
                        yeastUPSBinMSnSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(yeastUPSfile, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, precursorRemoval, charge, isNFTR);
                LOGGER.info("Size of upsBinMSnSpectra=" + upsBinMSnSpectra.size());
                LOGGER.info("Size of yeastUPSBinMSnSpectra=" + yeastUPSBinMSnSpectra.size());
                calculate_BinBasedScores(upsBinMSnSpectra, yeastUPSBinMSnSpectra, bw, charge, precursor_mz_window, fragment_tolerance);
            }
        }
        bw.close();
        System.exit(0);
    }

}
