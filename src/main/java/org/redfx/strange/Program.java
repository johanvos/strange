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
 *
 * @author johan
 * @version $Id: $Id
 */
public class Program {
 
    private final int numberQubits;
    private Result result;
    private double[] initAlpha;

    private Qubit[] qubits;
    private final ArrayList<Step> steps = new ArrayList<>();

    // cache decomposedSteps
    private List<Step> decomposedSteps = null;

    /**
     * Create a Quantum Program and indicate how many qubits will be involved.
     * By default, all qubits are initialized to the |0 &gt; state. Steps can
     * be added via this constructor, but they can also be added later by calling
     * the {@link addStep} or {@link addSteps} methods.
     *
     * @param nQubits the amount of qubits that will be used in this program
     * @param moreSteps steps to add to the program
     */
    public Program(int nQubits, Step... moreSteps) {
        this.numberQubits = nQubits;
        this.qubits = new Qubit[nQubits];
        for (int i = 0; i < nQubits; i++) {
            this.qubits[i] = new Qubit();
        }
        this.initAlpha = new double[numberQubits];
        Arrays.fill(initAlpha, 1d);
        addSteps(moreSteps);
    }

    public Program(Qubit... qubits) {
        this.qubits = qubits;
        this.numberQubits = qubits.length;
        this.initAlpha = new double[numberQubits];
        Arrays.fill(initAlpha, 1d);
    }

    public Qubit[] getQubits() {
        return this.qubits;
    }

    /**
     * Initialize this qubit. The qubit will be in a state
     * \psi = \alpha |0 &gt; + \beta |1 &gt; .
     * Since \alpha^2 + \beta^2 should equals 1, only
     * \alpha is required.
     *
     * @param idx the index of the qubit to be initialized
     * @param alpha the alpha value of the qubit state.
     * @throws java.lang.IllegalArgumentException in case the index of the qubit is higher than the amount of qubits -1 .
     */
    public void initializeQubit(int idx, double alpha) {
        if (idx >= numberQubits) {
            throw new IllegalArgumentException("Can not initialize qubit "+
                    idx+" since we have only "+numberQubits+" qubits.");
        }
        this.initAlpha[idx] = alpha;
    }

    /**
     * This method allows to set the initial values of the probabilities of the qubits in this
     * program. By default, all qubits are in the |0 &gt; state when a Program is created.
     * However, for testing or demoes, it sometimes makes sense to show the behavior of an
     * application that starts with non-zero qubits.
     * 
     * @return an array of the initial probabilities that the qubits in this program are supposed to have.
     */
    public double[] getInitialAlphas() {
        return this.initAlpha;
    }

    /**
     * Adds a {@link Step} containing zero or more gates to the current program.
     * In case the {@link Step} contains an operation that would put a measured qubit into a potential superposition
     * again, an IllegalArgumentException is thrown.
     *
     * @param step the {@link Step} to be added to the program
     */
    public void addStep(Step step) {
        if (!ensureMeasuresafe( Objects.requireNonNull(step)) ) {
            throw new IllegalArgumentException ("Adding a superposition step to a measured qubit");
        }
        List<Gate> gates = step.getGates();
        for (Gate gate : gates) {
            System.err.println("gate = "+gate);
            Qubit candidate = gate.getQubit();
            if (candidate != null) {
                int ibq = getIndexByQubit(candidate);
                gate.setMainQubitIndex(ibq);
            } else {
                continue;
            }
            candidate = gate.getSecondQubit();
            if (candidate != null) {
                getIndexByQubit(candidate);
                gate.setAdditionalQubit(getIndexByQubit(candidate), 1);
            } else {
                continue;
            }
            candidate = gate.getThirdQubit();
            if (candidate != null) {
                getIndexByQubit(candidate);
                gate.setAdditionalQubit(getIndexByQubit(candidate), 2);
            } else {
                continue;
            }
        }
        step.setIndex(steps.size());
        step.setProgram(this);
        steps.add(step);
        this.decomposedSteps = null;
    }

    private int getIndexByQubit(Qubit q) {
        if (this.qubits == null) return -1;
        for (int i = 0; i < this.qubits.length; i++) {
            if (q.equals(this.qubits[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds multiple steps with one or more gates to the existing program.
     * In case the Step contains an operation that would put a measured qubit into a potential superposition
     * again, an IllegalArgumentException is thrown.
     *
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
    
    /**
     * Return the list of {@link Step}s in this program. Steps are added either via the
     * constructor or via the {@link addStep} or {@link addSteps} methods.
     *
     * @return a list of {@link Step}s.
     */
    public List<Step> getSteps() {
        return this.steps;
    }

    /**
     * <p>Getter for the field <code>decomposedSteps</code>.</p>
     *
     * @return a {@link java.util.List} object
     */
    @Deprecated
    public List<Step> getDecomposedSteps () {
        return this.decomposedSteps;
    }

    /**
     * <p>Setter for the field <code>decomposedSteps</code>.</p>
     *
     * @param ds a {@link java.util.List} object
     */
    @Deprecated
    public void setDecomposedSteps(List<Step> ds) {
        this.decomposedSteps = ds;
    }
    
    /**
     * Return the number of @{link Qubit}s in this program.
     *
     * @return an int containing the number of qubits in this program.
     */
    public int getNumberQubits() {
        return this.numberQubits;
    }

    /**
     * Sets the {@link Result} of the execution of the program. This method is
     * invoked by the {@link QuantumExecutionEnvironment} when it has executed
     * this program. It should not be called by the developer.
     *
     * @param r a {@link org.redfx.strange.Result} object
     */
    public void setResult(Result r) {
        this.result = r;
    }

    /**
     * Returns the {@link Result} object of a program, after it has been
     * executed by a {@link QuantumExecutionEnvironment}.
     *
     * @return a {@link org.redfx.strange.Result} object
     */
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
        steps.forEach(step -> {
            System.out.println("Step: "+step.getGates());
        });
        System.out.println("==========================");
    }
    
}
