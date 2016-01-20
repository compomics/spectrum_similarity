/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectra.preprocess.filter.noise.implementation;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import junit.framework.TestCase;
import preprocess.filter.noise.implementation.DivideAndNormalize;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class DivideAndNormalizeTest extends TestCase {

    public DivideAndNormalizeTest(String testName) {
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
     * Test of getIntervals method, of class DivideAndNormalize.
     */
    public void testGetIntervals() throws IOException, MzMLUnmarshallerException {
        MSnSpectrum ms = null;
        String mgfFileName = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/TEST_BESTANDROMEDA_FORSEQUEST.mgf";
        SpectrumFactory fct = SpectrumFactory.getInstance();
        File f = new File(mgfFileName);
        if (mgfFileName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                System.out.println(title);
                ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);

            }
        }
        int intervals = 10;
        double normalize_value = 50;
        DivideAndNormalize instance = new DivideAndNormalize(ms, intervals, normalize_value);

        System.out.println("getIntervals");
        int expResult = 10;
        int result = instance.getIntervals();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNormalize_value method, of class DivideAndNormalize.
     */
    public void testGetNormalize_value() throws IOException, MzMLUnmarshallerException {

        MSnSpectrum ms = null;
        String mgfFileName = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/TEST_BESTANDROMEDA_FORSEQUEST.mgf";
        SpectrumFactory fct = SpectrumFactory.getInstance();
        File f = new File(mgfFileName);
        if (mgfFileName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                System.out.println(title);
                ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);

            }
        }
        int intervals = 10;
        double normalize_value = 50;
        DivideAndNormalize instance = new DivideAndNormalize(ms, intervals, normalize_value);

        System.out.println("getNormalize_value");
        double expResult = 50.0;
        double result = instance.getNormalize_value();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of process method, of class DivideAndNormalize.
     */
    public void testProcess() throws IOException, MzMLUnmarshallerException {

        MSnSpectrum ms = null;
        String mgfFileName = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/TEST_BESTANDROMEDA_FORSEQUEST.mgf";
        SpectrumFactory fct = SpectrumFactory.getInstance();
        File f = new File(mgfFileName);
        if (mgfFileName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                System.out.println(title);
                ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);

            }
        }
        System.out.println("process");
        int intervals = 10;
        double normalize_value = 50;
        DivideAndNormalize instance = new DivideAndNormalize(ms, intervals, normalize_value);
        instance.process();
        ArrayList<Peak> filteredPeaks = instance.getFilteredPeaks();
        Collections.sort(filteredPeaks, Peak.AscendingMzComparator);
        for (Peak p : filteredPeaks) {
            System.out.println(p.mz + "\t" + p.intensity);
        }

        assertEquals(230, instance.getFilteredPeaks().size());
        assertEquals(10.35374064, instance.getFilteredPeaks().get(0).intensity, 0.01);
        assertEquals(2.6534, instance.getFilteredPeaks().get(1).intensity, 0.01);
        assertEquals(20.953, instance.getFilteredPeaks().get(2).intensity, 0.01);
        assertEquals(5.443, instance.getFilteredPeaks().get(3).intensity, 0.01);
        assertEquals(1.326, instance.getFilteredPeaks().get(4).intensity, 0.01);
        assertEquals(0.210866598, instance.getFilteredPeaks().get(58).intensity, 0.01);
        assertEquals(0.24175793, instance.getFilteredPeaks().get(35).intensity, 0.01);
        assertEquals(3.340702851, instance.getFilteredPeaks().get(99).intensity, 0.01);
        assertEquals(5.223916995, instance.getFilteredPeaks().get(183).intensity, 0.01);
        assertEquals(0.070, instance.getFilteredPeaks().get(197).intensity, 0.01);
        assertEquals(7.0549, instance.getFilteredPeaks().get(209).intensity, 0.01);
        assertEquals(50, instance.getFilteredPeaks().get(229).intensity, 0.01);
    }

}
