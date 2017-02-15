/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QueueBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Genet
 */
public class DequeNCompare implements Runnable {

    String filename1 = "";
    String filename2 = "";

    public DequeNCompare(String f1, String f2) {
        this.filename1 = f1;
        this.filename2 = f2;
    }

    ArrayBlockingQueue<String> queue1 = new ArrayBlockingQueue<>(8192);
    ArrayBlockingQueue<String> queue2 = new ArrayBlockingQueue<>(8192);
    ArrayList spectrumList1 = new ArrayList();
    ArrayList spectrumList2 = new ArrayList();
    PrintWriter pr = null;

    public static boolean flag = true;

    @Override
    public void run() {

        try {

            String tempBuffer1;
            String tempBuffer2;

            ReaderThread reader1 = new ReaderThread(queue1, filename1);
            ReaderThread reader2 = new ReaderThread(queue2, filename2);
            pr = new PrintWriter(new File("C:/testfile/compResult.txt"));

            final ExecutorService executor = Executors.newFixedThreadPool(3);
            executor.execute(reader1);
            executor.execute(reader2);

            //Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            while (true) {

                while (!((tempBuffer1 = queue1.take()).equals("END IONS"))) {// && !((tempBuffer2 = queue1.take()).equals("EOF"))) {

                    spectrumList1.add(tempBuffer1);
                }

                while (!(tempBuffer2 = queue2.take()).equals("EOF")) {

                    if (tempBuffer2.equals("END IONS")) {
                        calculateSimilarity(spectrumList1, spectrumList2, pr);
                        spectrumList2.clear();
                    } else {
                        spectrumList2.add(tempBuffer2);
                    }

                }

                if (queue1.peek().equals("EOF")) {
                    pr.close();
                    
                    executor.shutdown();
                    break;
                } else {
                    reader2 = new ReaderThread(queue2, filename2);
                    executor.execute(reader2);

                }
                spectrumList1.clear();
                spectrumList2.clear();

            }

        } catch (InterruptedException e) {

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DequeNCompare.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void calculateSimilarity(ArrayList a, ArrayList b, PrintWriter pr) {

        boolean result = false;

        Collections.sort(a);
        Collections.sort(b);
        result = a.equals(b);

        pr.println("Size of first spectra = " + Integer.toString(a.size()) + "\n" + "Size of second spectra = " + Integer.toString(b.size()));
        pr.println("\n Similarity Result = " + Boolean.toString(result));

    }

}
