package org.redfx.strange;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author johan
 */
public class ResultTest {

    double DELTA = 0.0001;

    @Test
    void calculateOneQubitStatesFromVectorTest() {
        Complex[] prob = new Complex[2];
        prob[0] = Complex.ONE;
        prob[1] = Complex.ZERO;
        double[] res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(1, res.length);
        assertEquals(0, res[0], DELTA);
        prob[1] = Complex.ONE;
        prob[0] = Complex.ZERO;
        res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(1, res[0], DELTA);
        prob[0] = Complex.I;
        prob[1] = Complex.ZERO;
        res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(0, res[0], DELTA);
        prob[1] = Complex.I;
        prob[0] = Complex.ZERO;
        res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(1, res[0], DELTA);
        prob[0] = Complex.HC;
        prob[1] = Complex.HC;
        res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(.5, res[0], DELTA);
    }

    @Test
    void calculateTwoQubitStatesFromVectorTest() {
        Complex[] prob = new Complex[4];
        prob[0] = Complex.ONE;
        prob[1] = Complex.ZERO;
        prob[2] = Complex.ZERO;
        prob[3] = Complex.ZERO;
        double[] res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(2, res.length);
        assertEquals(0, res[0], DELTA);
        assertEquals(0, res[1], DELTA);
        prob[0] = Complex.ZERO;
        prob[1] = Complex.ZERO;
        prob[2] = Complex.HC;
        prob[3] = new Complex(.5,.5);
        res = Result.calculateQubitStatesFromVector(prob);
        assertEquals(.5, res[0], DELTA);
        assertEquals(1, res[1], DELTA);
    }
}
