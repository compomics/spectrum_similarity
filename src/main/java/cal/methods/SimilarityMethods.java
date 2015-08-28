/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.methods;

/**
 *
 * @author Sule
 */
public enum SimilarityMethods {   
   
    DOT_PRODUCT, // dot product score without any weights on m/z values. 
    NORMALIZED_DOT_PRODUCT_STANDARD,     // normalized-dot product score without any weights on m/z values. 
    NORMALIZED_DOT_PRODUCT_HORAI,        // weighted normalized-dot product score, weight of peak intensity=0.5 and weight of peak mz =2
    NORMALIZED_DOT_PRODUCT_ZHANG,        // weighted normalized-dot product score, weight of peak intensity=0.53 and weight of peak mz=1.3
    NORMALIZED_DOT_PRODUCT_SOKOLOW,      // weighted normalized-dot product score, weight of peak intensity=0.5 and weight of peak mz =1
    NORMALIZED_DOT_PRODUCT_USER_DEFINED, // weighted normalized-dot product score, weight of peak intensity=x and peak mz=y are determined by user
    
    // TO DO: Check these Squared error formulas! 
    MEAN_SQUARED_ERROR,       // [Mean  (Sum((intensityPeak1 - intensityPeak2)^2)) ] Math.abs(mz(Peak1)- mz(Peak2)) <fragmentTolerance
    MEDIAN_SQUARED_ERROR,     // [Median(Sum((intensityPeak1 - intensityPeak2)^2)) ] Math.abs(mz(Peak1)- mz(Peak2)) <fragmentTolerance
    ROOT_MEAN_SQUARE_ERROR,   // ([Mean  (Sum((intensityPeak1 - intensityPeak2)^2)) ])^(1/2)
    ROOT_MEDIAN_SQUARE_ERROR, // ([Median  (Sum((intensityPeak1 - intensityPeak2)^2)) ])^(1/2)
    
    
    PEARSONS_CORRELATION,      
    SPEARMANS_CORRELATION,
    MSRobin, // CUMULATIVE_BIONOMINAL_PROBABILITY derived scoring function on spectra comparison
    SLIDING_DOT_PRODUCT;      // SEQUEST-LIKE SCORING! 
}
