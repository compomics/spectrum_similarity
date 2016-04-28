/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import cal.binBased.BinMSnSpectrum;
import cal.binBased.ConvertToBinMSnSpectrum;
import cal.methods.SimilarityMethods;
import cal.multithread.Calculate_Similarity;
import cal.multithread.SimilarityResult;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import config.ConfigHolder;
import gui.MainController;
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
import static main.ScorePipeline.LOGGER;
import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import preprocess.filter.noise.implementation.DiscardLowIntensePeaks;
import preprocess.filter.noise.implementation.NoiseFilteringPrideAsap;
import preprocess.filter.noise.implementation.TopNFiltering;
import preprocess.filter.precursor.RemovePrecursorRelatedPeaks;
import preprocess.transformation.implementation.TransformIntensitiesImp;
import preprocess.transformation.methods.Transformations;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class is used to compared two data sets on comparative analysis.
 *
 * @author Sule
 */
public class ScorePipeline {

    static SpectrumFactory fct = SpectrumFactory.getInstance();
    static Logger LOGGER = Logger.getLogger(ScorePipeline.class);

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
        run(false);
    }

    /**
     * Run the spectrum similarity pipeline.
     *
     * @param isGUI true if the GUI is asked, false: a standalone version
     *
     * @throws IOException in case of an I/O related problem
     * @throws FileNotFoundException in case of file opening related problem
     * @throws ClassNotFoundException in case of a class loading by name problem
     * @throws MzMLUnmarshallerException in case of an MzML parsing related
     * problem
     * @throws IllegalArgumentException in case of an in appropriate argument
     * was passed
     * @throws NumberFormatException in case of a numeric conversion related
     * problem
     * @throws InterruptedException in case of an inactive thread interruption
     * problem
     */
    public static void run(boolean isGUI) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException, IllegalArgumentException, NumberFormatException, InterruptedException {
        //send an event
        sendAnalyticsEvent();
        if (isGUI) {
            LOGGER = Logger.getLogger(MainController.class);
        }
        String thydFolder = ConfigHolder.getInstance().getString("spectra.folder"),
                tsolFolder = ConfigHolder.getInstance().getString("spectra.to.compare.folder"),
                outputFolder = ConfigHolder.getInstance().getString("output.folder");
        int transformation = ConfigHolder.getInstance().getInt("transformation"), // 0-Nothing 1- Log2 2-Square root
                noiseFiltering = ConfigHolder.getInstance().getInt("noise.filtering"), // 0-Nothing 1- PrideAsap 2-TopN 3-Discard peaks less than 5% of precursor
                topN = ConfigHolder.getInstance().getInt("topN"), // Top intense N peaks
                msRobinCalculationOption = 1, // Calculation is  #0: -10*Log10(Pro)*SQRT(IP)) #1: -10*Log10(Pro)*IP #2: -Log10(Pro*IP) #3: (1-Pro)*IP
                calculationOptionIntensityMSRobin = 1; // IP is calculated as 0:Summing up intensity-ratios #1:Multiply intensity-ratios #2:Math.pow(10, (1-IP))
        boolean is_charged_based = ConfigHolder.getInstance().getBoolean("is.charged.based"), // F- All against all T-only the same charge state 2-bigger than 4, check against all
                is_hq_data = true,//removed is.hq = true from MS2Similarity.properties
                is_precursor_peak_removed = ConfigHolder.getInstance().getBoolean("precursor.peak.removal"),
                doesCalculateOnly5 = ConfigHolder.getInstance().getBoolean("calculate.only5"),
                isNFTR = ConfigHolder.getInstance().getBoolean("isNFTR");
        double min_mz = 100, // To start binning (removed min.mz from MS2Similarity.properties because only for cumulative binomial scoring function)
                max_mz = 3500, // To end binning (removed max.mz from MS2Similarity.properties because only for cumulative binomial scoring function)
                fragment_tolerance = ConfigHolder.getInstance().getDouble("fragment.tolerance"), // A bin size if 2*0.5
                percentage = ConfigHolder.getInstance().getDouble("percent"),
                precTol = ConfigHolder.getInstance().getDouble("precursor.tolerance"); // 0-No PM tolerance otherwise the exact mass difference
        int sliceIndex = ConfigHolder.getInstance().getInt("slice.index");
        // Select a scoring function name as msrobin (this is pROBility INtensity weighted scoring function, dot, spearman, and pearson (all lower case))
        String scoreType = "msrobin"; // Avaliable scoring functions: msrobin/spearman/pearson/dot

        /// SETTINGS//////////////////////////////////////////
        File thydatigenas_directory = new File(thydFolder),
                tsoliums_directory = new File(tsolFolder);

        // prepare a title on an output file
        String noiseFilteringInfo = "None",
                transformationInfo = "None",
                precursorPeakRemoval = "On",
                chargeBased = "None",
                precTolBased = "0";
        if (!is_precursor_peak_removed) {
            precursorPeakRemoval = "None";
        }
        if (noiseFiltering == 1) {
            noiseFilteringInfo = "Pride";
        } else if (noiseFiltering == 2) {
            noiseFilteringInfo = "TopN";
        } else if (noiseFiltering == 3) {
            noiseFilteringInfo = "DiscardLowAbundance";
        }
        if (transformation == 1) {
            transformationInfo = "Log2";
        } else if (transformation == 2) {
            transformationInfo = "Sqrt";
        }
        if (is_charged_based) {
            chargeBased = "givenCharge";
        }
        if (precTol > 0) {
            precTolBased = String.valueOf(precTol);
        }

        String param = "NF_" + noiseFilteringInfo + "_TR_" + transformationInfo + "_PPR_" + precursorPeakRemoval + "_CHR_" + chargeBased + "_PRECTOL_" + precTolBased,
                paramTitle = "_NF_" + noiseFiltering + "_TR_" + transformation + "_PPR_" + precursorPeakRemoval + "_CHR_" + is_charged_based + "_PRECTOL_" + precTol;

        if (is_hq_data) {
            paramTitle = "_HQ_" + paramTitle;
            param = "HQ_" + param;
        }
        switch (scoreType) {
            case "msrobin":
                param += "_MSRobin";
                break;
            case "dot":
                param += "_Dot";
                break;
            case "spearman":
                param += "_Spearman";
                break;
            case "pearson":
                param += "_Pearson";
                break;
            default:
                LOGGER.info("Selected scoring name is not avaliable. Available functions are msrobin, do, spearman and pearson");
                System.exit(0);
        }
        param += ".txt";

        File output = new File(outputFolder + File.separator + param);
        // write a title on an output
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(output))) {
            // write a title on an output
            bw.write("Spectrum Title" + "\t" + "Charge" + "\t" + "PrecursorMZ" + "\t" + "Spectrum_Title_Calculated_Against" + "\t" + scoreType + paramTitle + "\n");

            /// RUNNING //////////////////////////////////////////
            int[] charges = {2, 3, 4, 5}; // restricting to only charge state based
            LOGGER.info("Run is ready to start with " + param + " for " + scoreType);
            LOGGER.info("Only calculate +/-2 slices up and down: " + doesCalculateOnly5);
            //Get indices for each spectrum..

            for (int index = 0; index < thydatigenas_directory.listFiles().length; index++) {
                for (File thyd : thydatigenas_directory.listFiles()) {
                    for (File tsol : tsoliums_directory.listFiles()) {
                        int tsolIndex = Integer.parseInt(tsol.getName().split("_")[sliceIndex].substring(0, tsol.getName().split("_")[sliceIndex].indexOf(".mgf"))),
                                thydIndex = Integer.parseInt(thyd.getName().split("_")[sliceIndex].substring(0, thyd.getName().split("_")[sliceIndex].indexOf(".mgf")));
                        if (doesCalculateOnly5) {
                            // Now select an mgf files from the same slices..
                            if (index - 2 <= thydIndex && thydIndex <= index + 2 && tsolIndex == index) {
                                LOGGER.info("slice number (spectra.folder and spectra.folder.to.compare)=" + thydIndex + "\t" + tsolIndex);
                                LOGGER.info("a name of an mgf from the spectra.folder=" + thyd.getName());
                                LOGGER.info("a name of an mgf from the spectra.folder.to.compare=" + tsol.getName());
                                if (!scoreType.equals("msrobin")) {
                                    // Run against all - no restriction for binned based calculation
                                    if (!is_charged_based) {
                                        ArrayList<BinMSnSpectrum> thydBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(thyd, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, 0, isNFTR),
                                                tsolBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(tsol, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, 0, isNFTR);
                                        LOGGER.info("Size of BinSpectra at spectra.folder=" + thydBinSpectra.size());
                                        LOGGER.info("Size of BinSpectra at spectra.to.compare.folder=" + tsolBinSpectra.size());
                                        calculate_BinBasedScores(thydBinSpectra, tsolBinSpectra, bw, 0, precTol, fragment_tolerance, scoreType);
                                        // Run only the same charge state
                                    } else if (is_charged_based) {
                                        for (int charge : charges) {
                                            ArrayList<BinMSnSpectrum> thydBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(thyd, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, charge, isNFTR),
                                                    tsolBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(tsol, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, charge, isNFTR);
                                            LOGGER.info("Size of BinSpectra at spectra.folder=" + thydBinSpectra.size());
                                            LOGGER.info("Size of BinSpectra at spectra.to.compare.folder=" + tsolBinSpectra.size());
                                            calculate_BinBasedScores(thydBinSpectra, tsolBinSpectra, bw, charge, precTol, fragment_tolerance, scoreType);
                                        }
                                    }
                                } else if (!is_charged_based) {
                                    // Run against all for MSRobin
                                    ArrayList<MSnSpectrum> thydMSnSpectra = prepareData(thyd, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, 0, fragment_tolerance),
                                            tsolMSnSpectra = prepareData(tsol, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, 0, fragment_tolerance);
                                    LOGGER.info("Size of MSnSpectra at spectra.folder=" + thydMSnSpectra.size());
                                    LOGGER.info("Size of MSnSpectra at spectra.to.compare.folder=" + tsolMSnSpectra.size());

                                    calculate_MSRobins(thydMSnSpectra, tsolMSnSpectra, bw, fragment_tolerance, precTol, calculationOptionIntensityMSRobin, msRobinCalculationOption);
                                } else if (is_charged_based) {
                                    for (int charge : charges) {
                                        ArrayList<MSnSpectrum> thydMSnSpectra = prepareData(thyd, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, charge, fragment_tolerance),
                                                tsolMSnSpectra = prepareData(tsol, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, charge, fragment_tolerance);
                                        LOGGER.info("Charge=" + charge);
                                        LOGGER.info("Size of MSnSpectra at spectra.folder=" + thydMSnSpectra.size());
                                        LOGGER.info("Size of MSnSpectra at spectra.to.compare.folder=" + tsolMSnSpectra.size());
                                        calculate_MSRobins(thydMSnSpectra, tsolMSnSpectra, bw, fragment_tolerance, precTol, calculationOptionIntensityMSRobin, msRobinCalculationOption);
                                    }
                                }
                            }
                            // Calculate all against all..
                        } else {
                            LOGGER.info("slice number (spectra.folder and spectra.to.compare.folder)=" + thydIndex + "\t" + tsolIndex);
                            LOGGER.info("a name of an mgf from the spectra.folder=" + thyd.getName());
                            LOGGER.info("a name of an mgf from the spectra.to.compare.folder=" + tsol.getName());
                            if (!scoreType.equals("msrobin")) {
                                // Run against all - no restriction for binned based calculation
                                if (!is_charged_based) {
                                    ArrayList<BinMSnSpectrum> thydBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(thyd, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, 0, isNFTR),
                                            tsolBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(tsol, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, 0, isNFTR);
                                    LOGGER.info("Size of BinMSnSpectra spectra at spectra.folder=" + thydBinSpectra.size());
                                    LOGGER.info("Size of BinMSnSpectra spectra at spectra.to.compare.folder=" + tsolBinSpectra.size());
                                    calculate_BinBasedScores(thydBinSpectra, tsolBinSpectra, bw, 0, precTol, fragment_tolerance, scoreType);
                                    // Run only the same charge state
                                } else if (is_charged_based) {
                                    for (int charge : charges) {
                                        ArrayList<BinMSnSpectrum> thydBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(thyd, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, charge, isNFTR),
                                                tsolBinSpectra = convert_all_MSnSpectra_to_BinMSnSpectra(tsol, min_mz, max_mz, fragment_tolerance, noiseFiltering, transformation, topN, is_precursor_peak_removed, charge, isNFTR);
                                        LOGGER.info("Size of BinMSnSpectra spectra at spectra.folder=" + thydBinSpectra.size());
                                        LOGGER.info("Size of BinMSnSpectra spectra at spectra.to.compare.folder=" + tsolBinSpectra.size());
                                        calculate_BinBasedScores(thydBinSpectra, tsolBinSpectra, bw, charge, precTol, fragment_tolerance, scoreType);
                                    }
                                }
                            } else if (!is_charged_based) {
                                // Run against all for MSRobin
                                ArrayList<MSnSpectrum> thydMSnSpectra = prepareData(thyd, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, 0, fragment_tolerance),
                                        tsolMSnSpectra = prepareData(tsol, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, 0, fragment_tolerance);
                                LOGGER.info("Size of MSnSpectra at spectra.folder=" + thydMSnSpectra.size());
                                LOGGER.info("Size of MSnSpectra at spectra.to.compare.folder=" + tsolMSnSpectra.size());

                                calculate_MSRobins(thydMSnSpectra, tsolMSnSpectra, bw, fragment_tolerance, precTol, calculationOptionIntensityMSRobin, msRobinCalculationOption);
                            } else if (is_charged_based) {
                                for (int charge : charges) {
                                    ArrayList<MSnSpectrum> thydMSnSpectra = prepareData(thyd, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, charge, fragment_tolerance),
                                            tsolMSnSpectra = prepareData(tsol, transformation, noiseFiltering, topN, percentage, is_precursor_peak_removed, charge, fragment_tolerance);
                                    LOGGER.info("Size of MSnSpectra at spectra.folder=" + thydMSnSpectra.size());
                                    LOGGER.info("Size of MSnSpectra at spectra.to.compare.folder=" + tsolMSnSpectra.size());
                                    calculate_MSRobins(thydMSnSpectra, tsolMSnSpectra, bw, fragment_tolerance, precTol, calculationOptionIntensityMSRobin, msRobinCalculationOption);
                                }
                            }
                        }
                    }
                }
            }
        }
//        System.exit(0);
    }

    /**
     * This method convert an MSnSpectrum into a BinMSnSpectrum with also
     * applying a given preprocessing settings
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
     * @param intensities_sum_or_mean_or_median
     * @param charge
     * @return
     * @throws MzMLUnmarshallerException
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NumberFormatException
     */
    private static BinMSnSpectrum constructBinMSnSpectrum(MSnSpectrum ms, boolean is_precursor_peak_removal, double fragment_tolerance,
            ConvertToBinMSnSpectrum convertToBinMSnSpectrumObj, boolean isNFTR) throws MzMLUnmarshallerException, IOException, ClassNotFoundException, NumberFormatException {
        BinMSnSpectrum binMSnSpectrum = null;
        if (is_precursor_peak_removal) {
            RemovePrecursorRelatedPeaks removal = new RemovePrecursorRelatedPeaks(ms, fragment_tolerance);
            removal.removePrecursor();
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
            int noiseFiltering, int transformation, int topN, boolean is_precursor_peak_removal, int charge, boolean isNFTR)
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
                if (charge == 0) {
                    BinMSnSpectrum binMSnSpectrum = constructBinMSnSpectrum(ms, is_precursor_peak_removal, fragment_tolerance, convertToBinMSnSpectrumObj, isNFTR);
                    if (binMSnSpectrum != null) {
                        binspectra.add(binMSnSpectrum);
                    }
                } else if (charge == ms.getPrecursor().getPossibleCharges().get(0).value) {
                    BinMSnSpectrum binMSnSpectrum = constructBinMSnSpectrum(ms, is_precursor_peak_removal, fragment_tolerance, convertToBinMSnSpectrumObj, isNFTR);
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
     * @param yeast_and_human_file
     * @param is_precursor_peak_removal
     * @param fragment_tolerance
     * @param noiseFiltering
     * @param transformation
     * @param intensities_sum_or_mean_or_median
     * @param yeast_spectra
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
    private static void calculate_BinBasedScores(ArrayList<BinMSnSpectrum> yeast_spectra, ArrayList<BinMSnSpectrum> yeast_human_spectra,
            BufferedWriter bw, int charge, double precursorTol, double fragTol, String scoreType)
            throws IllegalArgumentException, ClassNotFoundException, IOException, MzMLUnmarshallerException, NumberFormatException, InterruptedException {
        ExecutorService excService = Executors.newFixedThreadPool(ConfigHolder.getInstance().getInt("thread.numbers"));
        List<Future<SimilarityResult>> futureList = new ArrayList<>();
        for (BinMSnSpectrum binYeastHumanSp : yeast_human_spectra) {
            int tmpMSCharge = binYeastHumanSp.getSpectrum().getPrecursor().getPossibleCharges().get(0).value;
            if (charge == 0 || tmpMSCharge == charge) {
                if (!binYeastHumanSp.getSpectrum().getPeakList().isEmpty() && !yeast_spectra.isEmpty()) {
                    Calculate_Similarity similarity = new Calculate_Similarity(binYeastHumanSp, yeast_spectra, fragTol, precursorTol);
                    Future future = excService.submit(similarity);
                    futureList.add(future);
                }
            }
        }
        SimilarityMethods method = SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD;
        if (scoreType.equals("spearman")) {
            method = SimilarityMethods.SPEARMANS_CORRELATION;
        } else if (scoreType.equals("pearson")) {
            method = SimilarityMethods.PEARSONS_CORRELATION;
        }
        for (Future<SimilarityResult> future : futureList) {
            try {
                SimilarityResult get = future.get();
                String tmp_charge = get.getSpectrumChargeAsString(),
                        tmp_Name = get.getSpectrumName();
                double tmpPrecMZ = get.getSpectrumPrecursorMZ(),
                        score = get.getScores().get(method);
                if (score == Double.MIN_VALUE) {
                    // Means that score has not been calculated!
//                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t");
//                    bw.write("NA" + "\t" + "NA" + "\t" + "NA" + "\t" + "NA");
                } else {
                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t" + get.getTmpSpectrumName() + "\t" + score + "\n");
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * This method calculates similarities bin-based between yeast_human spectra
     * on the first data set against all yeast spectra on the second data set
     *
     * @param min_mz
     * @param max_mz
     * @param topN
     * @param percentage
     * @param yeast_and_human_file
     * @param is_precursor_peak_removal
     * @param fragment_tolerance
     * @param noiseFiltering
     * @param transformation
     * @param intensities_sum_or_mean_or_median
     * @param yeast_spectra
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
    private static void calculate_BinBasedScoresObsolete_AllTogether(ArrayList<BinMSnSpectrum> yeast_spectra, ArrayList<BinMSnSpectrum> yeast_human_spectra,
            BufferedWriter bw, int charge, double precursorTol, double fragTol)
            throws IllegalArgumentException, ClassNotFoundException, IOException, MzMLUnmarshallerException, NumberFormatException, InterruptedException {
        ExecutorService excService = Executors.newFixedThreadPool(ConfigHolder.getInstance().getInt("thread.numbers"));
        List<Future<SimilarityResult>> futureList = new ArrayList<>();
        for (BinMSnSpectrum binYeastHumanSp : yeast_human_spectra) {
            int tmpMSCharge = binYeastHumanSp.getSpectrum().getPrecursor().getPossibleCharges().get(0).value;
            if (charge == 0 || tmpMSCharge == charge) {
                if (!binYeastHumanSp.getSpectrum().getPeakList().isEmpty() && !yeast_spectra.isEmpty()) {
                    Calculate_Similarity similarity = new Calculate_Similarity(binYeastHumanSp, yeast_spectra, fragTol, precursorTol);
                    Future future = excService.submit(similarity);
                    futureList.add(future);
                }
            }
        }
        for (Future<SimilarityResult> future : futureList) {
            try {
                SimilarityResult get = future.get();
                String tmp_charge = get.getSpectrumChargeAsString(),
                        tmp_Name = get.getSpectrumName();
                double tmpPrecMZ = get.getSpectrumPrecursorMZ();
                double dot_product = get.getScores().get(SimilarityMethods.NORMALIZED_DOT_PRODUCT_STANDARD),
                        dot_product_skolow = get.getScores().get(SimilarityMethods.NORMALIZED_DOT_PRODUCT_SOKOLOW),
                        pearson = get.getScores().get(SimilarityMethods.PEARSONS_CORRELATION),
                        spearman = get.getScores().get(SimilarityMethods.SPEARMANS_CORRELATION);
                if (dot_product == Double.MIN_VALUE) {
                    // Means that score has not been calculated!
//                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t");
//                    bw.write("NA" + "\t" + "NA" + "\t" + "NA" + "\t" + "NA");
                } else {
                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t" + get.getTmpSpectrumName() + "\t");
                    bw.write(dot_product + "\t" + dot_product_skolow + "\t" + pearson + "\t" + spearman + "\n");
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     *
     * This method calculates similarities for MSRobin for each spectra on
     * yeast_human_spectra on the first data set against all yeast spectra on
     * the second data set
     *
     * @param yeast_spectra
     * @param yeast_human_spectra
     * @param bw
     * @param fragTol
     * @param precTol
     * @throws IllegalArgumentException
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws MzMLUnmarshallerException
     * @throws NumberFormatException
     * @throws InterruptedException
     */
    private static void calculate_MSRobins(ArrayList<MSnSpectrum> yeast_spectra, ArrayList<MSnSpectrum> yeast_human_spectra,
            BufferedWriter bw, double fragTol, double precTol, int calculationOptionIntensityMSRobin, int msRobinCalculationOption)
            throws IllegalArgumentException, ClassNotFoundException, IOException, MzMLUnmarshallerException, NumberFormatException, InterruptedException {
        ExecutorService excService = Executors.newFixedThreadPool(ConfigHolder.getInstance().getInt("thread.numbers"));
        List<Future<SimilarityResult>> futureList = new ArrayList<>();
        for (MSnSpectrum yeast_human_spectrum : yeast_human_spectra) {
            Calculate_Similarity similarity = new Calculate_Similarity(yeast_human_spectrum, yeast_spectra, fragTol, precTol, calculationOptionIntensityMSRobin, msRobinCalculationOption);
            Future future = excService.submit(similarity);
            futureList.add(future);
        }
        for (Future<SimilarityResult> future : futureList) {
            try {
                SimilarityResult get = future.get();
                String tmp_charge = get.getSpectrumChargeAsString(),
                        tmp_Name = get.getSpectrumName();
                double tmpPrecMZ = get.getSpectrumPrecursorMZ(),
                        msrobin = get.getScores().get(SimilarityMethods.MSRobin);
                if (msrobin == Double.MIN_VALUE) {
                    // Means that score has not been calculated!
//                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t");
//                    bw.write("NA" + "\n");
                } else {
                    bw.write(tmp_Name + "\t" + tmp_charge + "\t" + tmpPrecMZ + "\t" + get.getTmpSpectrumName() + "\t" + msrobin + "\n");
                }
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error(e);
            }
        }
        excService.shutdown();

    }

    /**
     * Prepare data for MSRobin calculation!
     *
     *
     * @param mgf_file
     * @param transformation
     * @param is_precursor_peak_removal
     * @param charge
     * @param fragment_tolerance
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ClassNotFoundException
     * @throws MzMLUnmarshallerException
     */
    private static ArrayList<MSnSpectrum> prepareData(File mgf_file, int transformation, int noiseFiltering, int topN, double percentage, boolean is_precursor_peak_removal, int charge, double fragment_tolerance) throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        ArrayList<MSnSpectrum> spectra = new ArrayList<>();
        if (mgf_file.getName().endsWith(".mgf")) {
            fct.clearFactory();
            fct.addSpectra(mgf_file, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(mgf_file.getName())) {
                MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(mgf_file.getName(), title);
                if (charge == 0 || charge == ms.getPrecursor().getPossibleCharges().get(0).value) {
                    if (is_precursor_peak_removal) {
                        RemovePrecursorRelatedPeaks removal = new RemovePrecursorRelatedPeaks(ms, fragment_tolerance);
                        removal.removePrecursor();
                    }
                    if (noiseFiltering > 0) {
                        apply_noise_filtering(ms, noiseFiltering, topN, percentage);
                    }
                    if (transformation > 0) {
                        transform_intensities(ms, transformation);
                    }
                    if (!ms.getPeakList().isEmpty()) {
                        spectra.add(ms);
                    }
                }
            }
            fct.clearFactory();
        }
        return spectra;
    }

    /**
     * This class applies noise filtering 1 - PrideAsapNoiseFiltering 2 -
     * TopNFiltering 3 - DiscardLowIntensePeaks
     *
     * @param noise_filtering_case [0-3]
     * @param ms an MSnSpectrum object
     *
     */
    private static void apply_noise_filtering(MSnSpectrum ms, int noise_filtering_case, int topN, double percentage) {
        switch (noise_filtering_case) {

            case 1:
                NoiseFilteringPrideAsap noiseFilterImp = new NoiseFilteringPrideAsap();
                noiseFilterImp.noiseFilter(ms);
                break;
            case 2:
                TopNFiltering topNFiltering = new TopNFiltering(topN);
                topNFiltering.noiseFilter(ms);
                break;

            case 3:
                DiscardLowIntensePeaks discardlowintense = new DiscardLowIntensePeaks(percentage);
                discardlowintense.noiseFilter(ms);
                break;
        }
    }

    private static void transform_intensities(MSnSpectrum ms, int transformation_case) {
        Transformations tr;
        TransformIntensitiesImp transform;
        switch (transformation_case) {
            case 1: // Log 2
                tr = Transformations.LOG_2;
                transform = new TransformIntensitiesImp(tr, ms);
                transform.transform(tr);
                ms.setPeakList(transform.getTr_peaks());
                break;
            case 2: // Square root
                tr = Transformations.SQR_ROOT;
                transform = new TransformIntensitiesImp(tr, ms);
                transform.transform(tr);
                ms.setPeakList(transform.getTr_peaks());
                break;
        }
    }

    /**
     * Send an event to the google analytics server for tool start monitoring.
     */
    private static void sendAnalyticsEvent() {
        String COLLECT_URL = "http://www.google-analytics.com/collect";
        String POST = "v=1&tid=UA-36198780-13&cid=35119a79-1a05-49d7-b876-bb88420f825b&uid=asuueffeqqss&t=event&ec=usage&ea=toolstart&el=spectrumsimilarity";

        //spring rest template
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request = new HttpEntity<>(POST);
        ResponseEntity<String> postForEntity
                = restTemplate.postForEntity(COLLECT_URL,
                        request, String.class);

        if (postForEntity.getStatusCode().equals(HttpStatus.OK)) {
            LOGGER.info("Successfully sent analytics event.");
        }
    }

}
