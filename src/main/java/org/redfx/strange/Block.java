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
package org.redfx.strange;

import org.redfx.strange.gate.PermutationGate;
import org.redfx.strange.gate.ProbabilitiesGate;
import org.redfx.strange.local.Computations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Block class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class Block {

    private List<Step> steps = new ArrayList<>();
    private final int nqubits;
    private Complex[][] matrix = null;
    private final String name;

    /**
     * Create a block spanning size qubits
     *
     * @param size the number of (adjacent) qubits in this block
     */
    public Block(int size) {
        this("anonymous", size);
    }
    
    /**
     * Create a named block spanning size qubits
     *
     * @param name the name of the block
     * @param size the number of (adjacent) qubits in this block
     */
    public Block(String name, int size) {
        this.nqubits = size;
        this.name = name;
    }

    /**
     * <p>addStep.</p>
     *
     * @param step a {@link org.redfx.strange.Step} object
     */
    public void addStep(Step step) {
        this.steps.add(step);
        matrix = null;
    }

    /**
     * <p>Getter for the field <code>steps</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    public List<Step> getSteps() {
        return this.steps;
    }

    /**
     * <p>getNQubits.</p>
     *
     * @return a int
     */
    public int getNQubits() {
        return this.nqubits;
    }

    private void validateGate(Gate gate) {
        gate.getAffectedQubitIndexes().stream().filter((idx) -> (idx > nqubits - 1)).
                forEachOrdered(item -> {
                    throw new IllegalArgumentException("Can't add a gate with qubit index larger than block size");
                });
    }

    Complex[][] getMatrix() {
        return getMatrix(null);
    }

    Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        if (matrix == null) {
            matrix = Complex.identityMatrix(1 << nqubits);
            List<Step> simpleSteps = new ArrayList<>();
            for (Step step : steps) {
                simpleSteps.addAll(Computations.decomposeStep(step, nqubits,0));
            }
            Collections.reverse(simpleSteps);

            for (Step step : simpleSteps) {
                List<Gate> gates = step.getGates();
                if ((matrix != null) && (gates.size() == 1) && (gates.get(0) instanceof PermutationGate)) {
                    matrix = Complex.permutate((PermutationGate) gates.get(0), matrix);
                } else {
                    Complex[][] m = Computations.calculateStepMatrix(step.getGates(), nqubits, qee);
                    if (matrix == null) {
                        matrix = m;
                    } else {
                        if (qee != null) {
                            matrix = qee.mmul(matrix, m);
                        } else {
                            matrix = Complex.mmul(matrix, m);
                        }
                    }
                }
            }
        }
        return matrix;
    }

    /**
     * <p>applyOptimize.</p>
     *
     * @param probs an array of {@link org.redfx.strange.Complex} objects
     * @param inverse a boolean
     * @return an array of {@link org.redfx.strange.Complex} objects
     */
    public Complex[] applyOptimize(Complex[] probs, boolean inverse) {
        for (Step step : steps) {
            System.err.println("STEP " + step);
            List<Gate> gates = step.getGates();
            probs = Computations.getNextProbability(gates, probs);
        }
        if (1 < 2) return probs;

        
        
        
        
        
        
        List<Step> simpleSteps = new ArrayList<>();
        for (Step step : steps) {
            simpleSteps.addAll(Computations.decomposeStep(step, nqubits,0));
        }
        if (inverse) {
            Collections.reverse(simpleSteps);
            for (Step step : simpleSteps) {
                step.setInverse(true);
            }
        }
        for (Step step : simpleSteps) {
            if (!step.getGates().isEmpty()) {
                probs = applyStep(step, probs);
            }
        }
        if (inverse) {
            for (Step step : simpleSteps) {
                step.setInverse(true);
            }
        }
        return probs;
    }

    private Complex[] applyStep(Step step, Complex[] vector) {
        long s0 = System.currentTimeMillis();
        List<Gate> gates = step.getGates();
        if (!gates.isEmpty() && gates.get(0) instanceof ProbabilitiesGate) {
            return vector;
        }
        if (gates.size() == 1 && gates.get(0) instanceof PermutationGate) {
            PermutationGate pg = (PermutationGate) gates.get(0);
            return Computations.permutateVector(vector, pg.getIndex1(), pg.getIndex2());
        }

        Complex[] result = new Complex[vector.length];
        result = Computations.calculateNewState(gates, vector, nqubits);
        long s1 = System.currentTimeMillis();
        return result;

    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Block named " + name + " at " + super.toString();
    }
}
