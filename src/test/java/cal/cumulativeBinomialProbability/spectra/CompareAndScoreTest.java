/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.spectra;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import junit.framework.TestCase;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class CompareAndScoreTest extends TestCase {

    public CompareAndScoreTest(String testName) {
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
     * Test of getMSRobinScore method, of class CompareAndScore.
     */
    public void testGetMSRobinScore() throws IOException, MzMLUnmarshallerException, FileNotFoundException, ClassNotFoundException, Exception {
        System.out.println("getMSRobinScore");
        String expMGFFolder = "TestingData\\Scoring/";
        MSnSpectrum specA = null,
                specB = null;

        for (File mgf : new File(expMGFFolder).listFiles()) {
            if (mgf.getName().equals("specA.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    System.out.println(title2);
                    specA = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);

                }
            } else if (mgf.getName().equals("specB.mgf")) {
                System.out.println(mgf.getName());
                SpectrumFactory fct = SpectrumFactory.getInstance();
                fct.addSpectra(mgf);
                for (String title2 : fct.getSpectrumTitles(mgf.getName())) {
                    System.out.println(title2);
                    specB = (MSnSpectrum) fct.getSpectrum(mgf.getName(), title2);
                }
            }
        }
        CompareAndScore instance = new CompareAndScore(specA, specB, 0.5, 0, 0);
        System.out.println("Result=" + instance.getMSRobinScore());
        double expResult = 0.8;
        double result = instance.getMSRobinScore();
        assertEquals(expResult, result, 0.1);

        instance = new CompareAndScore(specA, specB, 0.5, 1,0);
        expResult = 0.22;
        result = instance.getMSRobinScore();
        assertEquals(expResult, result, 0.1);

    }

}
