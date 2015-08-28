/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cal.binBased;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;

/**
 *
 * @author Sule
 */
public class ConvertToBinMSnSpectrumTest extends TestCase {
    
    public ConvertToBinMSnSpectrumTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of convertToBinMSnSpectrum method, of class ConvertToBinMSnSpectrum.
     */
    public void testConvertToBinMSnSpectrum() throws Exception {
        System.out.println("convertToBinMSnSpectrum");
        // Create an object to convert a given MSnSpectrum into BinMSnSpectrum
        double min_mz = 113,
                max_mz = 119.5;
        int topN = 50,
                percentage = 5,
                intensities_sum_or_mean_or_median = 0;

        File testFileFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\TapeWormAnalysis\\TestingData\\ConvertMSnBinSpectrum");
        for (File testFile : testFileFolder.listFiles()) {
            if (testFile.getName().endsWith(".mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(testFile);
                System.out.println(testFile.getName());
                for (String title : fct.getSpectrumTitles(testFile.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(testFile.getName(), title);
                    // So no filtering or no intensities transformation
                    // raw peaks
                    ConvertToBinMSnSpectrum instance = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percentage, 0.25, 0, 0, 0);
                    BinMSnSpectrum result = instance.convertToBinMSnSpectrum(ms, true);
                    double[] bin_spectrum1 = result.getBin_spectrum();
                    assertEquals(7.21, bin_spectrum1[0], 0.05);
                    assertEquals(70.96823312, bin_spectrum1[1], 0.05);
                    assertEquals(22.94, bin_spectrum1[2], 0.05);
                    assertEquals(16.55, bin_spectrum1[3], 0.05);
                    assertEquals(196.4237669, bin_spectrum1[4], 0.05);
                    assertEquals(52.92462015, bin_spectrum1[5], 0.05);
                    assertEquals(144.4771905, bin_spectrum1[6], 0.05);
                    assertEquals(33.64148712, bin_spectrum1[7], 0.05);
                    assertEquals(119.7086511, bin_spectrum1[8], 0.05);
                    assertEquals(248.8392706, bin_spectrum1[9], 0.05);
                    assertEquals(235.171773, bin_spectrum1[10], 0.05);
                    assertEquals(974.3392551, bin_spectrum1[11], 0.05);
                    assertEquals(41.56056785, bin_spectrum1[12], 0.05);

                    // TOP50 peaks with raw peak intensities
                    instance = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percentage, 0.25, 2, 0, 0);
                    result = instance.convertToBinMSnSpectrum(ms, true);
                    bin_spectrum1 = result.getBin_spectrum();
                    assertEquals(0, bin_spectrum1[0], 0.05);
                    assertEquals(63.74, bin_spectrum1[1], 0.05);
                    assertEquals(0, bin_spectrum1[2], 0.05);
                    assertEquals(13.28, bin_spectrum1[3], 0.05);
                    assertEquals(182.06, bin_spectrum1[4], 0.05);
                    assertEquals(47.04, bin_spectrum1[5], 0.05);
                    assertEquals(134.75, bin_spectrum1[6], 0.05);
                    assertEquals(33.64, bin_spectrum1[7], 0.05);
                    assertEquals(117.73, bin_spectrum1[8], 0.05);
                    assertEquals(227.43, bin_spectrum1[9], 0.05);
                    assertEquals(235.171773, bin_spectrum1[10], 0.05);
                    assertEquals(941.392551, bin_spectrum1[11], 0.05);
                    assertEquals(27.4, bin_spectrum1[12], 0.05);

                    // TOP50 peaks with log2 peak intensities
                    instance = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percentage, 0.25, 2, 1, 0);
                    result = instance.convertToBinMSnSpectrum(ms, true);
                    bin_spectrum1 = result.getBin_spectrum();
                    assertEquals(0, bin_spectrum1[0], 0.05);
                    assertEquals(9.98, bin_spectrum1[1], 0.05);
                    assertEquals(0, bin_spectrum1[2], 0.05);
                    assertEquals(3.73, bin_spectrum1[3], 0.05);
                    assertEquals(24.15, bin_spectrum1[4], 0.05);
                    assertEquals(9.08, bin_spectrum1[5], 0.05);
                    assertEquals(25.32, bin_spectrum1[6], 0.05);
                    assertEquals(5.07, bin_spectrum1[7], 0.05);
                    assertEquals(21.85, bin_spectrum1[8], 0.05);
                    assertEquals(32.95, bin_spectrum1[9], 0.05);
                    assertEquals(36.75, bin_spectrum1[10], 0.05);
                    assertEquals(64.96, bin_spectrum1[11], 0.05);
                    assertEquals(7.52, bin_spectrum1[12], 0.05);
                }
            }
        }
        // MSnSpectrum object needs to be reseted and SpectrumFactory must be cleared
        // Otherwise, since after processing MSnSpectrum object set to the current state, 
        // Already transformed intensities will be used, instead of using original intensities
        ConvertToBinMSnSpectrum instance = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percentage, 0.25, 2, 2, 0);
        for (File testFile : testFileFolder.listFiles()) {
            if (testFile.getName().endsWith(".mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.clearFactory();
                fct.addSpectra(testFile);
                System.out.println(testFile.getName());
                for (String title : fct.getSpectrumTitles(testFile.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(testFile.getName(), title);
                    // TOP50 peaks with sqrt peak intensities
                    BinMSnSpectrum result = instance.convertToBinMSnSpectrum(ms, true);
                    double[] bin_spectrum1 = result.getBin_spectrum();
                    assertEquals(0, bin_spectrum1[0], 0.05);
                    assertEquals(11.285, bin_spectrum1[1], 0.05);
                    assertEquals(0, bin_spectrum1[2], 0.05);
                    assertEquals(3.644, bin_spectrum1[3], 0.05);
                    assertEquals(28.45, bin_spectrum1[4], 0.05);
                    assertEquals(9.68, bin_spectrum1[5], 0.05);
                    assertEquals(27.132, bin_spectrum1[6], 0.05);
                    assertEquals(5.8, bin_spectrum1[7], 0.05);
                    assertEquals(23.48, bin_spectrum1[8], 0.05);
                    assertEquals(37.73, bin_spectrum1[9], 0.05);
                    assertEquals(41.26, bin_spectrum1[10], 0.05);
                    assertEquals(93.77, bin_spectrum1[11], 0.05);
                    assertEquals(7.38, bin_spectrum1[12], 0.05);
                }
            }
        }

        ConvertToBinMSnSpectrum instance2 = new ConvertToBinMSnSpectrum(min_mz, max_mz, topN, percentage, 0.25, 2, 0, 1);
        for (File testFile : testFileFolder.listFiles()) {
            if (testFile.getName().endsWith(".mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.clearFactory();
                fct.addSpectra(testFile);
                System.out.println(testFile.getName());
                for (String title : fct.getSpectrumTitles(testFile.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(testFile.getName(), title);
                    double fragment_tolerance = 0.25;
                    int noise_filtering_case = 2;
                    int transformation_case = 0;
                    // TOP50 peaks with raw intensities to take average of all intensities, not sum! 
                    BinMSnSpectrum result2 = instance2.convertToBinMSnSpectrum(ms, true);
                    double[] bin_spectrum1 = result2.getBin_spectrum();
                    assertEquals(0, bin_spectrum1[0], 0.05);
                    assertEquals(31.87, bin_spectrum1[1], 0.05);
                    assertEquals(0, bin_spectrum1[2], 0.05);
                    assertEquals(13.28, bin_spectrum1[3], 0.05);
                    assertEquals(36.41, bin_spectrum1[4], 0.05);
                    assertEquals(23.52, bin_spectrum1[5], 0.05);
                    assertEquals(22.45, bin_spectrum1[6], 0.05);
                    assertEquals(33.64, bin_spectrum1[7], 0.05);
                    assertEquals(23.54, bin_spectrum1[8], 0.05);
                    assertEquals(32.49, bin_spectrum1[9], 0.05);
                    assertEquals(29.39, bin_spectrum1[10], 0.05);
                    assertEquals(85.58, bin_spectrum1[11], 0.05);
                    assertEquals(13.70, bin_spectrum1[12], 0.05);
                }
            }
        }
    }
}
