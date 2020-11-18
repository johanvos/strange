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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * A single Step in a quantum <code>Program</code>. In a step, a number
 * of Gates can be added, involving a number of qubits. However, in a single
 * step a qubit can be involved in at most one <code>Gate</code>. It is illegal
 * to declare 2 gates in a single step that operate on the same qubit.
 * @author johan
 */
public class Step {
    
    private final ArrayList<Gate> gates = new ArrayList<>();
    private int index;
    private final String name;

    private Program program;
    
    private int complexStep = -1; // if a complex step needs to broken into
    // simple steps, only one simple step can have this value to be the index of the complex step

    private boolean informal = false;

    public Step(Gate... moreGates ) {
        this("unknown", moreGates);
    }

    public Step(String name, Gate... moreGates ) {
        this.name = name;
        addGates(moreGates);
    }

    /**
     * Return the name of this step. This is for descriptive information only, it has no impact on the
     * computations
     * @return the name of the step, if supplied by the user.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Add gate to the list of gates for this step
     * @param gate gate to add
     * @throws IllegalArgumentException in case the supplied Gate operates on a qubit that is already
     * been operated on in this step
     */
    public void addGate(Gate gate) throws IllegalArgumentException {
        verifyUnique(Objects.requireNonNull(gate));
        gates.add(gate);
    }

    /**
     * Adds the multiple Gates to the list of gates for this step
     * @param moreGates more gates
     * @throws IllegalArgumentException in case the supplied Gate operates on a qubit that is already
     * been operated on in this step
     */
    public void addGates(Gate... moreGates) throws IllegalArgumentException {
        for( Gate g: moreGates ){
            addGate(g);
        }
    }

    public List<Gate> getGates() {
        return Collections.unmodifiableList(gates);
    }
    
    public void setComplexStep(int idx) {
        this.complexStep = idx;
    }
    
    public int getComplexStep() {
        return this.complexStep;
    }

    public void setInformalStep(boolean b) {
        this.informal = b;
    }

    public boolean isInformal() {
        return informal;
    }

    public void setIndex(int s) {
        this.index = s;
        this.complexStep = s;
    }
    
    public int getIndex() {
        return this.index;
    }

    public void setProgram(Program p) {
        this.program = p;
    }

    public Program getProgram() {
        return this.program;
    }

    public void inverse() {
        for (Gate g: gates) {
            g.inverse();
        }
    }
    
    private void verifyUnique (Gate gate) {
        for (Gate g: gates) {
            long overlap = g.getAffectedQubitIndexes().stream().filter(gate.getAffectedQubitIndexes()::contains).count();
            if (overlap > 0) throw new IllegalArgumentException("Adding gate that affects a qubit already involved in this step");
        }
    }

    @Override public String toString() {
        return "Step with gates "+this.gates;
    }

}
