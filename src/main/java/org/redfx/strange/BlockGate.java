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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * A Gate describes an operation on one or more qubits.
 * @author johan
 */
public class BlockGate<T> implements Gate {

    protected Block block;
    protected int idx;
    private boolean inverse = false;
    
    protected BlockGate() {
    }
    
    /**
     * Create a block 
     * @param block
     * @param idx
     */
    public BlockGate (Block block, int idx) {
        this.block = block;
        this.idx = idx;
    }
    
    protected final void setBlock(Block b) {
        this.block = b;
    }
    
    public final Block getBlock() {
        return this.block;
    }
    
    protected final void setIndex(int idx) {
        this.idx = idx;
    }
    
    @Override
    public void setMainQubitIndex(int idx) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getMainQubitIndex() {
        return idx;
    }

    @Override
    public void setAdditionalQubit(int idx, int cnt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Integer> getAffectedQubitIndexes() {
        return IntStream.range(idx, idx+block.getNQubits()).boxed().collect(Collectors.toList());
    }

    @Override
    public int getHighestAffectedQubitIndex() {
        int answer = block.getNQubits()+idx-1;
        return answer;
    }

    @Override
    public String getCaption() {
        return "B";
    }

    @Override
    public String getName() {
        return "BlockGate";
    }

    @Override
    public String getGroup() {
        return "BlockGroup";
    }

    @Override
    public Complex[][] getMatrix() {
        return getMatrix(null);
    }
    
    @Override
    public Complex[][] getMatrix(QuantumExecutionEnvironment qee) {
        Complex[][] answer = block.getMatrix(qee);
        if (inverse) {
            answer = Complex.conjugateTranspose(answer);
        }
        return answer;
    }
    
    public T inverse() {
        this.inverse = !inverse;
        return (T)this;
    }
    
    @Override
    public int getSize() {
        return block.getNQubits();
    }
        
    @Override
    public boolean hasOptimization() {
        return true;
    }

    @Override
    public Complex[] applyOptimize(Complex[] v) {
        System.err.println("APPLYOPT on blockgate ");
        Complex[][] matrix = getMatrix();
        int size = v.length;
        Complex[] answer = new Complex[size];
        for (int i = 0; i < size; i++) {
            answer[i] = Complex.ZERO;
            for (int j = 0; j < size; j++) {
                answer[i] = answer[i].add(matrix[i][j].mul(v[j]));
            }
        }
        return answer;
    }

    @Override public String toString() {
        return "Gate for block "+block+", size = "+getSize();
    }

    
}
