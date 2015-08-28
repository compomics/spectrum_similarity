/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.binBased;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class BinMSnSpectrumTest extends TestCase {

    public BinMSnSpectrumTest(String testName) {
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
     * Test of construct_bin_spectrum method, of class BinMSnSpectrum.
     */
    public void testConstruct_bin_spectrum() throws IOException, MzMLUnmarshallerException {
        MSnSpectrum ms = null;
        String mgfFileName = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/BIN_TEST.mgf";
        SpectrumFactory fct = SpectrumFactory.getInstance();
        File f = new File(mgfFileName);
        if (mgfFileName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                System.out.println(title);
                ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);
            }
        }
        double min_value = 157.1002197,
                max_value = 163.0133,
                fragment_tolerance = 0.5;

        System.out.println("construct_bin_spectrum");
        BinMSnSpectrum instance = new BinMSnSpectrum(ms, min_value, max_value, fragment_tolerance, 0);
        instance.construct_bin_spectrum();
        assertEquals(6, instance.bin_spectrum.length);
        assertEquals(6, instance.getBin_spectrum().length);

        assertEquals(119.9387, instance.bin_spectrum[0], 0.01);
        assertEquals(243.4012833, instance.bin_spectrum[1], 0.01);
        assertEquals(12.228, instance.bin_spectrum[2], 0.01);
        assertEquals(95.61695862, instance.bin_spectrum[3], 0.01);
        assertEquals(461.047, instance.bin_spectrum[4], 0.01);
        assertEquals(129.4687, instance.bin_spectrum[5], 0.01);

        min_value -= 75;
        max_value += 75;
        instance = new BinMSnSpectrum(ms, min_value, max_value, fragment_tolerance, 0);
        instance.construct_bin_spectrum();
        assertEquals(156, instance.bin_spectrum.length);
        assertEquals(156, instance.getBin_spectrum().length);

        assertEquals(119.9387, instance.bin_spectrum[75], 0.01);
        assertEquals(243.4012833, instance.bin_spectrum[76], 0.01);
        assertEquals(12.228, instance.bin_spectrum[77], 0.01);
        assertEquals(95.61695862, instance.bin_spectrum[78], 0.01);
        assertEquals(461.047, instance.bin_spectrum[79], 0.01);
        assertEquals(129.4687, instance.bin_spectrum[80], 0.01);

    }

    /**
     * Test of getBin_spectrum method, of class BinMSnSpectrum.
     */
    public void testGetBin_spectrum() throws IOException, MzMLUnmarshallerException {
        System.out.println("getBin_spectrum");

        MSnSpectrum ms = null;
        String mgfFileName = "C:\\Users\\Sule\\Desktop\\BOOK_CHAPTER\\mgf/BIN_TEST.mgf";
        SpectrumFactory fct = SpectrumFactory.getInstance();
        File f = new File(mgfFileName);
        if (mgfFileName.endsWith(".mgf")) {
            fct.addSpectra(f, new WaitingHandlerCLIImpl());
            for (String title : fct.getSpectrumTitles(f.getName())) {
                System.out.println(title);
                ms = (MSnSpectrum) fct.getSpectrum(f.getName(), title);
            }
        }
        double min_value = 157.1002197 - 75,
                max_value = 163.0133 + 75,
                fragment_tolerance = 0.5;

        System.out.println("construct_bin_spectrum");
        BinMSnSpectrum instance = new BinMSnSpectrum(ms, min_value, max_value, fragment_tolerance, 0);

        int shift = 1;
        double[] result = instance.getBin_spectrum(shift);

        assertEquals(156, result.length);

        assertEquals(119.9387, result[76], 0.01);
        assertEquals(243.4012833, result[77], 0.01);
        assertEquals(12.228, result[78], 0.01);
        assertEquals(95.61695862, result[79], 0.01);
        assertEquals(461.047, result[80], 0.01);
        assertEquals(129.4687, result[81], 0.01);

        shift = -1;
        result = instance.getBin_spectrum(shift);

        assertEquals(156, result.length);

        assertEquals(119.9387, result[74], 0.01);
        assertEquals(243.4012833, result[75], 0.01);
        assertEquals(12.228, result[76], 0.01);
        assertEquals(95.61695862, result[77], 0.01);
        assertEquals(461.047, result[78], 0.01);
        assertEquals(129.4687, result[79], 0.01);

        shift = -11;
        result = instance.getBin_spectrum(shift);

        assertEquals(156, result.length);

        assertEquals(119.9387, result[64], 0.01);
        assertEquals(243.4012833, result[65], 0.01);
        assertEquals(12.228, result[66], 0.01);
        assertEquals(95.61695862, result[67], 0.01);
        assertEquals(461.047, result[68], 0.01);
        assertEquals(129.4687, result[69], 0.01);
    }

}
