/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter;

import bookChapter.experimental.AnalyzeExpScoring;
import bookChapter.theoretical.AnalyzeTheoreticalMSMSCalculation;
import config.ConfigHolder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class enables scoring given spectra against either theoretical spectra
 * or acquired spectra.
 *
 * @author Sule
 */
public class CompareScoringFunctions {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.InterruptedException
     * @throws uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException
     * @throws java.io.FileNotFoundException
     * @throws java.util.concurrent.ExecutionException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, MzMLUnmarshallerException, FileNotFoundException, ExecutionException {
        Logger l = Logger.getLogger("CompareScoringFunctions");
        boolean doesCompareTheoreticals = ConfigHolder.getInstance().getBoolean("compareTheoreticals");
        if (doesCompareTheoreticals) {
            l.info("Comparison against theoretical specttra.");
            AnalyzeTheoreticalMSMSCalculation.main(args);
        } else {
            l.info("Comparison against acquired (aka experimental) specttra.");
            AnalyzeExpScoring.main(args);
        }
    }

}
