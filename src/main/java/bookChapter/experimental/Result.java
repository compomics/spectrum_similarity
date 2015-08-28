/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookChapter.experimental;

import java.util.Comparator;

/**
 *
 * @author Sule
 */
public class Result {

    private String spectrum_title_A,
            spectrum_title_B,
            peptideA = "",
            peptideB = "",
            peptideInfo = "differentPeptide",
            proteinInfo = "differentProtein",
            specAChargeInfo,
            specAPrecMZInfo;
    private double dot_score,
             normalized_dot_score,
            pearson,
            spearman,
            mse;

    private boolean isSpectrumAIdentified = false,
            isSpectrumBIdentified = false,
            isValidated = false,
            isSame = false;

    public Result(String spectrum_title_A,
            String spectrum_title_B,
            String peptideA,
            String peptideB,
            String specAChargeInfo,
            String specAPrecMZInfo,
            double dot_score, double pearson, double spearman, double mse) {
        this.peptideA = peptideA;
        this.peptideB = peptideB;
        this.spectrum_title_A = spectrum_title_A;
        this.spectrum_title_B = spectrum_title_B;
        this.specAChargeInfo = specAChargeInfo;
        this.specAPrecMZInfo = specAPrecMZInfo;
        this.dot_score = dot_score;
        this.pearson = pearson;
        this.spearman = spearman;
        this.mse = mse;
    }
    
    
    public Result(String spectrum_title_A,
            String spectrum_title_B,
            String peptideA,
            String peptideB,
            String specAChargeInfo,
            String specAPrecMZInfo,
            double dot_score, double normalized_dot_score, double pearson, double spearman, double mse) {
        this.peptideA = peptideA;
        this.peptideB = peptideB;
        this.spectrum_title_A = spectrum_title_A;
        this.spectrum_title_B = spectrum_title_B;
        this.specAChargeInfo = specAChargeInfo;
        this.specAPrecMZInfo = specAPrecMZInfo;
        this.dot_score = dot_score;
        this.pearson = pearson;
        this.spearman = spearman;
        this.mse = mse;
        this.normalized_dot_score= normalized_dot_score;
    }

    public double getNormalized_dot_score() {
        return normalized_dot_score;
    }

    public void setNormalized_dot_score(double normalized_dot_score) {
        this.normalized_dot_score = normalized_dot_score;
    }
   
    
    public String getSpecAChargeInfo() {
        return specAChargeInfo;
    }

    public void setSpecAChargeInfo(String specAChargeInfo) {
        this.specAChargeInfo = specAChargeInfo;
    }

    public String getSpecAPrecMZInfo() {
        return specAPrecMZInfo;
    }

    public void setSpecAPrecMZInfo(String specAPrecMZInfo) {
        this.specAPrecMZInfo = specAPrecMZInfo;
    }

    public String getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(String peptideA) {
        this.peptideA = peptideA;
    }

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
    }

    public String getPeptideInfo() {
        return peptideInfo;
    }

    public void setPeptideInfo(String peptideInfo) {
        this.peptideInfo = peptideInfo;
    }

    public String getProteinInfo() {
        return proteinInfo;
    }

    public void setProteinInfo(String proteinInfo) {
        this.proteinInfo = proteinInfo;
    }

    public boolean isIsValidated() {
        return isValidated;
    }

    public void setIsValidated(boolean isValidated) {
        this.isValidated = isValidated;
    }

    public boolean isIsSame() {
        return isSame;
    }

    public void setIsSame(boolean isSame) {
        this.isSame = isSame;
    }

    public boolean isIsSpectrumAIdentified() {
        return isSpectrumAIdentified;
    }

    public void setIsSpectrumAIdentified(boolean isSpectrumAIdentified) {
        this.isSpectrumAIdentified = isSpectrumAIdentified;
    }

    public boolean isIsSpectrumBIdentified() {
        return isSpectrumBIdentified;
    }

    public void setIsSpectrumBIdentified(boolean isSpectrumBIdentified) {
        this.isSpectrumBIdentified = isSpectrumBIdentified;
    }

    public String getSpectrum_title_A() {
        return spectrum_title_A;
    }

    public void setSpectrum_title_A(String spectrum_title_A) {
        this.spectrum_title_A = spectrum_title_A;
    }

    public String getSpectrum_title_B() {
        return spectrum_title_B;
    }

    public void setSpectrum_title_B(String spectrum_title_B) {
        this.spectrum_title_B = spectrum_title_B;
    }

    public double getDot_score() {
        return dot_score;
    }

    public void setDot_score(double dot_score) {
        this.dot_score = dot_score;
    }

    public double getPearson() {
        return pearson;
    }

    public void setPearson(double pearson) {
        this.pearson = pearson;
    }

    public double getSpearman() {
        return spearman;
    }

    public void setSpearman(double spearman) {
        this.spearman = spearman;
    }

    public double getMse() {
        return mse;
    }

    public void setMse(double mse) {
        this.mse = mse;
    }

    
    
    public static final Comparator<Result> dotScoreASC
            = new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return o1.getDot_score() < o2.getDot_score() ? -1 : o1.getDot_score() == o2.getDot_score() ? 0 : 1;
                }
            };

    public static final Comparator<Result> pearsonASC
            = new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return o1.getPearson() < o2.getPearson() ? -1 : o1.getPearson() == o2.getPearson() ? 0 : 1;
                }
            };

    public static final Comparator<Result> spearmanASC
            = new Comparator<Result>() {
                @Override
                public int compare(Result o1, Result o2) {
                    return o1.getSpearman() < o2.getSpearman() ? -1 : o1.getSpearman() == o2.getSpearman() ? 0 : 1;
                }
            };

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.spectrum_title_A != null ? this.spectrum_title_A.hashCode() : 0);
        hash = 89 * hash + (this.spectrum_title_B != null ? this.spectrum_title_B.hashCode() : 0);
        hash = 89 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 89 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 89 * hash + (this.peptideInfo != null ? this.peptideInfo.hashCode() : 0);
        hash = 89 * hash + (this.proteinInfo != null ? this.proteinInfo.hashCode() : 0);
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.dot_score) ^ (Double.doubleToLongBits(this.dot_score) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.pearson) ^ (Double.doubleToLongBits(this.pearson) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.spearman) ^ (Double.doubleToLongBits(this.spearman) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.mse) ^ (Double.doubleToLongBits(this.mse) >>> 32));
        hash = 89 * hash + (this.isSpectrumAIdentified ? 1 : 0);
        hash = 89 * hash + (this.isSpectrumBIdentified ? 1 : 0);
        hash = 89 * hash + (this.isValidated ? 1 : 0);
        hash = 89 * hash + (this.isSame ? 1 : 0);
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
        final Result other = (Result) obj;
        if ((this.spectrum_title_A == null) ? (other.spectrum_title_A != null) : !this.spectrum_title_A.equals(other.spectrum_title_A)) {
            return false;
        }
        if ((this.spectrum_title_B == null) ? (other.spectrum_title_B != null) : !this.spectrum_title_B.equals(other.spectrum_title_B)) {
            return false;
        }
        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
            return false;
        }
        if ((this.peptideB == null) ? (other.peptideB!= null) : !this.peptideB.equals(other.peptideB)) {
            return false;
        }
        if ((this.peptideInfo == null) ? (other.peptideInfo != null) : !this.peptideInfo.equals(other.peptideInfo)) {
            return false;
        }
        if ((this.proteinInfo == null) ? (other.proteinInfo != null) : !this.proteinInfo.equals(other.proteinInfo)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dot_score) != Double.doubleToLongBits(other.dot_score)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pearson) != Double.doubleToLongBits(other.pearson)) {
            return false;
        }
        if (Double.doubleToLongBits(this.spearman) != Double.doubleToLongBits(other.spearman)) {
            return false;
        }
        if (Double.doubleToLongBits(this.mse) != Double.doubleToLongBits(other.mse)) {
            return false;
        }
        if (this.isSpectrumAIdentified != other.isSpectrumAIdentified) {
            return false;
        }
        if (this.isSpectrumBIdentified != other.isSpectrumBIdentified) {
            return false;
        }
        if (this.isValidated != other.isValidated) {
            return false;
        }
        if (this.isSame != other.isSame) {
            return false;
        }
        return true;
    }

}
