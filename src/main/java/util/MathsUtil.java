/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Sule
 */
public class MathsUtil {

    /**
     * This method calculates logX of given value.
     *
     * @param x
     * @param base 2=LOG2, 4=LOG4 or 10=LOG10
     * @return logBase of given value
     */
    public static double log(double x, int base) {
        return ((double) Math.log(x) / (double) Math.log(base));
    }

    /**
     * This method calculate combinations for C(n,r).
     *
     * @param n
     * @param r
     * @return
     * @throws Exception
     */
    
    public static long calculateCombination(int n, int r) throws Exception {
        long score = 0;
        if (r == 0) {
            score = 1;
        }
        if (n >= r) {
            double upper = 1,
                    lower = 1;
            for (int i = n; i > n - r; i--) {
                upper = upper * i;
            }
            for (int i = r; i > 1; i--) {
                lower = lower * i;
            }
            score = (long) (upper / lower);
        } else {
            throw new Exception("Error! n >= r");
        }
        return score;

    }
}
