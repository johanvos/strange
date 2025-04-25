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

import java.util.ArrayList;
import java.util.List;
import org.redfx.strange.Block;
import org.redfx.strange.Complex;
import org.redfx.strange.QuantumExecutionEnvironment;
import org.redfx.strange.Step;
import org.redfx.strange.local.Computations;

/**
 * <p>InvFourier class.</p>
 *
 * @author alain
 * @version $Id: $Id
 */
public class InvFourier extends Fourier {
    
    /**
     * Create a Fourier gate with the given size (dimensions), starting at idx
     *
     * @param size number of qubits affected by this gate
     * @param idx the index of the first qubit in the circuit affected by this gate
     */
    public InvFourier(int size, int idx) {
        super("InvFourier", size, idx);
    }
    
    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }
    
    /** {@inheritDoc} */
    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment eqq) {
        if (matrix == null) {
            double omega = Math.PI*2/size;
            double den = Math.sqrt(size);
            matrix = new Complex[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    double alpha = omega *i *j;
                    int tpd = (int)(alpha/(Math.PI * 2));
                    if (tpd > 0) {
                        alpha = alpha - (Math.PI*2 * tpd);
                    }
                    double ar = Math.cos(alpha);
                    double ai = Math.sin(alpha);
                    if (Math.abs(alpha) < 1e-6) {
                        ar = 1;
                        ai = 0;
                    }else if (Math.abs(Math.PI - alpha) < 1e-6) {
                        ar = -1;
                        ai = 0;
                    } else if (Math.abs(Math.PI / 2 - alpha) < 1e-6) {
                        ar = 0;
                        ai = 1;
                    } else if (Math.abs(3 * Math.PI / 2 - alpha) < 1e-6) {
                        ar = 0;
                        ai = -1;
                    }
                    matrix[i][j] = new Complex(ar/den, -1*ai/den);
                }
                for (int k = 0; k < i; k++) {
                    matrix[i][k] = matrix[k][i];
                }
            }
        }
        return matrix;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean hasOptimization() {
        return true;
    }
    
    @Override
    public Block createBlock(boolean inverse) {
        int length = (int) Math.ceil(Math.log(size) / Math.log(2));
        Block answer = new Block("InvFourier", length);
        for (Step step : getSubSteps()) {
            answer.addStep(step);
        }
        return answer;
    }

    @Override
    public List<Step> getSubSteps() {
            int length = (int) Math.ceil(Math.log(size) / Math.log(2));
        List<Step> answer = new ArrayList<>();
        for (int i = dim -1; i >=0; i--) {
            Step step = new Step(new Hadamard(i));
            answer.add(step);
            for (int j = 2; j <= i+1; j++) {
                step = new Step(new Cr(i+1-j, i, 2,-j));
                answer.add(step);
            }
        }
        for (int i = 0; i < length/2;i++) {
            Step step = new Step(new Swap(0,length-1-i));
            answer.add(step);
        }
        return answer;
    }
 
    @Override
    public Complex[] applyOptimize(Complex[] v) {
        int length = (int) Math.ceil(Math.log(size) / Math.log(2));
        System.err.println("[INVFOURIER] start, probs are now: ");
        Complex.printArray(v);
        for (int i = dim - 1; i >= 0; i--) {
            v = Computations.simpleNextProb(new Hadamard(i), v);
            System.err.println("after a H, we have ");
            Complex.printArray(v);
            for (int j = 2; j <= i+1; j++) {
                v = Computations.simpleNextProb(new Cr(i+1-j, i, 2,-j), v);
            }
        }
        for (int i = 0; i < length/2;i++) {
            v = Computations.simpleNextProb(new Swap(0,length-1-i), v);
        }
        System.err.println("[INVFOURIER] done, probs are now: ");
        Complex.printArray(v);
        return v;
    }
}
