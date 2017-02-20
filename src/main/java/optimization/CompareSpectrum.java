/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Genet and Sule
 */
public class CompareSpectrum {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        final ExecutorService executor = Executors.newFixedThreadPool(2);

        // get inputs for data 
        String path_folderA = "C:\\Users\\Sule\\Desktop\\mgfs\\yeast-s48",
                path_folderB = "C:\\Users\\Sule\\Desktop\\mgfs\\s-48";
        File folderA = new File(path_folderA),
                folderB = new File(path_folderB),
                toPrint = new File("C:\\Users\\Sule\\Desktop\\mgfs\\test\\result-precTol.txt");
        for (File tmpA : folderA.listFiles()) {
            if (tmpA.getName().endsWith(".mgf")) {
                for (File tmpB : folderB.listFiles()) {
                    DequeNCompare writer = new DequeNCompare(tmpA.getAbsolutePath(), tmpB.getAbsolutePath(), toPrint, 0.5, 10, true, 0);
                    executor.execute(writer);
                }
            }
        }
        executor.shutdown();
    }

}
