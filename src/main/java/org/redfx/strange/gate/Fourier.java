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

import org.redfx.strange.Block;
import org.redfx.strange.BlockGate;
import org.redfx.strange.Complex;
import org.redfx.strange.QuantumExecutionEnvironment;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.redfx.strange.local.Computations;

/**
 * <p>Fourier class.</p>
 *
 * @author alain
 * @version $Id: $Id
 */
public class Fourier extends BlockGate {

    protected Complex[][] matrix = null;
    protected int dim;
    protected int size;
    
    /**
     * Create a Fourier gate with the given size (dimensions), starting at idx
     *
     * @param dim number of qubits affected by this gate
     * @param idx the index of the first qubit in the circuit affected by this gate
     */
    public Fourier(int dim, int idx) {
        this("Fourier", dim, idx);
    }
    
    /**
     * <p>Constructor for Fourier.</p>
     *
     * @param name a {@link java.lang.String} object
     * @param dim a int
     * @param idx a int
     */
    public Fourier(String name, int dim, int idx) {
         super(new Block(name, dim), idx);
        this.dim = dim;
        this.size = 1 <<dim;
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
                    matrix[i][j] = new Complex(Math.cos(alpha)/den, Math.sin(alpha)/den);
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
    public void setInverse(boolean v) {
        if (v) {
            Complex[][] m = getMatrix();
            this.matrix = Complex.conjugateTranspose(m);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Fourier inverse() {
        Complex[][] m = getMatrix();
        this.matrix = Complex.conjugateTranspose(m);
        return this;
    }
    
    /** {@inheritDoc} */
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return IntStream.range(idx, idx+dim).boxed().collect(Collectors.toList());
    }

    /** {@inheritDoc} */
    @Override
    public int getHighestAffectedQubitIndex() {
        return dim+idx-1;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean hasOptimization() {
        return true;
    }

    @Override
    public Complex[] applyOptimize(Complex[] v) {
        System.err.println("[FOURIER] start, probs are now: ");
        Complex.printArray(v);
        int length = (int) Math.ceil(Math.log(size) / Math.log(2));
        for (int i = dim -1; i >=0; i--) {
            v = Computations.simpleNextProb(new Hadamard(i), v);
            for (int j = 2; j <= i+1; j++) {
                v = Computations.simpleNextProb(new Cr(i+1-j, i, 2,j), v);
            }
        }
        for (int i = 0; i < length/2;i++) {
            v = Computations.simpleNextProb(new Swap(0,length-1-i), v);
        }
        System.err.println("[FOURIER] done, probs are now: ");
        Complex.printArray(v);
        return v;
    }
    
    
}
