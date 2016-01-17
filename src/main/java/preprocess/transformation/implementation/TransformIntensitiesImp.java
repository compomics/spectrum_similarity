/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package preprocess.transformation.implementation;

import preprocess.transformation.methods.Transformations;
import preprocess.transformation.interfaces.TransformIntensity;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.math.BasicMathFunctions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class transforms intensities on MS2 spectrum according to a
 * transformation mode in selection
 *
 * @author Sule
 */
public class TransformIntensitiesImp implements TransformIntensity {

    private Transformations transformation; // Trasnformation type (4 options: LOG10, LOG2, RECIPROCAL, SQR_ROOT)
    private MSnSpectrum spec_org, // The original MS2 spectrum (just to get intensities, it remains) 
            spec_tr; // An MS spectrum with transformed intensities according to transformation type  (MS2) 
    private HashMap<Double, Peak> tr_peaks; // transformed peaks
    private int log_base; // the default is 10 but if LOG2 is selected, then it equals to 2. 
    private boolean is_spec_transformed = false; // a boolean statement to control transformation. 

    /**
     * This constructs generates object to transform intensities of a spectrum
     *
     * @param transformation is a Transformation enum object.
     * @param spectrum is an MSnSpectrum object with intensities and mz..
     */
    public TransformIntensitiesImp(Transformations transformation, MSnSpectrum spectrum) {
        this.transformation = transformation;
        this.spec_org = spectrum;
        tr_peaks = new HashMap<Double, Peak>();
        log_base = 10; // if it is log10..
        // If it is asked log2, base is changed to 2..
        if (transformation.equals(Transformations.LOG_2)) {
            log_base = 2;
        }
    }

    /**
     * This method makes transformation on intensities based on 3 options and
     * then constructs a new spectrum. This method only works if
     * is_spec_transformed is set to FALSE!
     *
     * It prepares a list of peaks which are already transformed (get Then
     * construct a MSnSpectrum (MS2) object with transformed intensities from
     * original MSnSpectrum
     *
     * @param tr is Transformation enum
     */
    @Override
    public void transform(Transformations tr) {
        if (is_spec_transformed == false) {
            HashMap<Double, Peak> peakMap = new HashMap<Double, Peak>();
            switch (tr) {
                case LOG_2:
                    peakMap = transform_log(2);
                    break;
                case LOG:
                    peakMap = transform_log(10);
                    break;
                case SQR_ROOT:
                    peakMap = transform_sqr_root();
                    break;
                case RECIPROCAL:
                    peakMap = transform_reciprocal();
                    break;
                default:
                    throw new AssertionError(tr.name());
            }
            spec_tr = generate_ms(peakMap);
            tr_peaks = (HashMap<Double, Peak>) Sort.sortByKey(peakMap);
            is_spec_transformed = true;
        }
    }

    /* Accessors and mutators */
    public Transformations getTransformation() {
        return transformation;
    }

    public void setTransformation(Transformations transformation) {
        setIs_spec_transformed(false);
        this.transformation = transformation;
        if(transformation.equals(Transformations.LOG_2)){
            setLog_base(2);
        } else{
            setLog_base(10);
        }
    }

    public MSnSpectrum getSpec_org() {
        return spec_org;
    }

  
    public MSnSpectrum getSpec_tr() {
        if (is_spec_transformed== false){
            transform(transformation);
        }
        return spec_tr;
    }

    public HashMap<Double, Peak> getTr_peaks() {
        return tr_peaks;
    }

    public int getLog_base() {
        return log_base;
    }

    /**
     * It makes sure that log base is either 2 or 10 (default=10)
     * @param log_base 
     */
    public void setLog_base(int log_base) {
        setIs_spec_transformed(false);
        if(log_base == 2){
            this.log_base = 2;
        } else {
            this.log_base = 10;
        }
    }

    public boolean is_spec_transformed() {
        return is_spec_transformed;
    }

    public void setIs_spec_transformed(boolean is_spec_transformed) {
        this.is_spec_transformed = is_spec_transformed;
    }

    /**
     * This method transforms intensities into log scale with given log_base.
     * Then, it return a peaklist with transformed intensities (HashMap<Double,
     * Peak>)
     *
     * @param log_base
     * @return
     */
    private HashMap<Double, Peak> transform_log(int log_base) {
        HashMap<Double, Peak> peakMap = new HashMap<Double, Peak>();
        for (Peak peak : spec_org.getPeakList()) {
            double intensity = peak.intensity,
                    mz = peak.mz,
                    log_intensity = 0;
            if (log_base == 0) {
                log_intensity = intensity;
            } else {
                log_intensity = BasicMathFunctions.log(intensity, log_base);
            }
            Peak p = new Peak(mz, log_intensity);
            peakMap.put(mz, p);
        }
        peakMap = (HashMap<Double, Peak>) Sort.sortByKey(peakMap);
        return peakMap;
    }

    /**
     * This method transforms intensities via square root. Then, it return
     *
     * @param log_base
     * @return a peaklist with transformed intensities (HashMap<Double, Peak>)
     */
    private HashMap<Double, Peak> transform_sqr_root() {
        HashMap<Double, Peak> peakMap = new HashMap<Double, Peak>();
        for (Peak peak : spec_org.getPeakList()) {
            double intensity = peak.intensity,
                    mz = peak.mz,
                    sqr_root_intensity = Math.sqrt(intensity);
            Peak p = new Peak(mz, sqr_root_intensity);
            peakMap.put(mz, p);
        }
        peakMap = (HashMap<Double, Peak>) Sort.sortByKey(peakMap);
        return peakMap;
    }

    /**
     * This method transforms intensities by reciprocally
     *
     * @return a peaklist with transformed intensities (HashMap<Double, Peak>)
     */
    private HashMap<Double, Peak> transform_reciprocal() {
        HashMap<Double, Peak> peakMap = new HashMap<Double, Peak>();
        for (Peak peak : spec_org.getPeakList()) {
            double intensity = peak.intensity,
                    mz = peak.mz,
                    reciprocal_intensity = (1 / (double) (intensity));
            Peak p = new Peak(mz, reciprocal_intensity);
            peakMap.put(mz, p);
        }
        peakMap = (HashMap<Double, Peak>) Sort.sortByKey(peakMap);
        return peakMap;
    }

    /**
     * This generates a new MSpectrum (MS2) with given (or already transformed)
     * peakMap
     *
     * @param peakMap (HashMap<Double, Peak>) object with mz and corresponding
     * transformed intensities
     * @return MSnSpectrum object
     */
    private MSnSpectrum generate_ms(HashMap<Double, Peak> peakMap) {
        HashMap<Double, Peak> sortByKey = (HashMap<Double, Peak>) Sort.sortByKey(peakMap);
        Collection<Peak> peaks = sortByKey.values();
        Precursor precursor = spec_org.getPrecursor();
        String spectrumTitle = spec_org.getSpectrumTitle(),
                fileName = spec_org.getFileName();
        double scanStartTime = spec_org.getScanStartTime();
        MSnSpectrum ms = new MSnSpectrum(2, precursor, spectrumTitle, sortByKey, fileName, scanStartTime);
        ms.setMzOrdered(true);
        ms.setPeaks(new ArrayList<Peak>(peaks));
        ms.setPeakList(peakMap);
        return ms;
    }

    @Override
    public String toString() {
        return "TransformIntensity{" + "transformation=" + transformation + ", spec_org=" + spec_org + ", spec_tr=" + spec_tr + ", tr_peaks=" + tr_peaks + ", log_base=" + log_base + ", is_spec_transformed=" + is_spec_transformed + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (this.spec_org != null ? this.spec_org.hashCode() : 0);
        hash = 67 * hash + (this.spec_tr != null ? this.spec_tr.hashCode() : 0);
        hash = 67 * hash + (this.tr_peaks != null ? this.tr_peaks.hashCode() : 0);
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
        final TransformIntensitiesImp other = (TransformIntensitiesImp) obj;
        if (this.spec_org != other.spec_org && (this.spec_org == null || !this.spec_org.equals(other.spec_org))) {
            return false;
        }
        if (this.spec_tr != other.spec_tr && (this.spec_tr == null || !this.spec_tr.equals(other.spec_tr))) {
            return false;
        }
        if (this.tr_peaks != other.tr_peaks && (this.tr_peaks == null || !this.tr_peaks.equals(other.tr_peaks))) {
            return false;
        }
        return true;
    }

    /**
     * This class sorts a Map object either by value or key
     *
     * @author Sule
     */
    public static class Sort {

        /**
         * This method sorts a map by values
         *
         * @param map is the one in study
         * @return a map with sorted values(Desc)
         */
        public static Map sortByValue(Map map) {
            List list = new LinkedList(map.entrySet());
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
                }
            });
            Map result = new LinkedHashMap();
            for (Iterator it = list.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }

        public static Map sortByKey(Map map) {
            List list = new LinkedList(map.entrySet());
            Collections.sort(list, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Comparable) ((Map.Entry) (o1)).getKey()).compareTo(((Map.Entry) (o2)).getKey());
                }
            });
            Map result = new LinkedHashMap();
            for (Iterator it = list.iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                result.put(entry.getKey(), entry.getValue());
            }
            return result;
        }
    }

}
