/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package protein_spectrum_diversity;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

/**
 * This class holds information from CL-input file
 * 
 * @author Sule
 */
public class CLEntry {

    private int spectrum_id,
            charge;
    private String modification,
            peptide_sequence,
            protein;
    private MSnSpectrum msms;

    public CLEntry(String line) {
        String[] sp = line.split(" ");
        spectrum_id = Integer.parseInt(sp[0]);
        charge = Integer.parseInt(sp[1]);
        modification = sp[2];
        peptide_sequence = sp[3];
        protein = sp[4].replace("\"", "");
        protein = protein.replace("|", "_");
    }

    public MSnSpectrum getMsms() {
        return msms;
    }

    public void setMsms(MSnSpectrum msms) {
        this.msms = msms;
    }      

    public int getSpectrum_id() {
        return spectrum_id;
    }

    public void setSpectrum_id(int spectrum_id) {
        this.spectrum_id = spectrum_id;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public String getModification() {
        return modification;
    }

    public void setModification(String modification) {
        this.modification = modification;
    }

    public String getPeptide_sequence() {
        return peptide_sequence;
    }

    public void setPeptide_sequence(String peptide_sequence) {
        this.peptide_sequence = peptide_sequence;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.spectrum_id;
        hash = 97 * hash + this.charge;
        hash = 97 * hash + (this.modification != null ? this.modification.hashCode() : 0);
        hash = 97 * hash + (this.peptide_sequence != null ? this.peptide_sequence.hashCode() : 0);
        hash = 97 * hash + (this.protein != null ? this.protein.hashCode() : 0);
        hash = 97 * hash + (this.msms != null ? this.msms.hashCode() : 0);
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
        final CLEntry other = (CLEntry) obj;
        if (this.spectrum_id != other.spectrum_id) {
            return false;
        }
        if (this.charge != other.charge) {
            return false;
        }
        if ((this.modification == null) ? (other.modification != null) : !this.modification.equals(other.modification)) {
            return false;
        }
        if ((this.peptide_sequence == null) ? (other.peptide_sequence != null) : !this.peptide_sequence.equals(other.peptide_sequence)) {
            return false;
        }
        if ((this.protein == null) ? (other.protein != null) : !this.protein.equals(other.protein)) {
            return false;
        }
        if (this.msms != other.msms && (this.msms == null || !this.msms.equals(other.msms))) {
            return false;
        }
        return true;
    }      

}
