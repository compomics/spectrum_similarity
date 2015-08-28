/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spectra.preprocess.filter.precursor;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.File;
import java.io.IOException;
import junit.framework.TestCase;
import preprocess.filter.precursor.RemoveWindowAround;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 *
 * @author Sule
 */
public class RemoveWindowAroundTest extends TestCase {

    public RemoveWindowAroundTest(String testName) {
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
     * Test of removePrecursor method, of class RemoveWindowAround.
     */
    public void testRemovePrecursor() throws IOException, MzMLUnmarshallerException {
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
        System.out.println("removePrecursor");
        RemoveWindowAround instance = new RemoveWindowAround(ms, 0.5);
        instance.removePrecursor();        
        assertEquals(229, ms.getPeakMap().size());
    }

}
