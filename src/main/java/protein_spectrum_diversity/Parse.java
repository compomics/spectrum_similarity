/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protein_spectrum_diversity;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.gui.waiting.waitinghandlers.WaitingHandlerCLIImpl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class finds individual mgf files in children folders of given rootFolder
 * 
 * @author Sule
 */
public class Parse {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, MzMLUnmarshallerException {       
        File rootFolder = new File(args[0]),
                outputFolder = new File(args[1]),
                info = new File(args[2]);

        SpectrumFactory fct = SpectrumFactory.getInstance();

        Path path = Paths.get(rootFolder.getAbsolutePath());
        final List<Path> files = new ArrayList<Path>();
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (!attrs.isDirectory()) {
                        files.add(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        // to split by file separator
        String pattern = Pattern.quote(System.getProperty("file.separator"));
        BufferedWriter bw = null;
        BufferedWriter bw_all = new BufferedWriter(new FileWriter(info));
        for (Path p : files) {
            String mgfName = p.toAbsolutePath().toString();
            if (mgfName.endsWith(".mgf")) {
                File mgfFile = new File(mgfName);
                fct.addSpectra(mgfFile, new WaitingHandlerCLIImpl());

                String[] splittedFileName = mgfName.split(pattern);

                String spectrumTitle = splittedFileName[(splittedFileName.length - 1)];
                spectrumTitle = "H-" + spectrumTitle.substring(0, spectrumTitle.indexOf(".mgf")) + "-OH";
                bw = new BufferedWriter(new FileWriter(outputFolder + "//" + spectrumTitle + ".mgf"));

                System.out.println(spectrumTitle +"\t"+mgfName);

                MSnSpectrum spectrum = (MSnSpectrum) fct.getSpectrum(mgfFile.getName(), spectrumTitle);
                bw_all.write(spectrum.getSpectrumTitle() + "\t" + spectrum.getPrecursor().getPossibleChargesAsString() + "\t" + spectrumTitle + "\n");
                bw.write(spectrum.asMgf());
                bw.close();
                fct.clearFactory();
            }
        }
        bw_all.close();
    }

}
