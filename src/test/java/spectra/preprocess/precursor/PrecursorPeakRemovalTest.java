/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectra.preprocess.precursor;

import preprocess.filter.precursor.RemovePrecursorRelatedPeaks;
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
public class PrecursorPeakRemovalTest extends TestCase {

    public PrecursorPeakRemovalTest(String testName) {
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
     * Test of removePrecursor method, of class RemovePrecursorRelatedPeaks.
     */
    public void testRemovePrecursor() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("removePrecursor");
        File testFileFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\TapeWormAnalysis\\TestingData\\PrecursorPeakRemoval");
        for (File testFile : testFileFolder.listFiles()) {
            if (testFile.getName().endsWith(".mgf")) {
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(testFile);
                System.out.println(testFile.getName());
                for (String title : fct.getSpectrumTitles(testFile.getName())) {
                    MSnSpectrum ms = (MSnSpectrum) fct.getSpectrum(testFile.getName(), title);
                    assertEquals(84, ms.getPeakList().size());

                    ArrayList<Peak> tmpPeaks = new ArrayList<Peak>(ms.getPeakList());
                    Collections.sort(tmpPeaks, Peak.AscendingMzComparator);
                    assertEquals(tmpPeaks.get(37).getMz(), 436.441101, 0.01);
                    assertEquals(tmpPeaks.get(54).getMz(), 581.136267, 0.01);
                    assertEquals(tmpPeaks.get(83).getMz(),871.1807631, 0.01);

                    RemovePrecursorRelatedPeaks instance = new RemovePrecursorRelatedPeaks(ms, 0.5);
                    instance.removePrecursor();
                    assertEquals(81, ms.getPeakList().size());

                    ArrayList<Peak> actualPeaks = new ArrayList<Peak>(ms.getPeakList());
                    Collections.sort(actualPeaks, Peak.AscendingMzComparator);
                    assertEquals(actualPeaks.get(37).getMz(), 437.388, 0.01);
                    assertEquals(actualPeaks.get(53).getMz(), 599.5991, 0.01);
                    assertEquals(actualPeaks.get(actualPeaks.size() - 1).getMz(), 849.78491, 0.01);
                }
            }
        }
    }

}
