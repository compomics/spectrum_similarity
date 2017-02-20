/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import optimization.spectra.Peak;
import cal.binBased.Calculate_BinSpectrum_Similarity;
import com.compomics.util.experiment.biology.Atom;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.ScorePipeline;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import util.CalculateMS1Err;

/**
 *
 * @author Genet and Sule
 */
public class DequeNCompare implements Runnable {

    private String filename1 = "", // name of the first spectrum file
            filename2 = ""; // name of the second spectrum file
    private File toPrint = null; // name of the file to write calculation output   

    private double frag_tol = 0.5, // fragment tolerance (in Da)
            prec_tol = 10; // precursor tolerance
    private boolean isPrecTolPPM = true; // true: the unit of the given precursor tolerance is PPM, false: the unit is Da

    private int summary_option = 0, // 0-to sum up all intensities within a bin; 2-Take mean of all intensities or 3-Take the percentile
            shift = 0, // to shift spectra for binning in order to calculate cross-correlation 
            threads = 4;

    private ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(8192),
            queue2 = new ArrayBlockingQueue<>(8192);
    private ArrayList<Peak> spectrumList1 = new ArrayList(), // a list of peaks from the first spectra to compare
            spectrumList2 = new ArrayList(); // a list of peaks from the first spectra to compare
    private PrintWriter pr = null;
    public static boolean flag = true;

    /* Constructors */
    public DequeNCompare(String f1, String f2) {
        this.filename1 = f1;
        this.filename2 = f2;
    }

    public DequeNCompare(String f1, String f2, File toPrint) {
        this.filename1 = f1;
        this.filename2 = f2;
        this.toPrint = toPrint;
    }

    public DequeNCompare(String f1, String f2, File toPrint, double frag_tol, int summary_option) {
        this.filename1 = f1;
        this.filename2 = f2;
        this.toPrint = toPrint;
        this.frag_tol = frag_tol;
        this.summary_option = summary_option;
    }

    public DequeNCompare(String f1, String f2, File toPrint, double frag_tol, double prec_tol, boolean isPrecTolPPM, int summary_option) {
        this.filename1 = f1;
        this.filename2 = f2;
        this.toPrint = toPrint;
        this.frag_tol = frag_tol;
        this.prec_tol = prec_tol;
        this.isPrecTolPPM = isPrecTolPPM;
        this.summary_option = summary_option;
    }

    @Override
    public void run() {

        try {
            String tmpBuffer1,
                    tmpBuffer2,
                    specAname = "",
                    specBname = "";

            ReaderThread reader1 = new ReaderThread(queue1, filename1),
                    reader2 = new ReaderThread(queue2, filename2);
            pr = new PrintWriter(toPrint);
            String title = "Spectrum A" + "\t" + "Spectrum B" + "\t" + "Precursor tolerance (inPPM)" + "\t" + "Score" + "\n";
            if (!isPrecTolPPM) {
                title = "Spectrum A" + "\t" + "Spectrum B" + "\t" + "Precursor tolerance (in Da)" + "\t" + "Score" + "\n";
            }
            pr.print(title);

            final ExecutorService executor = Executors.newFixedThreadPool(threads);
            executor.execute(reader1);
            executor.execute(reader2);
            double prec_mz_A = 0,
                    prec_mass_A = 0,
                    prec_mz_B = 0,
                    prec_mass_B = 0;

            //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (true) {

                while (!((tmpBuffer1 = queue1.take()).equals("END IONS"))) {// && !((tempBuffer2 = queue1.take()).equals("EOF"))) {
                    if (tmpBuffer1.startsWith("TITLE=")) {
                        specAname = tmpBuffer1.split("=")[1];
                    } else if (tmpBuffer1.startsWith("PEPMASS=")) {
                        prec_mz_A = Double.parseDouble(tmpBuffer1.split("=")[1]);
                    } else if (tmpBuffer1.startsWith("CHARGE=")) {
                        int prec_charge_A = Integer.parseInt(tmpBuffer1.split("=")[1]);
                        prec_mass_A = calculate_precursor_mass(prec_mz_A, prec_charge_A);
                    } else if (tmpBuffer1.contains("\t")) {
                        // each line has m/z and intensity information for peaks in given spectrum 
                        spectrumList1.add(new Peak(Double.parseDouble(tmpBuffer1.split("\t")[0]), Double.parseDouble(tmpBuffer1.split("\t")[1])));
                    } else if (tmpBuffer1.contains(" ")) {
                        // each line has m/z and intensity information for peaks in given spectrum 
                        spectrumList1.add(new Peak(Double.parseDouble(tmpBuffer1.split(" ")[0]), Double.parseDouble(tmpBuffer1.split(" ")[1])));
                    }
                }

                while (!(tmpBuffer2 = queue2.take()).equals("EOF")) {
                    if (tmpBuffer2.startsWith("TITLE=")) {
                        specBname = tmpBuffer2.split("=")[1];
                    } else if (tmpBuffer2.startsWith("PEPMASS=")) {
                        prec_mz_B = Double.parseDouble(tmpBuffer2.split("=")[1]);
                    } else if (tmpBuffer2.startsWith("CHARGE=")) {
                        int prec_charge_B = Integer.parseInt(tmpBuffer2.split("=")[1]);
                        prec_mass_B = calculate_precursor_mass(prec_mz_B, prec_charge_B);
                    } else if (tmpBuffer2.equals("END IONS")) {
                        // calculate the results    
                        // calculate precursor tolerance
                        double tmp_prec_tol = Math.abs(CalculateMS1Err.getMS1Err(isPrecTolPPM, prec_mass_A, prec_mass_B));

                        // Make sure spectra have enough peaks
                        if (!spectrumList1.isEmpty() && !spectrumList2.isEmpty()) {
//                            System.out.println(specAname + "\t" + specBname + "\t" + prec_mz_A + "\t" + prec_mz_B + "\t" + tmp_prec_tol);
                            // precTol=0, to score against all or precTol>0 to score only selected ones
                            if ((prec_tol == 0) || (tmp_prec_tol <= prec_tol)) {
//                                System.out.println("TMP PREC TOL " + tmp_prec_tol);
                                pr.print(specAname + "\t" + specBname + "\t" + tmp_prec_tol + "\t");
                                calculateBinnedBasedSimilarity(spectrumList1, spectrumList2, pr);
                            }
                        } else {
//                            pr.print(specAname + "\t" + specBname + "\t" + "-" + "\t" + "NotEnoughPeaks \n");
                        }
                        spectrumList2.clear();
                    } else if (tmpBuffer2.contains("\t")) {
                        spectrumList2.add(new Peak(Double.parseDouble(tmpBuffer2.split("\t")[0]), Double.parseDouble(tmpBuffer2.split("\t")[1])));
                    } else if (!tmpBuffer2.equals("END IONS") && !tmpBuffer2.equals("BEGIN IONS")) {
                        if (tmpBuffer2.contains(" ")) {                        // each line has m/z and intensity information for peaks in given spectrum 
                            spectrumList2.add(new Peak(Double.parseDouble(tmpBuffer2.split(" ")[0]), Double.parseDouble(tmpBuffer2.split(" ")[1])));
                        }
                    }
                }

                if (queue1.peek().equals("EOF")) {
                    pr.close();
                    executor.shutdown();
                    break;
                } else {
                    reader2 = new ReaderThread(queue2, filename2);
                    executor.execute(reader2);
                }
                spectrumList1.clear();
                spectrumList2.clear();
            }

        } catch (InterruptedException e) {

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DequeNCompare.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Simple pairwise spectrum similarity calculation
     *
     * @param spectrumA
     * @param spectrumB
     * @param pr
     */
    private void calculateBinnedBasedSimilarity(ArrayList<Peak> spectrumA, ArrayList<Peak> spectrumB, PrintWriter pr) {

        // Prepare binned spectrum for each spectrum 
        double min_value = spectrumA.get(0).getMz(),
                max_value = spectrumA.get(spectrumA.size() - 1).getMz();

        if (spectrumB.get(0).getMz() < min_value) {
            min_value = spectrumB.get(0).getMz();
        }
        if (spectrumB.get(spectrumB.size() - 1).getMz() > max_value) {
            max_value = spectrumB.get(spectrumB.size() - 1).getMz();
        }

        double[] binA = getBin_spectrum(spectrumA, shift, frag_tol, min_value, max_value, summary_option),
                binB = getBin_spectrum(spectrumB, shift, frag_tol, min_value, max_value, summary_option);
        double dot_score = calculateDotProductDerived(binA, binB, 1, 0, true, min_value);

        pr.print(dot_score + "\n");
    }

    /**
     * THIS METHOD COMES FROM BinMSnSpectrum
     *
     * @param peaks
     * @param shift
     * @param fragment_tolerance
     * @param min_value
     * @param max_value
     * @param peak_summary_option
     * @return
     */
    public double[] getBin_spectrum(ArrayList<Peak> peaks, int shift, double fragment_tolerance, double min_value, double max_value, int peak_summary_option) {
        ArrayList<Double> bin_spec_al = new ArrayList<Double>();
        double binSize = (fragment_tolerance * 2),
                upperLimit = max_value + 0.00001;
        for (double lowerLimit = min_value; lowerLimit < upperLimit; lowerLimit = lowerLimit + binSize) {
            double tmp_intensity_bin = 0;
            DescriptiveStatistics obj = new DescriptiveStatistics();
            for (Peak p : peaks) {
                double mz = p.getMz() + shift;
                if (mz >= lowerLimit && mz < lowerLimit + binSize) {
                    obj.addValue(p.getIntensity());
                }
            }
            if (obj.getN() > 0) {
                if (peak_summary_option == 0) {
                    tmp_intensity_bin = obj.getSum();
                } else if (peak_summary_option == 1) {
                    tmp_intensity_bin = obj.getMean();
                } else if (peak_summary_option == 2) {
                    tmp_intensity_bin = obj.getPercentile(50);
                }
            }
            // put every bin_pectrum
            bin_spec_al.add(tmp_intensity_bin);
        }
        // convert an arraylist to double array
        // initiate size of array
        double[] bin_spectrum = new double[bin_spec_al.size()];
        for (int i = 0; i < bin_spec_al.size(); i++) {
            bin_spectrum[i] = bin_spec_al.get(i);
        }
        return bin_spectrum;
    }

    /**
     * METHOD COMES FROM Calculate_BinSpectrum_Similarity.java
     *
     * This method calculates dot product score according to given weights.
     * Maximum similarity gives 1. Minimum gives 0.
     *
     * @param xArray - binned-spectrumX with intensities summed up
     * @param yArray - binned-spectrumY with intensities summed up
     * @param x - weight for peak intensity
     * @param y - weight for peak m/z
     * @param isNormalized - true: normalize calculated dot-score, false: no
     * normalization on dot-score
     * @return a score
     */
    public double calculateDotProductDerived(double[] xArray, double[] yArray, double x, double y, boolean isNormalized, double min_mz) {
        // now, calculate dot product score
        double dot_product_alpha_beta = 0,
                dot_product_alpha_alpha = 0,
                dot_product_beta_beta = 0,
                score = 0;
        double binSize = 2 * frag_tol,
                mz = min_mz + binSize;
        for (int i = 0; i < xArray.length; i++) {
            double mz_peakA = mz,
                    mz_peakB = mz,
                    intensity_peakA = xArray[i],
                    intensity_peakB = yArray[i];
            boolean control = false;
            if (intensity_peakA == 0 && intensity_peakB == 0) {
                control = true;
            }
            if (!control) {
                if (i == xArray.length - 1) {// make sure last one is not empty..
                    mz_peakA = mz_peakA + 0.0000001;
                    mz_peakB = mz_peakB + 0.0000001;
                }
                double alpha = Math.pow(intensity_peakA, x) * Math.pow(mz_peakA, y),
                        beta = Math.pow(intensity_peakB, x) * Math.pow(mz_peakB, y);
                dot_product_alpha_beta = dot_product_alpha_beta + (double) (alpha * beta);
                dot_product_alpha_alpha = dot_product_alpha_alpha + (double) (alpha * alpha);
                dot_product_beta_beta = dot_product_beta_beta + (double) (beta * beta);
                mz = mz + binSize;
            }
        }
        if (isNormalized) {
            double normalized_dot_product = (double) dot_product_alpha_beta / (double) (Math.sqrt(dot_product_alpha_alpha * dot_product_beta_beta));
            score = normalized_dot_product;
        } else {
            score = dot_product_alpha_beta;
        }
        return score;
    }

    /**
     * Calculate precursor mass for a given precursor m/z
     *
     * @param mz is a precursor m/z
     * @param charge is a precursor charge
     * @return a double value for a precursor mass
     */
    public static double calculate_precursor_mass(double mz, int charge) {
        double mass = 0;
        mass = (mz * charge) - (charge * Atom.H.getMonoisotopicMass());
        return mass;
    }
}
