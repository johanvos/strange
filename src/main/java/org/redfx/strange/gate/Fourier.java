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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.redfx.strange.QuantumExecutionEnvironment;

public class Fourier extends BlockGate {

    protected Complex[][] matrix = null;
    protected int dim;
    protected int size;
    
    /**
     * Create a Fourier gate with the given size (dimensions), starting at idx
     * @param dim number of qubits affected by this gate
     * @param idx the index of the first qubit in the circuit affected by this gate
     */
    public Fourier(int dim, int idx) {
        super(new Block("Fourier", dim), idx);
        this.dim = dim;
        this.size = 1 <<dim;
      //  System.err.println("Created a fourier block");
    }
    
    
    
    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }
    
    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment eqq) {
      //  System.err.println("Get fourier matrix");
        //Thread.dumpStack();

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

    @Override public Fourier inverse() {
        Complex[][] m = getMatrix();
        this.matrix = Complex.conjugateTranspose(m);
        return this;
    }
    
    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return IntStream.range(idx, idx+dim).boxed().collect(Collectors.toList());
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        return dim+idx-1;
    }
    
        @Override
    public boolean hasOptimization() {
        return false;
    }
}
