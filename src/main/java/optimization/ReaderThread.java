/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * @author Genet
 */
public class ReaderThread implements Runnable {

    protected ArrayBlockingQueue<String> ringBuff = null;
    String filename;

    public ReaderThread(ArrayBlockingQueue<String> blockingQueue, String filename) {
        this.ringBuff = blockingQueue;
        this.filename = filename;
    }

    @Override
    public void run() {
        BufferedReader br = null;
        try {

            br = new BufferedReader(new FileReader(new File(filename)));

            String currentLine = null;
            //Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

            while ((currentLine = br.readLine()) != null) {
                // to make sure that the title is there
                if (currentLine.contains("TITLE=")) {
                    ringBuff.put(currentLine);
                }
//                System.out.println(currentLine);
                if (Character.isDigit(currentLine.charAt(0)) || currentLine.equals("END IONS")) {
                    //spectra=buffer1.split(" ");
//                    if (ringBuff.size() > 8000) {
//                        Thread.currentThread().wait(1);
//                    }

                    ringBuff.put(currentLine);
                }

            }
            ringBuff.put("EOF");  //When end of file has been reached

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

        } finally {
            try {
                br.close();
                // br2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
