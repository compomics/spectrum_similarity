/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bookChapter.theoretical;

import com.compomics.util.experiment.biology.Peptide;

/**
 *
 * @author Sule
 */
public class DBEntry {
    
    private Peptide peptide;
    private String proteinDescription,
            proteinAccession;
    private double peptideMass;

    public DBEntry(Peptide peptide, String proteinDescription, String proteinAccession, double peptideMass) {
        this.peptide = peptide;
        this.proteinDescription = proteinDescription;
        this.proteinAccession = proteinAccession;
        this.peptideMass = peptideMass;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
    }

    public String getProteinDescription() {
        return proteinDescription;
    }

    public void setProteinDescription(String proteinDescription) {
        this.proteinDescription = proteinDescription;
    }

    public String getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(String proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public double getPeptideMass() {
        return peptideMass;
    }

    public void setPeptideMass(double peptideMass) {
        this.peptideMass = peptideMass;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + (this.peptide != null ? this.peptide.hashCode() : 0);
        hash = 37 * hash + (this.proteinDescription != null ? this.proteinDescription.hashCode() : 0);
        hash = 37 * hash + (this.proteinAccession != null ? this.proteinAccession.hashCode() : 0);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.peptideMass) ^ (Double.doubleToLongBits(this.peptideMass) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DBEntry other = (DBEntry) obj;
        if (this.peptide != other.peptide && (this.peptide == null || !this.peptide.equals(other.peptide))) {
            return false;
        }
        if ((this.proteinDescription == null) ? (other.proteinDescription != null) : !this.proteinDescription.equals(other.proteinDescription)) {
            return false;
        }
        if ((this.proteinAccession == null) ? (other.proteinAccession != null) : !this.proteinAccession.equals(other.proteinAccession)) {
            return false;
        }
        if (Double.doubleToLongBits(this.peptideMass) != Double.doubleToLongBits(other.peptideMass)) {
            return false;
        }
        return true;
    }
    
    
    
}
