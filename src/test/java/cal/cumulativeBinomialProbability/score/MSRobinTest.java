/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cal.cumulativeBinomialProbability.score;

import junit.framework.TestCase;

/**
 *
 * @author Sule
 */
public class MSRobinTest extends TestCase {

    public MSRobinTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of calculateScore method, of class MSRobin.
     */
    public void testCalculateScore() {
        System.out.println("calculateScore");
        double probability = 0.01,
                intensity_part = 0.5;
        int N = 20,
                n = 3;
        MSRobin instance = new MSRobin(probability, N, n, intensity_part, 0);
        instance.calculateScore();
        assertEquals(21.20, instance.getScore(), 0.01);

        probability = 0.1;
        intensity_part = 0.78;
        N = 20;
        n = 8;
        instance = new MSRobin(probability, N, n, intensity_part, 0);
        instance.calculateScore();
        assertEquals(29.86, instance.getScore(), 0.01);

        N = 20;
        n = 3;
        probability = 0.01;
        intensity_part = 0.5;
        instance = new MSRobin(probability, N, n, intensity_part, 1);
        instance.calculateScore();
        assertEquals(14.99, instance.getScore(), 0.01);

        probability = 0.1;
        intensity_part = 0.78;
        N = 20;
        n = 8;
        instance = new MSRobin(probability, N, n, intensity_part, 1);
        instance.calculateScore();
        assertEquals(26.37, instance.getScore(), 0.01);

    }

}
