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

/**
 * <p>Cz class.</p>
 *
 * @author johan
 * @version $Id: $Id
 */
public class Cz extends TwoQubitGate implements ControlledGate {
        
    int controlQubit;
    int rootGateIndex;
    
    Complex[][] matrix =  new Complex[][]{
        {Complex.ONE,Complex.ZERO,Complex.ZERO,Complex.ZERO},
        {Complex.ZERO,Complex.ONE,Complex.ZERO,Complex.ZERO},
        {Complex.ZERO,Complex.ZERO,Complex.ONE,Complex.ZERO},
        {Complex.ZERO,Complex.ZERO,Complex.ZERO,Complex.ONE.mul(-1)}
    };
    
//    /**
//     * <p>Constructor for Cz.</p>
//     */
//    public Cz() {    
//    }
//    
    /**
     * <p>Constructor for Cz.</p>
     *
     * @param a a int
     * @param b a int
     */
    public Cz (int a, int b) {
        super(a,b);
        this.controlQubit = a;
        this.rootGateIndex = b;
    }

    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix() {
        return matrix;
    }

    /** {@inheritDoc} */
    @Override public String getCaption() {
        return "Cz";
    }
    
    
    @Override
    public int getControllQubitIndex() {
        return this.controlQubit;
    }

    @Override
    public Gate getRootGate() {
        return new Z(0);
    }

    @Override
    public int getRootGateIndex() {
        return this.rootGateIndex;
    }

    @Override
    public void shift(int n) {
        super.shift(n);
        this.controlQubit = controlQubit +n;
        this.rootGateIndex = rootGateIndex +n;
    }

}
