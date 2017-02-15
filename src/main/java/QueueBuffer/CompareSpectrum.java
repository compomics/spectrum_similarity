/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueueBuffer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Genet
 */
public class CompareSpectrum {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        String filename1="C:/testfile/spectra3.txt";
        String filename2="C:/testfile/spectra2.txt";

     
        final ExecutorService executor = Executors.newFixedThreadPool(2);       
              
        DequeNCompare writer = new DequeNCompare(filename1, filename2);

        
        executor.execute(writer);
        executor.shutdown();
    }
    
}
