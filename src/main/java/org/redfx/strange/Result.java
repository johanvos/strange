/*-
 * #%L
 * Strange
 * %%
 * Copyright (C) 2020, 2021 Johan Vos
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Johan Vos nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.redfx.strange;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * <p>Result class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class Result {

    static final Logger LOG = Logger.getLogger(Result.class.getName());

    private int nqubits;
    private int nsteps;
    
    private Qubit[] qubits;
    private Complex[] probability;
    private Complex[][] intermediateProps;
    private Map<Integer, Qubit[]> intermediateQubits;
    private int measuredProbability = -1;
 
    /**
     * <p>Constructor for Result.</p>
     *
     * @param nqubits a int
     * @param steps a int
     */
    public Result(int nqubits, int steps) {
        assert(steps >= 0);
        LOG.info("Create result for "+nsteps+" steps and "+ nqubits+" qubits");
        this.nqubits = nqubits;
        this.nsteps = steps;
        intermediateProps = new Complex[steps > 0 ? steps : 1][];
        intermediateQubits = new HashMap<>();
    }

    /**
     * <p>Constructor for Result.</p>
     *
     * @param q an array of {@link org.redfx.strange.Qubit} objects
     * @param p an array of {@link org.redfx.strange.Complex} objects
     */
    public Result(Qubit[] q, Complex[] p) {
        this.qubits = q;
        this.probability = p;
    }
    
    /**
     * <p>Getter for the field <code>qubits</code>.</p>
     *
     * @return an array of {@link org.redfx.strange.Qubit} objects
     */
    public Qubit[] getQubits() {
        if (this.qubits == null) {
            this.qubits = calculateQubits();
        }
        return this.qubits;
    }

    /**
     * <p>Getter for the field <code>intermediateQubits</code>.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<Integer, Qubit[]> getIntermediateQubits() {
        return this.intermediateQubits;
    }
    
    private Qubit[] calculateQubits() {
        Qubit[] answer = new Qubit[nqubits];
        if (nqubits == 0) {
            return answer;
        }
        int lastidx = nsteps-1;
        while (intermediateProps[lastidx] == null) lastidx--;
        double[] d = calculateQubitStatesFromVector(intermediateProps[lastidx]);
        for (int i = 0; i < answer.length; i++) {
            answer[i] = new Qubit();
            answer[i].setProbability(d[i]);
        }
        return answer;
    }
    
    private Qubit[] calculateQubitsFromVector(Complex[] probs) {
        Qubit[] answer = new Qubit[nqubits];
        if (nqubits == 0) {
            return answer;
        }
        double[] d = calculateQubitStatesFromVector(probs);
        for (int i = 0; i < answer.length; i++) {
            answer[i] = new Qubit();
            answer[i].setProbability(d[i]);
        }
        return answer;
    }
    
    /**
     * <p>Getter for the field <code>probability</code>.</p>
     *
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public Complex[] getProbability() {
        return this.probability;
    }
    
    /**
     * <p>setIntermediateProbability.</p>
     *
     * @param step a int
     * @param p an array of {@link org.redfx.strange.Complex} objects
     */
    public void setIntermediateProbability(int step, Complex[] p) {
        this.intermediateProps[step] = p;
    //    this.intermediateQubits.put(step, calculateQubitsFromVector(p));
    //    if ((step == nsteps -1) || (nsteps == 0)) { // in case we have no steps, this is the final result
            this.probability = p;
     //   }
    }

    /**
     * <p>getIntermediateProbability.</p>
     *
     * @param step a int
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public Complex[] getIntermediateProbability(int step) {
        int ret = step;
        while ((ret > 0) && (intermediateProps[ret] == null)) ret--;
        return intermediateProps[ret];
    }
static ForkJoinPool customPool = new ForkJoinPool(8);

    public static double[] calculateQubitStatesFromVector(Complex[] vectorresult) {
        try {
            int nq = (int) Math.round(Math.log(vectorresult.length) / Math.log(2));
            double[] answer = new double[nq];
            double[] pabs = new double[vectorresult.length];
            for (int i = 0; i < pabs.length; i++) {
                pabs[i] = vectorresult[i].abssqr();
            }
            customPool.submit(() ->
                    IntStream.range(0, nq).parallel().forEach(i -> {
                        for (int j = 0; j < 1 << nq; j++) {
                            if (!hasZeroBit(j, i)) {
                                answer[i] = answer[i] + pabs[j];
                            }
                        }
                    })).get();
            return answer;
        } catch (InterruptedException | ExecutionException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
    public static boolean hasZeroBit(int a, int b) {
        if (b < 0) return false;
        int res = a & (1 << b);
        return (res == 0);
    }
    /**
     * Based on the probabilities of the system, this method will measure all qubits.
     * When this method is called, the <code>measuredValue</code> value of every qubit
     * contains a possible measurement value. The values are consistent for the entire system.
     * (e.g. when an entangled pair is measured, its values are equal)
     * However, different invocations of this method may result in different values.
     */
    public void measureSystem() {
        if (this.qubits == null) {
            this.qubits = getQubits();
        }
        double random = Math.random();
        int ressize = 1 << nqubits;
        double[] probamp = new double[ressize];
        double probtot = 0;
        // we don't need all probabilities, but we might use this later
        for (int i = 0; i < ressize; i++) {
            probamp[i] = this.probability[i].abssqr();
        }
        int sel = 0;
        probtot = probamp[0];
        while (probtot < random) {
            sel++;
            probtot = probtot + probamp[sel];
        }
        this.measuredProbability = sel;
        double outcome = probamp[sel];
        for (int i = 0; i < nqubits; i++) {
            qubits[i].setMeasuredValue(sel %2 == 1);
            sel = sel/2;
        }
    }

    /**
     * Returns a measurement based on the probability vector
     *
     * @return an integer representation of the measurement
     */
    public int getMeasuredProbability() {
        return measuredProbability;
    }

    /**
     * Print info about this result to stdout
     */
    public void printInfo() {
        System.out.println("Info about Quantum Result");
        System.out.println("==========================");
        System.out.println("Number of qubits = "+nqubits+", number of steps = "+nsteps);
        for (int i = 0; i < probability.length;i++) {
            System.out.println("Probability on "+i+":"+ probability[i].abssqr());
        }
        System.out.println("==========================");
    }
}
