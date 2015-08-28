/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bookChapter.experimental;

import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

/**
 *
 * @author Sule
 */
public class IDResult {
    private MSnSpectrum specA,
            specB;
    private String identification;    
    private double score;

    public IDResult(MSnSpectrum specA, MSnSpectrum specB, String identification, double score) {
        this.specA = specA;
        this.specB = specB;
        this.identification = identification;
        this.score = score;
    }

    public MSnSpectrum getSpecA() {
        return specA;
    }

    public void setSpecA(MSnSpectrum specA) {
        this.specA = specA;
    }

    public MSnSpectrum getSpecB() {
        return specB;
    }

    public void setSpecB(MSnSpectrum specB) {
        this.specB = specB;
    }

    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

}
