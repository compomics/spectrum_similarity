/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spectra.preprocess.filter.implementation;

import preprocess.filter.noise.implementation.TopNFiltering;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class TopNFilteringTest extends TestCase {
    
    public TopNFilteringTest(String testName) {
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
     * Test of noiseFilter method, of class TopNFiltering.
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

                    TopNFiltering instance = new TopNFiltering(50);
                    MSnSpectrum result = instance.noiseFilter(ms);

                    assertEquals(50, ms.getPeakList().size());

                    ArrayList<Peak> actualPeaks = new ArrayList<Peak>(result.getPeakList());
                    Collections.sort(actualPeaks, Peak.AscendingMzComparator);

                    assertEquals(actualPeaks.get(0).getMz(), 129.0795898, 0);
                    assertEquals(actualPeaks.get(1).getMz(), 130.1602173, 0);
                    assertEquals(actualPeaks.get(9).getMz(), 234.1070557, 0);
                    assertEquals(actualPeaks.get(14).getMz(), 271.7967529, 0);
                    assertEquals(actualPeaks.get(16).getMz(), 277.8079529, 0);
                    assertEquals(actualPeaks.get(48).getMz(), 571.2167358, 0);
                    assertEquals(actualPeaks.get(49).getMz(), 572.0350342, 0);

                    assertEquals(actualPeaks.get(0).getIntensity(), 29.98080826, 0);
                    assertEquals(actualPeaks.get(1).getIntensity(), 33.76114655, 0);
                    assertEquals(actualPeaks.get(9).getIntensity(), 44.92469406, 0);
                    assertEquals(actualPeaks.get(14).getIntensity(), 21.06731224, 0);
                    assertEquals(actualPeaks.get(16).getIntensity(), 17.09956741, 0);
                    assertEquals(actualPeaks.get(48).getIntensity(), 44.12823486, 0);
                    assertEquals(actualPeaks.get(49).getIntensity(), 25.19287109, 0);
                    
                    instance = new TopNFiltering(40);
                    instance.noiseFilter(ms);
                    assertEquals(40, ms.getPeakList().size());

                }
            }
        }
    }
    
}
