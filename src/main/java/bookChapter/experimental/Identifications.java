/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

/**
 *
 * @author Sule
 */
public class Identifications {

    private String specTitle,
            peptide,
            protein,
            rawPeptide;
    private double score,
            pep;
    private int charge;
    private boolean isModified = false;

    public Identifications(String specTitle, String peptide, String protein, double score, double pep, int charge, String rawPeptide) {
        this.specTitle = specTitle;
        this.rawPeptide=rawPeptide;
        this.peptide = peptide;
        this.protein = protein;
        this.score = score;
        this.pep = pep;
        this.charge = charge;
        // this part is specific to CPTAC data set
        if (rawPeptide.startsWith("ace")) {
            isModified = true;
        }
        if (rawPeptide.contains("moxM")) {
            isModified = true;
        }
        if (rawPeptide.contains("pyrQ")) {
            isModified = true;
        }
        if (rawPeptide.contains("cmmC")) {
            isModified = true;
        }
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }
   

    public boolean isIsModified() {
        return isModified;
    }

    public void setIsModified(boolean isModified) {
        this.isModified = isModified;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public String getSpecTitle() {
        return specTitle;
    }

    public void setSpecTitle(String specTitle) {
        this.specTitle = specTitle;
    }

    public String getPeptide() {
        return peptide;
    }

    public void setPeptide(String peptide) {
        this.peptide = peptide;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getPep() {
        return pep;
    }

    public void setPep(double pep) {
        this.pep = pep;
    }

    @Override
    public String toString() {
        return "Identifications{" + "specTitle=" + specTitle + ", peptide=" + peptide + ", rawPeptide=" + rawPeptide + ", score=" + score + ", pep=" + pep + ", charge=" + charge + ", isModified=" + isModified + '}';
    }

    

}
