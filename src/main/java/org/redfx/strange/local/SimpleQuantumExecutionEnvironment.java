/*-
 * #%L
 * Strange
 * %%
 * Copyright (C) 2020 Johan Vos
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
package org.redfx.strange.local;

import org.redfx.strange.*;
import org.redfx.strange.gate.Identity;
import org.redfx.strange.gate.PermutationGate;
import org.redfx.strange.gate.ProbabilitiesGate;
import org.redfx.strange.gate.Swap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>SimpleQuantumExecutionEnvironment class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class SimpleQuantumExecutionEnvironment implements QuantumExecutionEnvironment {

    static Logger LOG = Logger.getLogger(SimpleQuantumExecutionEnvironment.class.getName());

    /**
     * <p>Constructor for SimpleQuantumExecutionEnvironment.</p>
     */
    public SimpleQuantumExecutionEnvironment() {
    }

    /** {@inheritDoc} */
    @Override
    public Result runProgram(Program p) {
        LOG.info("runProgram ");
        LOG.info("LogLevel: " + LOG.getLevel());
        LOG.info("Logname: " + LOG.getName());
        LOG.info("LOG = "+LOG);
        LOG.info("FINE LOGGER? " + LOG.isLoggable(Level.FINE));
        int nQubits = p.getNumberQubits();
        Qubit[] qubit = new Qubit[nQubits];
        for (int i = 0; i < nQubits; i++) {
            qubit[i] = new Qubit();
        }
        int dim = 1 << nQubits;
        double[] initalpha = p.getInitialAlphas();
        Complex[] probs = new Complex[dim];
        for (int i = 0; i < dim; i++) {
            probs[i] = Complex.ONE;
            for (int j = 0; j < nQubits; j++) {
                int pw = nQubits - j -1 ;
                int pt = 1 << pw;
                int div = i/pt;
                int md = div % 2;
                if (md == 0) {
                    probs[i] = probs[i].mul(initalpha[j]);
                } else {
                    probs[i] = probs[i].mul(Math.sqrt(1-initalpha[j]*initalpha[j]));
                }
            }
        }
        List<Step> steps = p.getSteps();
        List<Step> simpleSteps = p.getDecomposedSteps();
        if (simpleSteps == null) {
            simpleSteps = new ArrayList<>();
            for (Step step : steps) {
                simpleSteps.addAll(Computations.decomposeStep(step, nQubits));
            }
            p.setDecomposedSteps(simpleSteps);
        }
        Result result = new Result(nQubits, steps.size());
        int cnt = 0;
        result.setIntermediateProbability(0, probs);
        LOG.fine("START RUN, number of steps = " + simpleSteps.size());
        int cntr = 0;
        for (Step step : simpleSteps) {
                            long s0 = System.currentTimeMillis();

            if (!step.getGates().isEmpty()) {
                LOG.finer("RUN STEP " + step + ", cnt = " + cnt);
                cnt++;
                LOG.finest("before this step, probs = ");
          //      printProbs(probs);
                probs = applyStep(step, probs, qubit);
                LOG.finest("after this step, probs = "+probs);
            //    printProbs(probs);
                int idx = step.getComplexStep();
                // System.err.println("complex? "+idx);
                if (idx > -1) {
                    result.setIntermediateProbability(idx, probs);
                }
            }
                long s1 = System.currentTimeMillis();
                System.err.println("STEP "+ cntr+"("+step+") took "+ (s1 -s0));
                cntr++;
        }
        LOG.info("DONE RUN, probability vector = " + probs);
        printProbs(probs);
        double[] qp = calculateQubitStatesFromVector(probs);
        for (int i = 0; i < nQubits; i++) {
            qubit[i].setProbability(qp[i]);
        }
        result.measureSystem();
        p.setResult(result);
        return result;
    }
    
    /** {@inheritDoc} */
    @Override
    public void runProgram(Program p, Consumer<Result> result) {
        Thread t = new Thread(() -> result.accept(runProgram(p)));
        t.start();
    }

    private void printProbs(Complex[] p) {
        Complex.printArray(p);
    }


    private List<Step> decomposeSteps(List<Step> steps) {
        return steps;
    }
    
    private Complex[]  applyStep (Step step, Complex[] vector, Qubit[] qubits) {
        LOG.finer("start applystep, vectorsize = "+vector.length+", ql = "+qubits.length);
        long s0 = System.currentTimeMillis();
        List<Gate> gates = step.getGates();
        if (!gates.isEmpty() && gates.get(0) instanceof ProbabilitiesGate ) {
            ProbabilitiesGate probGate = (ProbabilitiesGate)gates.get(0);
            probGate.setProbabilites(vector);
            return vector;
        }
        if (gates.size() == 1 && gates.get(0) instanceof PermutationGate) {
            PermutationGate pg = (PermutationGate)gates.get(0);
            return Computations.permutateVector (vector, pg.getIndex1(), pg.getIndex2());
        }
      
        Complex[] result = new Complex[vector.length];
        boolean vdd = true;
        if (vdd) {
            result = Computations.calculateNewState(gates, vector, qubits.length);
        } else {
            LOG.finest("start calcstepmatrix with gates " + gates);
            Complex[][] a = calculateStepMatrix(gates, qubits.length);
            LOG.finest("done calcstepmatrix");
            LOG.finest("vector");
            // printProbs(vector);
            if (a.length != result.length) {
                System.err.println("fatal issue calculating step for gates " + gates);
                throw new RuntimeException("Wrong length of matrix or probability vector: expected " + result.length + " but got " + a.length);
            }
            LOG.finest("start matrix-vector multiplication for vector size = " + vector.length);
            for (int i = 0; i < vector.length; i++) {
                result[i] = Complex.ZERO;
                for (int j = 0; j < vector.length; j++) {
                    result[i] = result[i].add(a[i][j].mul(vector[j]));
                }
            }
        }
        long s1 = System.currentTimeMillis();
        LOG.finer("done applystep took "+ (s1-s0));

        return result;
    }
    
    private Complex[][] calculateStepMatrix(List<Gate> gates, int nQubits) {
        return Computations.calculateStepMatrix(gates, nQubits, this);

    }

    // replaced by the similar function on Complex
    /**
     * <p>tensor.</p>
     *
     * @param a an array of {@link org.redfx.strange.Complex} objects
     * @param b an array of {@link org.redfx.strange.Complex} objects
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    @Deprecated
    public Complex[][] tensor(Complex[][] a, Complex[][] b) {
        int d1 = a.length;
        int d2 = b.length;
        Complex[][] result = new Complex[d1 * d2][d1 * d2];
        for (int rowa = 0; rowa < d1; rowa++) {
            for (int cola = 0; cola < d1; cola++) {
                for (int rowb = 0; rowb < d2; rowb++) {
                    for (int colb = 0; colb < d2; colb++) {
                        result[d2 * rowa + rowb][d2 * cola + colb] = a[rowa][cola].mul( b[rowb][colb]);
                    }
                }
            }
        }
        return result;
    }

    private double[] calculateQubitStatesFromVector(Complex[] vectorresult) {
        int nq = (int) Math.round(Math.log(vectorresult.length) / Math.log(2));
        double[] answer = new double[nq];
        int ressize = 1 << nq;
        for (int i = 0; i < nq; i++) {
            int pw = i;//nq - i - 1;
            int div = 1 << pw;
            for (int j = 0; j < ressize; j++) {
                int p1 = j / div;
                if (p1 % 2 == 1) {
                    answer[i] = answer[i] + vectorresult[j].abssqr();
                }
            }
        }
        return answer;
    }
    
    /**
     * <p>createPermutationMatrix.</p>
     *
     * @param first a int
     * @param second a int
     * @param n a int
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public Complex[][] createPermutationMatrix(int first, int second, int n) {
        Complex[][] swapMatrix = new Swap().getMatrix();
        Complex[][] iMatrix = new Identity().getMatrix();
        Complex[][] answer = iMatrix;
        int i = 1;
        if (first == 0) {
            answer = swapMatrix;
            i++;
        }
        while (i < n) {
            if (i == first) {
                i++;
                answer = tensor(answer, swapMatrix);
            } else {
                answer = tensor(answer, iMatrix);
            }
            i++;
        }
        return answer;
    }

    
    
}
