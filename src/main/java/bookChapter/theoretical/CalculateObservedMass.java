/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bookChapter.theoretical;

import com.compomics.util.experiment.biology.ions.ElementaryIon;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;

/**
 *
 * @author Sule
 */
public class CalculateObservedMass {
    
    /**
     * To return (M+H)+ mass from given spectrum
     * It is assumed that given spectrum containing only one charge...
     * Based on this charge value, (M+H)+ is calculated
     * 
     * @param ms
     * @return 
     */
    public static double calculateMass(MSnSpectrum ms){       
        double precursor_mz = ms.getPrecursor().getMz();
        int chargeValue = ms.getPrecursor().getPossibleCharges().get(0).value;        
        double multipliedMZ = precursor_mz * chargeValue,
                theoProton = ElementaryIon.proton.getTheoreticMass(),
                multipliedProton = chargeValue * theoProton,
                mass = multipliedMZ - multipliedProton + theoProton;
        
        return mass;
    }
    
}
