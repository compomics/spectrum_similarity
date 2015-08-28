/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

import bookChapter.GetSpecAnDID;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class FindSimilarity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        double pep = 0.5;
        // change the settings
        String settings1 = "BC_NFTR_NFTR_NF_None_TR_None_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                // Intensity transformation 
                settings2 = "BC_NFTR_NFTR_NF_None_TR_Log2_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings3 = "BC_NFTR_NFTR_NF_None_TR_Sqrt_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                // Noise filtering
                settings4 = "BC_NFTR_NFTR_NF_Top50N_TR_None_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings5 = "BC_NFTR_NFTR_NF_Top100N_TR_None_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings6 = "BC_NFTR_NFTR_NF_Pride_TR_None_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings7 = "BC_NFTR_NFTR_NF_LowPrec_TR_None_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                // precursor peak removal
                settings8 = "BC_NFTR_NFTR_NF_None_TR_None_PPR_windowBased_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings9 = "BC_NFTR_NFTR_NF_None_TR_None_PPR_anyRelevantPeaks_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                
                // NF-TR or TR-NF with the best one
                settings10= "BC_NFTR_TRNF_NF_Pride_TR_Sqrt_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                settings11= "BC_NFTR_NFTR_NF_Pride_TR_Sqrt_PPR_None_CHR_givenCharge_PRECTOL_3.0_binScores.txt",
                
                setting = "";
        setting = settings11;
        String settingsFolder = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\experimental",
                mascotIDFolder = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mascot_search/";

        // ups run output - ups-matched spectra on yeast-ups data set against ups-matched spectra
        // read and define same/different peptide 
        // select only exact matched peptide peptide ID spectra with also new column of same peptide
        // write this same peptide ID spectra on the output
        // peptide Info: either same peptide or different peptide
        // protein Info: either same protein or different protein 
        // return "nsp" number of same peptide
        
        // nonups output - non-ups matched spectra on yeast-ups data set
        // randomly select nsp number of non-ups matched spectra
        // peptide Info: either same peptide or different peptide
        // protein Info: either same protein or different protein 
        // add these selected non-ups matched different peptide as 
        // there are two files       
        File upsResult = new File(settingsFolder + "/" + "ups_specs_" + setting), // input
                nonUPSResult = new File(settingsFolder + "/" + "non_ups_specs_" + setting), // input2                
                output = new File(settingsFolder + "/toAnalyze/steps/" + "id_" + setting), // output
                yeast_ups_mascot = new File(mascotIDFolder + "/Orbi2_study6a_W080314_6E008_yeast_S48_ft8_pc_SvenSPyeast.dat.parsed.ms2pip.percolator_only.ms2pipscore"),
                ups_mascot = new File(mascotIDFolder + "/Orbi2_study6a_W080314_6QC1_sigma48_ft8_pc_SvenSPyeast.dat.parsed.ms2pip.percolator_only.ms2pipscore");

        Similarity s = new Similarity(upsResult, nonUPSResult, output, ups_mascot, yeast_ups_mascot);
        s.analyze();

    }
}
