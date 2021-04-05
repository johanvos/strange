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
import org.redfx.strange.local.Computations;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * A Gate describes an operation on one or more qubits.
 * @author johan
 * @param <T> type of the Block
 */
public class ControlledBlockGate<T> extends BlockGate {

    private int control;
    private int size;
    private int high = -1;
    private int haq = -1;
    int low = 0;
    private Complex[][] matrix = null;
    
    protected ControlledBlockGate() {
    }
    
    /**
     * Create a controlled blockgate
     * @param bg the block gate
     * @param idx the start-index of the block gate
     * @param control the index of the control qubit
     */
    public ControlledBlockGate(BlockGate bg, int idx, int control) {
        this (bg.getBlock(), idx, control);
    }
    
    /**
     * Create a block 
     * @param block
     * @param idx
     * @param control the control qubit
     */
    public ControlledBlockGate (Block block, int idx, int control) {
        super(block, idx);
        this.control = control;
        if (control > idx) {
            this.haq = idx + block.getNQubits();
        } else {
            this.haq = idx+block.getNQubits() -1;
        }
    }

    @Override
    public List<Integer> getAffectedQubitIndexes() {
        ArrayList answer = new ArrayList(super.getAffectedQubitIndexes());
        answer.add(control);
        return answer;
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        if (high < 0) calculateHighLow();
        return this.haq;
    }

    @Override
    public String getCaption() {
        return "CB";
    }

    @Override
    public String getName() {
        return "CBlockGate";
    }

    @Override
    public String getGroup() {
        return "CBlockGroup";
    }
    
    public int getControlQubit() {
        return this.control;
    }

    public void calculateHighLow() {
        this.high = control;
        int gap = control - idx;
        int bs = block.getNQubits();
        low = 0;
         if (control > idx) {
             low =idx;
                if (gap < bs) {
                    throw new IllegalArgumentException("Can't have control at " + control + " for gate with size " + bs + " starting at " + idx);
                }
                if (gap > bs) {
                    high = control;
                }
            } else {
                low = control;
                high = idx + bs - 1;
         }
         size = high - low + 1;
    }
    
    public int getLow() {
        return this.low;
    }
    
    public void correctHigh(int h) {
        this.high = h;
    }
    
    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }

    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        if (matrix == null) {
            int low = 0;
            this.high = control;
            this.size = super.getSize() + 1;
            int gap = control - idx;
            List<PermutationGate> perm = new LinkedList<>();
            int bs = block.getNQubits();
            if (control > idx) {
                if (gap < bs) {
                    throw new IllegalArgumentException("Can't have control at " + control + " for gate with size " + bs + " starting at " + idx);
                }
                low = idx;
                if (gap > bs) {
                    high = control;
                    size = high - low + 1;
                    PermutationGate pg = new PermutationGate(control-low, control-low - gap + bs, size);
                    perm.add(pg);
                }
            } else {
                low = control;
                high = idx + bs - 1;
                size = high - low + 1;
                for (int i = 0; i < size - 1; i++) {
                    PermutationGate pg = new PermutationGate(i, i + 1, size);
                    perm.add(0,pg);
                }
            }
            Complex[][] part = block.getMatrix(qee);
          
            int dim = part.length;
            matrix = Computations.createIdentity(2 * dim);
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    matrix[i + dim][j + dim] = part[i][j];
                }
            }
        } else {
            System.err.println("Matrix was cached");
        }
        return matrix;
    }
    
    @Override
    public boolean hasOptimization() {
        return true;
    }

    @Override
    public Complex[] applyOptimize(Complex[] v) {
  //       System.err.println("APTOPTIMIZE CBG");
      //  Thread.dumpStack();
        int size = v.length;
        
        System.err.println("APTOPTIMIZE CBG, size = "+size);
        Complex[] answer = new Complex[size];
        int dim = size / 2;
        Complex[] oldv = new Complex[dim];
        for (int i = 0; i < dim; i++) {
            oldv[i] = v[i + dim];
        }
        Complex[] p2 = block.applyOptimize(oldv, inverse);
        for (int i = 0; i < dim; i++) {
            answer[i] = v[i];
            answer[dim + i] = p2[i];
        }
        return answer;
    }

    public int getSize() {
        return block.getNQubits()+1;
    }
    
    @Override public String toString() {
        return "ControlledGate for "+super.toString();
    }
    
}
