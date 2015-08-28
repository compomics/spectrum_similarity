/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cal.cumulativeBinomialProbability.score;

import preprocess.filter.noise.implementation.DivideAndTopNFilter;
import preprocess.filter.noise.implementation.Filter;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class FilterTest extends TestCase {
    
    public FilterTest(String testName) {
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
     * Test of getExpMSnSpectrum method, of class Filter.
     */
    public void testGetExpMSnSpectrum() {
        System.out.println("getExpMSnSpectrum");
        Filter instance = null;
        MSnSpectrum expResult = null;
        Spectrum result = instance.getExpMSnSpectrum();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setExpMSnSpectrum method, of class Filter.
     */
    public void testSetExpMSnSpectrum() {
        System.out.println("setExpMSnSpectrum");
        MSnSpectrum expMSnSpectrum = null;
        Filter instance = null;
        instance.setExpMSnSpectrum(expMSnSpectrum);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getFilteredPeaks method, of class Filter.
     */
    public void testGetFilteredPeaks() throws IOException, FileNotFoundException, ClassNotFoundException, MzMLUnmarshallerException {
        System.out.println("getFilteredPeaks");
        System.out.println("getFinalCPeaks");
        String expMGFFolder = "C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides/Data\\Test\\matching/";
        MSnSpectrum ms = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().endsWith("filtering.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    System.out.println(title2);
                    ms = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);

                }
            }
        }     
        Filter instance = new DivideAndTopNFilter(ms, 3);
        ArrayList<Peak> result = instance.getFilteredPeaks();
        assertEquals(9, result.size());
        assertEquals(150.40, result.get(2).getMz());
        assertEquals(160.60, result.get(1).getMz());
        assertEquals(199.20, result.get(0).getMz());
        assertEquals(210.40, result.get(5).getMz());
        assertEquals(220.40, result.get(4).getMz());
        assertEquals(280.70, result.get(3).getMz());
        assertEquals(300.00, result.get(7).getMz());
        assertEquals(310.00, result.get(6).getMz());
        assertEquals(370.00, result.get(8).getMz());
        
        
        instance = new DivideAndTopNFilter(ms, 2);
        result = instance.getFilteredPeaks();
        assertEquals(6, result.size());
        assertEquals(160.60, result.get(1).getMz());
        assertEquals(199.20, result.get(0).getMz());
        assertEquals(220.40, result.get(3).getMz());
        assertEquals(280.70, result.get(2).getMz());
        assertEquals(300.00, result.get(5).getMz());
        assertEquals(310.00, result.get(4).getMz());
    }
}
