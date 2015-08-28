/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter;

import bookChapter.experimental.Identifications;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public class GetSpecAnDID {

    public static HashMap<String, Identifications> getSpecAndIDs(File f, double pep, boolean isModified) throws FileNotFoundException, IOException {
        HashMap<String, Identifications> specAnDIDs = new HashMap<String, Identifications>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("modXpeptide") && !line.startsWith("sequence")) {
                String[] split = line.split("\t");
                if (split.length == 59) {
                    String specTitle = split[2],
                            rawPeptideIdentification = split[0],
                            identification = split[57],
                            scoreStr = split[54],
                            tmpPEPstr = split[56],
                            chargeStr = split[1],
                            protein = split[58];
                    double tmpPep = Double.parseDouble(tmpPEPstr),
                            score = Double.parseDouble(scoreStr);
                    int charge = Integer.parseInt(chargeStr);
                    identification = identification.substring(identification.indexOf(".") + 1, identification.lastIndexOf("."));
                    Identifications id = new Identifications(specTitle, identification, protein, score, tmpPep, charge, rawPeptideIdentification);

                    if (tmpPep <= pep && id.isIsModified() == isModified) {
                        if (specAnDIDs.containsKey(specTitle)) {
                            if (id.getScore() > specAnDIDs.get(specTitle).getScore()) {
                                specAnDIDs.remove(specTitle);
                                specAnDIDs.put(specTitle, id);
                            }

                        } else {
                            specAnDIDs.put(specTitle, id);
                        }
                    }
                }
            }
        }
        return specAnDIDs;
    }

    public static HashMap<String, Identifications> getSpecAndIDs(File f, double pep) throws FileNotFoundException, IOException {
        HashMap<String, Identifications> specAnDIDs = new HashMap<String, Identifications>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("modXpeptide") && !line.startsWith("sequence")) {
                String[] split = line.split("\t");
                if (split.length == 59) {
                    String specTitle = split[2],
                            rawPeptideIdentification = split[0],
                            identification = split[57],
                            scoreStr = split[54],
                            tmpPEPstr = split[56],
                            chargeStr = split[1],
                            protein = split[58];
                    double tmpPep = Double.parseDouble(tmpPEPstr),
                            score = Double.parseDouble(scoreStr);
                    int charge = Integer.parseInt(chargeStr);
                    identification = identification.substring(identification.indexOf(".") + 1, identification.lastIndexOf("."));
                    Identifications id = new Identifications(specTitle, identification, protein, score, tmpPep, charge, rawPeptideIdentification);

                    if (tmpPep <= pep) {
                        if (specAnDIDs.containsKey(specTitle)) {
                            if (id.getScore() > specAnDIDs.get(specTitle).getScore()) {
                                specAnDIDs.remove(specTitle);
                                specAnDIDs.put(specTitle, id);
                            }

                        } else {
                            specAnDIDs.put(specTitle, id);
                        }
                    }
                }
            }
        }
        return specAnDIDs;
    }

    /**
     * This method returns spectrum with identification, if spectra were
     * assigned to UPS proteins
     *
     * @param f
     * @param pep
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static HashMap<String, Identifications> getUPSSpecAndIDs(File f, double pep, boolean doesSelectUPS) throws FileNotFoundException, IOException {
        HashMap<String, Identifications> specAnDIDs = new HashMap<String, Identifications>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("modXpeptide") && !line.startsWith("sequence")) {
                String[] split = line.split("\t");
                if (split.length == 59) {
                    String specTitle = split[2],
                            rawPeptideIdentification = split[0],
                            identification = split[57],
                            scoreStr = split[54],
                            tmpPEPstr = split[56],
                            chargeStr = split[1],
                            protein = split[58];
                    double tmpPep = Double.parseDouble(tmpPEPstr),
                            score = Double.parseDouble(scoreStr);
                    int charge = Integer.parseInt(chargeStr);
                    identification = identification.substring(identification.indexOf(".") + 1, identification.lastIndexOf("."));
                    Identifications id = new Identifications(specTitle, identification,protein, score, tmpPep, charge, rawPeptideIdentification);
                    // if protein has pep value of <= and assigned to UPS proteins
                    if (tmpPep <= pep && protein.contains("ups") && doesSelectUPS) {
                        if (specAnDIDs.containsKey(specTitle)) {
                            if (id.getScore() > specAnDIDs.get(specTitle).getScore()) {
                                specAnDIDs.remove(specTitle);
                                specAnDIDs.put(specTitle, id);
                            }
                        } else {
                            specAnDIDs.put(specTitle, id);
                        }
                        // if a validated-peptide was not derived from UPS proteins
                    } 
                    if (tmpPep <= pep && !protein.contains("ups") && !protein.contains("SHUFFLED") && !doesSelectUPS) {
                        if (specAnDIDs.containsKey(specTitle)) {
                            if (id.getScore() > specAnDIDs.get(specTitle).getScore()) {
                                specAnDIDs.remove(specTitle);
                                specAnDIDs.put(specTitle, id);
                            }
                        } else {
                            specAnDIDs.put(specTitle, id);
                        }
                    }
                }
            }
        }
        return specAnDIDs;
    }

}
