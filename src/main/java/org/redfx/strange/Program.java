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

import org.redfx.strange.gate.Cnot;
import org.redfx.strange.gate.Hadamard;
import org.redfx.strange.gate.Measurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * A Quantum Program.
 * A Program contains a list of <code>Step</code>s that are executed sequentially 
 * by a QuantumExecutionEnvironment.
 * @author johan
 */
public class Program {
 
    private final int numberQubits;
    private Result result;
    private double[] initAlpha;

    private final ArrayList<Step> steps = new ArrayList<>();

    // cache decomposedSteps
    private List<Step> decomposedSteps = null;

    /**
     * Create a Quantum Program and indicate how many qubits will be involved.
     * By default, all qubits are initialized to the |0 &gt; state.
     * @param nQubits the amount of qubits that will be used in this program
     * @param moreSteps steps to add to the program
     */
    public Program(int nQubits, Step... moreSteps) {
        this.numberQubits = nQubits;
        this.initAlpha = new double[numberQubits];
        Arrays.fill(initAlpha, 1d);
        addSteps(moreSteps);
    }

    /**
     * Initialize this qubit. The qubit will be in a state
     * \psi = \alpha |0 &gt; + \beta |1 &gt; .
     * Since \alpha^2 + \beta^2 should equals 1, only
     * \alpha is required.
     * @param idx the index of the qubit to be initialized
     * @param alpha the alpha value of the qubit state.
     * @throws IllegalArgumentException in case the index of the qubit is higher than the amount of qubits -1 .
     */
    public void initializeQubit(int idx, double alpha) {
        if (idx >= numberQubits) {
            throw new IllegalArgumentException("Can not initialize qubit "+
                    idx+" since we have only "+numberQubits+" qubits.");
        }
        this.initAlpha[idx] = alpha;
    }

    public double[] getInitialAlphas() {
        return this.initAlpha;
    }

    /**
     * Adds a step with one or more gates to the existing program.
     * In case the Step contains an operation that would put a measured qubit into a potential superposition
     * again, an IllegalArgumentException is thrown.
     * @param step the step to be added to the program
     */
    public void addStep(Step step) {
        if (!ensureMeasuresafe( Objects.requireNonNull(step)) ) {
            throw new IllegalArgumentException ("Adding a superposition step to a measured qubit");
        }
        step.setIndex(steps.size());
        step.setProgram(this);
        steps.add(step);
        this.decomposedSteps = null;
    }

    /**
     * Adds multiple steps with one or more gates to the existing program.
     * In case the Step contains an operation that would put a measured qubit into a potential superposition
     * again, an IllegalArgumentException is thrown.
     * @param moreSteps steps to be added to the program
     */
    public void addSteps(Step... moreSteps) {
       for ( Step step: moreSteps) {
           addStep(step);
       }
    }

    private boolean ensureMeasuresafe(Step newStep) {

        // determine which qubits might get superpositioned
        List<Integer> mainQubits = new ArrayList<>();
        for (Gate g : newStep.getGates()) {
            if (g instanceof Hadamard) {
                mainQubits.add(g.getMainQubitIndex());
            } else if (g instanceof Cnot) {
                mainQubits.add(((Cnot) g).getSecondQubitIndex());
            }
        }
        for (Step step : this.getSteps()) {
            boolean match = step.getGates().stream().filter(g -> g instanceof Measurement)
                    .map(Gate::getMainQubitIndex).anyMatch(mainQubits::contains);
            if (match) return false;
        }
        ;
        return true;
    }
    
    public List<Step> getSteps() {
        return this.steps;
    }

    public List<Step> getDecomposedSteps () {
        return this.decomposedSteps;
    }

    public void setDecomposedSteps(List<Step> ds) {
        this.decomposedSteps = ds;
    }
    
    public int getNumberQubits() {
        return this.numberQubits;
    }

    public void setResult(Result r) {
        this.result = r;
    }

    public Result getResult() {
        return this.result;
    }

    /**
     * Print info about this program to stdout
     */
    public void printInfo() {
        System.out.println("Info about Quantum Program");
        System.out.println("==========================");
        System.out.println("Number of qubits = "+numberQubits+", number of steps = "+steps.size());
        for (Step step: steps) {
            System.out.println("Step: "+step.getGates());
        }
        System.out.println("==========================");
    }
    
}
