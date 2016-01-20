/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectra.preprocess.filter.implementation;

import preprocess.filter.noise.implementation.DiscardLowIntensePeaks;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import junit.framework.TestCase;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class DiscardLowIntensePeaksTest extends TestCase {

    public DiscardLowIntensePeaksTest(String testName) {
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
     * Test of noiseFilter method, of class DiscardLowIntensePeaks.
     */
    public void testNoiseFilter() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("noiseFilter");
        File testFileFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\TapeWormAnalysis\\TestingData\\NoiseFiltering");
        for (File testFile : testFileFolder.listFiles()) {
            if (testFile.getName().endsWith(".mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(testFile);
                System.out.println(testFile.getName());
                for (String title : fct.getSpectrumTitles(testFile.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(testFile.getName(), title);

                    DiscardLowIntensePeaks instance = new DiscardLowIntensePeaks(5);
                    MSnSpectrum result = instance.noiseFilter(ms);

                    assertEquals(59, ms.getPeakList().size());

                    ArrayList<Peak> actualPeaks = new ArrayList<Peak>(result.getPeakList());
                    Collections.sort(actualPeaks, Peak.AscendingMzComparator);

                    assertEquals(actualPeaks.get(0).getMz(), 129.0795898, 0);
                    assertEquals(actualPeaks.get(1).getMz(), 130.1602173, 0);
                    assertEquals(actualPeaks.get(9).getMz(), 232.1122437, 0);
                    assertEquals(actualPeaks.get(14).getMz(), 258.8475342, 0);
                    assertEquals(actualPeaks.get(16).getMz(), 263.7848511, 0);

                    assertEquals(actualPeaks.get(0).getIntensity(), 29.98080826, 0);
                    assertEquals(actualPeaks.get(1).getIntensity(), 33.76114655, 0);
                    assertEquals(actualPeaks.get(9).getIntensity(), 38.57017899, 0);
                    assertEquals(actualPeaks.get(14).getIntensity(), 26.63854218, 0);
                    assertEquals(actualPeaks.get(16).getIntensity(), 13.93132687, 0);
                }
            }
        }
    }

}
