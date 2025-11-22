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
package org.redfx.strange.gate;

import org.redfx.strange.Complex;
import org.redfx.strange.ControlledGate;
import org.redfx.strange.Gate;
import org.redfx.strange.Qubit;

/**
 * <p>Cnot class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class Cnot extends TwoQubitGate implements ControlledGate {
    
    private Gate rootGate;
    private int rootGateIndex;
    private int controlIndex;

    Complex[][] matrix =  new Complex[][]{
        {Complex.ONE,Complex.ZERO,Complex.ZERO,Complex.ZERO},
        {Complex.ZERO,Complex.ONE,Complex.ZERO,Complex.ZERO},
        {Complex.ZERO,Complex.ZERO,Complex.ZERO,Complex.ONE},
        {Complex.ZERO,Complex.ZERO,Complex.ONE,Complex.ZERO}
    };

    /**
     * <p>Constructor for Cnot.</p>
     *
     * @param controlQubitIndex the index of the control qubit
     * @param mainQubitIndex the index of the qubit we want to hit with a NOT gate
     */
    public Cnot (int controlQubitIndex, int mainQubitIndex) {
        super(controlQubitIndex, mainQubitIndex);
        this.rootGateIndex = mainQubitIndex;
        this.controlIndex = controlQubitIndex;
        this.rootGate = new X(mainQubitIndex);
    }

    public Cnot(Qubit controlQubit, Qubit targetQubit) {
        super(controlQubit, targetQubit);
        this.rootGate = new X(0);
    }

    @Override public int getSize() {return 1;}
    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix() {
        return matrix;
    }

    /** {@inheritDoc} */
    @Override public String getCaption() {
        return "Cnot";
    }

    @Override
    public int getControlQubitIndex() {
        return controlIndex;
    }

    @Override
    public int getRootGateIndex() {
        return rootGateIndex;
    }

    @Override
    public int getMainQubitIndex() {
        return this.rootGateIndex;
    }
    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        super.setAdditionalQubit(idx, cnt);
        setMainQubitIndex(idx);
    }
    @Override
    public Gate getRootGate() {
        return rootGate;
    }
}
