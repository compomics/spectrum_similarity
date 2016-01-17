/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess.transformation.interfaces;

import preprocess.transformation.methods.Transformations;

/**
 * This interface shows a method used to transform intensities on a spectrum. 
 * 
 * @author Sule
 */
public interface TransformIntensity {
    
    
    /**
     * This method transforms intensities on a spectrum 

     * @param tr an enum Object of Transformations
     */
    public void transform(Transformations tr);
    
}
