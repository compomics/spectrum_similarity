/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package preprocess.transformation.methods;

/**
 * The transformations are applied to intensities are:
 * 
 * LOG: Logarithmic transformation of intensities with 10 log-base
 * LOG_2: Logarithmic transformation of intensities with 2 log-base
 * SQR_ROOT: Square root transformation of intensities
 * RECIPROCAL: Transformation via division of intensities by 1 (1/intensity)
 * 
 * @author Sule
 */
public enum Transformations {
    LOG,
    LOG_2,
    SQR_ROOT,
    RECIPROCAL    
}
