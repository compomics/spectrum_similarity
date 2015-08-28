/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bookChapter.theoretical;

import com.compomics.util.experiment.massspectrometry.Peak;

/**
 *
 * @author Sule
 */
public class TheoreticalPeak extends Peak {    
    private int charge;
    private String name;

    public TheoreticalPeak(double mz, double intensity, int charge, String name) {
        super(mz, intensity);
        this.charge = charge;
        this.name = name;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.mz) ^ (Double.doubleToLongBits(this.mz) >>> 32));
        hash = 89 * hash + (int) (Double.doubleToLongBits(this.intensity) ^ (Double.doubleToLongBits(this.intensity) >>> 32));
        hash = 89 * hash + this.charge;
        hash = 89 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final TheoreticalPeak other = (TheoreticalPeak) obj;
        if (Double.doubleToLongBits(this.mz) != Double.doubleToLongBits(other.mz)) {
            return false;
        }
        if (Double.doubleToLongBits(this.intensity) != Double.doubleToLongBits(other.intensity)) {
            return false;
        }
        if (this.charge != other.charge) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
    
    
            
    
}
